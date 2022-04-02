package org.controlSoftware.data;

public class NegativeNumberException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NegativeNumberException(String message) {
		super(message);
	}

	public NegativeNumberException() {
		super("negative number is not valid");
	}
}
