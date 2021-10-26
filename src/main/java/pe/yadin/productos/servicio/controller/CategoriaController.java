package pe.yadin.productos.servicio.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.yadin.productos.servicio.model.documents.Categoria;
import pe.yadin.productos.servicio.model.dtos.ResponseDto;
import pe.yadin.productos.servicio.model.services.IProductoService;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/categorias")
public class CategoriaController {

	@Autowired
	private IProductoService productoService;

	@GetMapping({ "", "/" })
	public Mono<ResponseEntity<ResponseDto<List<Categoria>>>> listar() {
		return productoService.obtenerCategorias().collectList()
				.map(lista -> new ResponseEntity<>(ResponseDto.response(lista, "/categorias/", HttpStatus.OK.value(), "", ""), HttpStatus.OK));
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<ResponseDto<Categoria>>> obtener(@PathVariable String id) {
		return productoService.obtenerCategoria(id)
				.map(c -> new ResponseEntity<>(
						ResponseDto.response(c, "/categorias/".concat(id), HttpStatus.OK.value(), "", ""),
						HttpStatus.OK))
				.switchIfEmpty(
						Mono.just(new ResponseEntity<>(
								ResponseDto.response(null, "/categorias", HttpStatus.NOT_FOUND.value(),
										"Categoria no encontrada.", "Error en ID d categoría."),
								HttpStatus.NOT_FOUND)));
	}

	@PostMapping({"","/"})
	public Mono<ResponseEntity<ResponseDto<Categoria>>> guardar(@Valid @RequestBody Categoria categoria) {
		return productoService.obtenerCategoriaPorDescripcion(categoria.getDescripcion()).map(c -> {
			return new ResponseEntity<>(ResponseDto.response(c, "/productos/categorias/guardar-categoria",
					HttpStatus.NOT_ACCEPTABLE.value(), "Error al guardar categoría.",
					"La categoría ingresada ya se encuentra registrada."), HttpStatus.NOT_ACCEPTABLE);
		}).switchIfEmpty(productoService.guardar(categoria)
				.map(c -> new ResponseEntity<>(
						ResponseDto.response(categoria, "/productos/categorias/guardar-categoria",
								HttpStatus.CREATED.value(), "Categoria creada correctamente", null),
						HttpStatus.CREATED)))
				.onErrorResume(error -> Mono.just(new ResponseEntity<>(
						ResponseDto.response(categoria, "/productos/categorias/guardar-categoria",
								HttpStatus.NOT_ACCEPTABLE.value(), "Error al guardar categoría.", error.getMessage()),
						HttpStatus.NOT_ACCEPTABLE)));
	}

	@PutMapping({"","/"})
	public Mono<ResponseEntity<ResponseDto<Categoria>>> modificar(@Valid @RequestBody Categoria categoria) {
		return productoService.obtenerCategoria(categoria.getId()).flatMap(c -> {
			if (categoria.getDescripcion().equalsIgnoreCase(c.getDescripcion())) {
				return productoService.guardar(categoria)
						.map(cat -> new ResponseEntity<>(
								ResponseDto.response(cat, "/produtos/categoria/guardar-categoria",
										HttpStatus.CREATED.value(), "Categoria guardada correctamente.", ""),
								HttpStatus.CREATED));
			} else {
				return productoService.obtenerCategoriaPorDescripcion(categoria.getDescripcion())
						.map(cat -> new ResponseEntity<>(
								ResponseDto.response(categoria, "/productos/categorias/guardar-categoria",
										HttpStatus.NOT_ACCEPTABLE.value(), "Error al guardar categoría.",
										"La caregoría ingresada ya se encuentra registrada."),
								HttpStatus.NOT_ACCEPTABLE))
						.switchIfEmpty(productoService.guardar(categoria)
								.map(ct -> new ResponseEntity<>(
										ResponseDto.response(ct, "/produtos/categoria/guardar-categoria",
												HttpStatus.CREATED.value(), "Categoria guardada correctamente.", ""),
										HttpStatus.CREATED)));
			}
		}).switchIfEmpty(
				Mono.just(new ResponseEntity<>(ResponseDto.response(categoria, "/produtos/categoria/guardar-categoria",
						HttpStatus.NOT_FOUND.value(), "Categoria no encontrada.", "Error al actualizar categoría"),
						HttpStatus.NOT_FOUND)))
				.onErrorResume(error -> Mono.just(new ResponseEntity<>(ResponseDto.response(categoria,
						"/productos/categorias/guardar-categoria", HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Error al guardar categoria.", error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR)));
	}

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<ResponseDto<Object>>> eliminarCategoria(@PathVariable String id) {
		return productoService.obtenerCategoria(id)
				.map(c -> new ResponseEntity<>(
						ResponseDto.response(null, "/produtos/categoria/eliminar-categoria",
								HttpStatus.NO_CONTENT.value(), "Categoría eliminada correctamente.", ""),
						HttpStatus.NO_CONTENT))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(ResponseDto.response(null, "/produtos/categoria/eliminar-categoria",
						HttpStatus.NOT_FOUND.value(), "Categoria no encontrada.", "Error al eliminar categoría"),
						HttpStatus.NOT_FOUND)));
	}

}
