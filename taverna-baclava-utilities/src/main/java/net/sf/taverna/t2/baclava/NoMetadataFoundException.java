/**
 * This file is a component of the Taverna project,
 * and is licensed under the GNU LGPL.
 * Copyright Tom Oinn, EMBL-EBI
 */
package net.sf.taverna.t2.baclava;

/**
 * Thrown when metadata is requested for an object, no empty metadata element is
 * defined and there is no data stored.
 * 
 * @author Tom Oinn
 */
public class NoMetadataFoundException extends RuntimeException {

	public NoMetadataFoundException() {
		super();
	}

	public NoMetadataFoundException(String message) {
		super(message);
	}

	public NoMetadataFoundException(String message, Exception cause) {
		super(message, cause);
	}

	public NoMetadataFoundException(Exception cause) {
		super(cause);
	}

}
