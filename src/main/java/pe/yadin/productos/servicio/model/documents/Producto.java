package pe.yadin.productos.servicio.model.documents;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "productos")
public class Producto {

	@Id
	private String id;

	@NotNull(message = "Categoría no puede ser inválida.")
	private Categoria categoria;

	@NotEmpty(message = "Producto debe tener una descripción.")
	private String descripcion;

	@NotNull(message = "Producto debe tener un precio.")
	@DecimalMin(value = "0.1", inclusive = true, message = "Valor del precio inválido.")
	private Double precio;

	private String imagen;

	private Integer cantidad;

	private Boolean estado;

	public Producto() {
	}

	public Producto(Categoria categoria, String descripcion, Double precio, String imagen, Integer cantidad,
			Boolean estado) {
		this.categoria = categoria;
		this.descripcion = descripcion;
		this.precio = precio;
		this.imagen = imagen;
		this.cantidad = cantidad;
		this.estado = estado;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

}
