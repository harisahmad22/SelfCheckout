package org.lsmr.selfcheckout;

/**
 * An exception that can be raised when a method is called inappropriately
 * during the normal phase.
 */
@SuppressWarnings("serial")
public class IllegalNormalPhaseSimulationException extends IllegalPhaseSimulationException {
	/**
	 * Default constructor.
	 */
	public IllegalNormalPhaseSimulationException() {
		super("This method may not be used when the device is in the normal phase.");
	}
}
