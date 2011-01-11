package uk.org.taverna.scufl2.api.io;

public class ReaderException extends Exception {

	public ReaderException() {
	}

	public ReaderException(String message) {
		super(message);
	}

	public ReaderException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReaderException(Throwable cause) {
		super(cause);
	}

}
