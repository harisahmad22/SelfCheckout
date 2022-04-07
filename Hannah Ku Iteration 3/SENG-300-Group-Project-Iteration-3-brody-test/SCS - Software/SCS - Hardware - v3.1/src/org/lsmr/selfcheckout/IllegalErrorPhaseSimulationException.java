package org.lsmr.selfcheckout;

/**
 * An exception that can be raised when a method is called inappropriately
 * during the error phase.
 */
@SuppressWarnings("serial")
public class IllegalErrorPhaseSimulationException extends IllegalPhaseSimulationException {
	/**
	 * Default constructor.
	 */
	public IllegalErrorPhaseSimulationException() {
		super("This method may not be used when the device is in the error phase.");
	}
}
