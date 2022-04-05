package org.lsmr.selfcheckout.devices;

import org.lsmr.selfcheckout.IllegalErrorPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalPhaseSimulationException;

/**
 * A simple interface for devices that accept things.
 * 
 * @param <T>
 *            The type of the things to accept.
 */
public interface Acceptor<T> {
	/**
	 * Instructs the device to take the thing as input.
	 * 
	 * @param thing
	 *            The thing to be taken as input.
	 * @throws OverloadException
	 *             If the device does not have enough space for the thing.
	 * @throws DisabledException
	 *             If the device is disabled.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public void accept(T thing) throws OverloadException, DisabledException;

	/**
	 * Checks whether the device has enough space to expect one more thing. If this
	 * method returns true, an immediate call to accept should not throw
	 * CapacityExceededException, unless an asynchronous addition has occurred in
	 * the meantime.
	 * 
	 * @throws IllegalErrorPhaseSimulationException
	 *             If the device is in the error phase.
	 * @return true If there is space; otherwise, false.
	 */
	public boolean hasSpace();
}
