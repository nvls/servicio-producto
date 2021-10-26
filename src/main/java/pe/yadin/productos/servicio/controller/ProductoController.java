package pe.yadin.productos.servicio.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import pe.yadin.productos.servicio.model.documents.Producto;
import pe.yadin.productos.servicio.model.dtos.ResponseDto;
import pe.yadin.productos.servicio.model.services.IProductoService;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/productos")
public class ProductoController {

	@Autowired
	private IProductoService productoService;

	@Value("${config.upload.path}")
	private String pathImagenes;

	@GetMapping("/pagina/{pagina}/cantidad/{cantidad}")
	public Mono<ResponseEntity<ResponseDto<List<Producto>>>> listar(@PathVariable Integer pagina,
			@PathVariable Integer cantidad) {

		return productoService.obtenerProductos(pagina, cantidad).collectList()
				.zipWith(productoService.obtenerTotalProductos()).map(t -> {
					return new ResponseEntity<>(
							ResponseDto.response(t.getT1(), "/productos/", HttpStatus.OK.value(), "", "", t.getT2()),
							HttpStatus.OK);
				});
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<ResponseDto<Producto>>> obtener(@PathVariable String id) {
		return productoService.obtenerProducto(id)
				.map(producto -> new ResponseEntity<>(
						ResponseDto.response(producto, "/productos/".concat(id), HttpStatus.OK.value(), "", ""),
						HttpStatus.OK));
	}

	@PostMapping
	public Mono<ResponseEntity<ResponseDto<Producto>>> guardar(@Valid @RequestPart Producto producto, @RequestPart FilePart file) {

		return productoService.obtenerCategoria(producto.getCategoria().getId()).flatMap(categoria -> {
			producto.setCategoria(categoria);

			producto.setImagen(generarNombreImagen(file.filename()));

			return file.transferTo(new File(pathImagenes + producto.getImagen()))
					.then(productoService.guardar(producto));
		}).map(productoNuevo -> new ResponseEntity<>(ResponseDto.response(productoNuevo, "/productos",
				HttpStatus.CREATED.value(), "Producto creado correctamente.", null), HttpStatus.CREATED))
				.onErrorResume(
						error -> Mono
								.just(new ResponseEntity<>(
										ResponseDto.response(producto, "/productos/", HttpStatus.NOT_ACCEPTABLE.value(),
												"Error al crear Producto.", error.getMessage()),
										HttpStatus.NOT_ACCEPTABLE)))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(
						ResponseDto.response(producto, "/productos", HttpStatus.NOT_FOUND.value(),
								"Catgoría no válida.", "Error categoria ingresada, no existe."),
						HttpStatus.NOT_FOUND)));

	}
	
	@PostMapping("/sin-imagen")
	public Mono<ResponseEntity<ResponseDto<Producto>>> guardar(@Valid @RequestBody Producto producto) {

		return productoService.obtenerCategoria(producto.getCategoria().getId()).flatMap(categoria -> {
			producto.setCategoria(categoria);

			return productoService.guardar(producto);
		}).map(productoNuevo -> new ResponseEntity<>(ResponseDto.response(productoNuevo, "/productos",
				HttpStatus.CREATED.value(), "Producto creado correctamente.", null), HttpStatus.CREATED))
				.onErrorResume(
						error -> Mono
								.just(new ResponseEntity<>(
										ResponseDto.response(producto, "/productos/", HttpStatus.NOT_ACCEPTABLE.value(),
												"Error al crear Producto.", error.getMessage()),
										HttpStatus.NOT_ACCEPTABLE)))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(
						ResponseDto.response(producto, "/productos", HttpStatus.NOT_FOUND.value(),
								"Catgoría no válida.", "Error categoria ingresada, no existe."),
						HttpStatus.NOT_FOUND)));

	}

	@PutMapping("/{id}")
	public Mono<ResponseEntity<ResponseDto<Producto>>> actualizar(@PathVariable String id,
			@Valid @RequestPart Producto producto, @RequestPart FilePart file) {
		return productoService.obtenerProducto(id).flatMap(p -> {
			p.setCategoria(producto.getCategoria());
			p.setDescripcion(producto.getDescripcion());
			p.setPrecio(producto.getPrecio());
			p.setCantidad(producto.getCantidad());
			p.setEstado(producto.getEstado());
			if(p.getImagen() == null) {
				p.setImagen(generarNombreImagen(file.filename()));
			} else {
				eliminarImagenProducto(p.getImagen());
				p.setImagen(generarNombreImagen(file.filename()));
			}
			
			return file.transferTo(new File(pathImagenes + p.getImagen())).then(productoService.guardar(p))
			.map(pr -> {
				return new ResponseEntity<>(
						ResponseDto.response(pr, "/productos/" + pr.getId(), HttpStatus.OK.value(),
								"Producto actualizado correctamente.", "Producto actualizado correctamente."),
						HttpStatus.OK);
			}).onErrorResume(error -> {
				return Mono.just(new ResponseEntity<>(ResponseDto.response(producto, "/producto/",
						HttpStatus.NOT_ACCEPTABLE.value(), "Error al guardar producto", error.getMessage()),
						HttpStatus.NOT_ACCEPTABLE));
			});
		}).switchIfEmpty(
				Mono.just(new ResponseEntity<>(
						ResponseDto.response(producto, "/productos/", HttpStatus.NOT_FOUND.value(),
								"Error al actualizar Producto", "Producto ingresado no existe."),
						HttpStatus.NOT_FOUND)));
	}
	
	@PutMapping("/sin-imagen/{id}")
	public Mono<ResponseEntity<ResponseDto<Producto>>> actualizar(@PathVariable String id,
			@Valid @RequestBody Producto producto) {
		return productoService.obtenerProducto(id).flatMap(p -> {
			p.setCategoria(producto.getCategoria());
			p.setDescripcion(producto.getDescripcion());
			p.setPrecio(producto.getPrecio());
			p.setCantidad(producto.getCantidad());
			p.setEstado(producto.getEstado());
			
			return productoService.guardar(p)
			.map(pr -> {
				return new ResponseEntity<>(
						ResponseDto.response(pr, "/productos/" + pr.getId(), HttpStatus.OK.value(),
								"Producto actualizado correctamente.", "Producto actualizado correctamente."),
						HttpStatus.OK);
			}).onErrorResume(error -> {
				return Mono.just(new ResponseEntity<>(ResponseDto.response(producto, "/producto/",
						HttpStatus.NOT_ACCEPTABLE.value(), "Error al guardar producto", error.getMessage()),
						HttpStatus.NOT_ACCEPTABLE));
			});
		}).switchIfEmpty(
				Mono.just(new ResponseEntity<>(
						ResponseDto.response(producto, "/productos/", HttpStatus.NOT_FOUND.value(),
								"Error al actualizar Producto", "Producto ingresado no existe."),
						HttpStatus.NOT_FOUND)));
	}

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<ResponseDto<Producto>>> eliminar(@PathVariable String id) {
		return productoService.obtenerProducto(id).flatMap(producto -> {
			return productoService.eliminar(producto).map(p -> {
				eliminarImagenProducto(p.getImagen());
				return new ResponseEntity<>(ResponseDto.response(p, "/productos/", HttpStatus.NO_CONTENT.value(),
						"Producto eliminado correctamente.", ""), HttpStatus.OK);
			});
		}).switchIfEmpty(
				Mono.just(new ResponseEntity<>(ResponseDto.response(null, "/produtos/", HttpStatus.NOT_FOUND.value(),
						"Producto no encontrado.", "Error al eliminar producto"), HttpStatus.NOT_FOUND)));
	}

	@PostMapping("/imagen/{id}")
	public Mono<ResponseEntity<ResponseDto<Producto>>> subirImagen(@PathVariable String id,
			@RequestPart FilePart file) {
		return productoService.obtenerProducto(id).flatMap(p -> {
			
			if(p.getImagen() == null) {
				p.setImagen(generarNombreImagen(file.filename()));
			} else {
				eliminarImagenProducto(p.getImagen());
				p.setImagen(generarNombreImagen(file.filename()));
			}
			
			//p.setImagen(generarNombreImagen(file.filename()));
			return file.transferTo(new File(pathImagenes + p.getImagen())).then(productoService.guardar(p));
		}).map(p -> new ResponseEntity<>(ResponseDto.response(p, "/productos/" + p.getId().toString(),
				HttpStatus.OK.value(), "Imagen guardada correctamente", ""), HttpStatus.OK)).switchIfEmpty(
						Mono.just(
								new ResponseEntity<>(
										ResponseDto.response(null, "/productos/", HttpStatus.NOT_FOUND.value(),
												"Error al guardar imagen.", "El producto no existe."),
										HttpStatus.NOT_FOUND)));
	}

	@GetMapping("/imagen/{foto:.+}")
	public Mono<ResponseEntity<Resource>> imagen(@PathVariable String foto) {

		Path ruta = Paths.get(pathImagenes).resolve(foto).toAbsolutePath();
		Resource recurso;
		try {
			recurso = new UrlResource(ruta.toUri());
		} catch (MalformedURLException e) {
			return Mono.just(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
		}
		return Mono.just(new ResponseEntity<>(recurso, HttpStatus.OK));
	}
	
	public void eliminarImagenProducto(String imagen) {
		Path rutaImagen = Paths.get(pathImagenes).resolve(imagen).toAbsolutePath();
		File fileImagen = rutaImagen.toFile();
		if(fileImagen.exists() && fileImagen.canRead())
			fileImagen.delete();
	}
	
	public String generarNombreImagen(String nombre) {
		return UUID.randomUUID().toString() + "-" + nombre
				.replace(":","")
				.replace(" ","")
				.replace("\\", "");
	}

}
