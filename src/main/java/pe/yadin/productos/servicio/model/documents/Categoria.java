package pe.yadin.productos.servicio.model.documents;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categorias")
public class Categoria {

	@Id
	private String id;

	@NotBlank(message = "Descripción no debe ser vacía.")
	private String descripcion;

	public Categoria() {
	}

	public Categoria(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
