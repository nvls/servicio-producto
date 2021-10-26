package pe.yadin.productos.servicio.model.dtos;

import java.io.Serializable;
import java.util.Date;

public class ResponseDto<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private Date fecha;
	private T data;
	private String path;
	private Integer status;
	private String message;
	private String exception;
	private Long totalLista;

	public ResponseDto() {
	}

	public ResponseDto(Date fecha, T data, String path, Integer status, String message, String exception) {
		this.fecha = fecha;
		this.data = data;
		this.path = path;
		this.status = status;
		this.message = message;
		this.exception = exception;
	}
	
	public ResponseDto(T data, String path, Integer status, String message, String exception) {
		this.fecha = new Date();
		this.data = data;
		this.path = path;
		this.status = status;
		this.message = message;
		this.exception = exception;
	}
	
	public ResponseDto(T data, String path, Integer status, String message, String exception, Long totalLista) {
		this.fecha = new Date();
		this.data = data;
		this.path = path;
		this.status = status;
		this.message = message;
		this.exception = exception;
		this.totalLista = totalLista;
	}
	
	public static <T> ResponseDto<T> response(T data, String path, Integer status, String message, String exception) {
		return new ResponseDto<T>(data, path, status, message, exception);
	}
	
	public static <T> ResponseDto<T> response(T data, String path, Integer status, String message, String exception, Long cantidad) {
		return new ResponseDto<T>(data, path, status, message, exception, cantidad);
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public Long getTotalLista() {
		return totalLista;
	}

	public void setTotalLista(Long totalLista) {
		this.totalLista = totalLista;
	}
	
}
