package org.lsmr.selfcheckout.devices;

/**
 * Represents the situation when a device is emptied but an attempt is made to
 * remove something from it.
 */
public class EmptyException extends Exception {
	private static final long serialVersionUID = 3566954386000387724L;

	/**
	 * Default constructor.
	 */
	public EmptyException() {}

	/**
	 * Constructor taking a message.
	 * 
	 * @param message
	 *            The detail message to communicate.
	 */
	public EmptyException(String message) {
		super(message);
	}
}
