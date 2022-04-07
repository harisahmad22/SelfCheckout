package org.lsmr.selfcheckout;

/**
 * An exception that can be raised when a method is called during an
 * inappropriate phase.
 */
@SuppressWarnings("serial")
public abstract class IllegalPhaseSimulationException extends SimulationException {
	/**
	 * Basic constructor.
	 * 
	 * @param message
	 *            The message describing the problem.
	 */
	protected IllegalPhaseSimulationException(String message) {
		super(message);
	}
}
