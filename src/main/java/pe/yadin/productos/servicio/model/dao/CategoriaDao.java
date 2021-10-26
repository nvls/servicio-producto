package pe.yadin.productos.servicio.model.dao;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import pe.yadin.productos.servicio.model.documents.Categoria;
import reactor.core.publisher.Mono;

public interface CategoriaDao extends ReactiveMongoRepository<Categoria, String> {
	
	@Query("{'descripcion' : ?0}")
	public Mono<Categoria> findCategoriaByDescripcion(String descripcion);

}
