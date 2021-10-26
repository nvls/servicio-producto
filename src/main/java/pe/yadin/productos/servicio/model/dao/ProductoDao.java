package pe.yadin.productos.servicio.model.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import pe.yadin.productos.servicio.model.documents.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoDao extends ReactiveSortingRepository<Producto, String> {
	
	Flux<Producto> findByIdNotNull(Pageable page);
	
	Mono<Void> deleteById(String id);

}
