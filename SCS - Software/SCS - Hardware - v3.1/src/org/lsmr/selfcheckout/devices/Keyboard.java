package org.lsmr.selfcheckout.devices;

import org.lsmr.selfcheckout.IllegalConfigurationPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalErrorPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalPhaseSimulationException;
import org.lsmr.selfcheckout.NullPointerSimulationException;
import org.lsmr.selfcheckout.devices.observers.KeyboardObserver;

/**
 * Represents a device with a set of keys that permit manual entry of text. This
 * simulation does not model the individual keys, as that would be excessively
 * complicated to implement and to use.
 */
public class Keyboard extends AbstractDevice<KeyboardObserver> {
	/**
	 * A simple simulation of someone typing on the keyboard. This method is for
	 * convenience, so that {@link #pressKey(char)} need not be called multiple
	 * times.
	 * 
	 * @param s
	 *            A string whose characters represent keystrokes.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public void type(String s) {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(s == null)
			throw new NullPointerSimulationException("s");

		for(char c : s.toCharArray())
			pressKey(c);
	}

	/**
	 * A simple simulation of someone typing on the keyboard. This method is for
	 * convenience, so that {@link #pressKey(char)} need not be called multiple
	 * times.
	 * 
	 * @param characters
	 *            An array of characters representing keystrokes.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public void type(char[] characters) {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(characters == null)
			throw new NullPointerSimulationException("characters");

		for(char c : characters)
			pressKey(c);
	}

	/**
	 * A simple simulation of someone pressing a key on the keyboard. Announces
	 * "keystroke" event.
	 * 
	 * @param character
	 *            A character representing a keystroke.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public void pressKey(char character) {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		notifyKeystroke(character);
	}

	private void notifyKeystroke(char c) {
		for(KeyboardObserver l : observers)
			l.keyPressed(this, c);
	}
}
