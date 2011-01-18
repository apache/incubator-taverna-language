package uk.org.taverna.scufl2.api.io;

public class WriterException extends Exception {

	public WriterException() {
	}

	public WriterException(String message) {
		super(message);
	}

	public WriterException(String message, Throwable cause) {
		super(message, cause);
	}

	public WriterException(Throwable cause) {
		super(cause);
	}

}
