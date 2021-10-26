package pe.yadin.productos.servicio.model.services;

import pe.yadin.productos.servicio.model.documents.Categoria;
import pe.yadin.productos.servicio.model.documents.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductoService {

	public Flux<Producto> obtenerProductos(Integer pagina, Integer cantidad);
	
	public Mono<Long> obtenerTotalProductos();

	public Mono<Producto> obtenerProducto(String id);

	public Mono<Producto> guardar(Producto producto);

	public Mono<Producto> eliminar(Producto producto);
	
	public Flux<Categoria> obtenerCategorias();
	
	public Mono<Categoria> obtenerCategoria(String id);
	
	public Mono<Categoria> guardar(Categoria categoria);
	
	public Mono<Void> eliminar(Categoria categoria);
	
	public Mono<Categoria> obtenerCategoriaPorDescripcion(String descripcion);
}
