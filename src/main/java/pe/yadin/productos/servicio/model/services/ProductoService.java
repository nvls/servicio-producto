package pe.yadin.productos.servicio.model.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pe.yadin.productos.servicio.model.dao.CategoriaDao;
import pe.yadin.productos.servicio.model.dao.ProductoDao;
import pe.yadin.productos.servicio.model.documents.Categoria;
import pe.yadin.productos.servicio.model.documents.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoService implements IProductoService {

	@Autowired
	private ProductoDao productoDao;
	
	@Autowired
	private CategoriaDao categoriaDao;

	@Override
	public Flux<Producto> obtenerProductos(Integer pagina, Integer cantidad) { 
		Pageable page = PageRequest.of(pagina, cantidad);
		//return productoDao.findAll();
		return productoDao.findByIdNotNull(page);
	}
	
	@Override 
	public Mono<Long> obtenerTotalProductos() {
		return productoDao.count();
	}

	@Override
	public Mono<Producto> obtenerProducto(String id) {
		return productoDao.findById(id);
	}

	@Override
	public Mono<Producto> guardar(Producto producto) {
		return productoDao.save(producto);
	}

	@Override
	public Mono<Producto> eliminar(Producto producto) {
		return productoDao.deleteById(producto.getId()).thenReturn(producto);
	}

	@Override
	public Flux<Categoria> obtenerCategorias() {
		return categoriaDao.findAll();
	}

	@Override
	public Mono<Categoria> obtenerCategoria(String id) {
		return categoriaDao.findById(id);
	}

	@Override
	public Mono<Categoria> guardar(Categoria categoria) {
		return categoriaDao.save(categoria);
	}

	@Override
	public Mono<Void> eliminar(Categoria categoria) {
		return categoriaDao.delete(categoria);
	}
	
	@Override
	public Mono<Categoria> obtenerCategoriaPorDescripcion(String descripcion) {
		return categoriaDao.findCategoriaByDescripcion(descripcion);
	}

}
