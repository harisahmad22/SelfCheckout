package org.lsmr.selfcheckout;

/**
 * An exception that can be raised when a method is called inappropriately
 * during the configuration phase.
 */
@SuppressWarnings("serial")
public class IllegalConfigurationPhaseSimulationException extends IllegalPhaseSimulationException {
	/**
	 * Default constructor.
	 */
	public IllegalConfigurationPhaseSimulationException() {
		super("This method may not be used when the device is in the configuration phase.");
	}
}
