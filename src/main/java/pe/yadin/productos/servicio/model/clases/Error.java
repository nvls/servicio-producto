package pe.yadin.productos.servicio.model.clases;

public class Error {

	private String errorMessage;

	public Error(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
