package org.lsmr.selfcheckout.devices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simulates the station used by the attendant.
 * <p>
 * A supervisor station possesses:
 * <ul>
 * <li>one touch screen; and,</li>
 * <li>one QWERTY keyboard.</li>
 * </ul>
 * </p>
 * <p>
 * All other functionality of the supervisor station must be performed in
 * software. A given self-checkout station can be supervised by at most one
 * supervision station.
 * </p>
 */
public class SupervisionStation {
	public final TouchScreen screen;
	public final Keyboard keyboard;

	private final ArrayList<SelfCheckoutStation> supervisedStations;

	/**
	 * Creates a supervisor station.
	 */
	public SupervisionStation() {
		screen = new TouchScreen();
		supervisedStations = new ArrayList<SelfCheckoutStation>();
		keyboard = new Keyboard();
	}

	/**
	 * Accesses the list of supervised self-checkout stations.
	 * 
	 * @return An immutable list of the self-checkout stations supervised by this
	 *             supervisor station.
	 */
	public List<SelfCheckoutStation> supervisedStations() {
		return Collections.unmodifiableList(supervisedStations);
	}

	public int supervisedStationCount() {
		return supervisedStations.size();
	}

	public void add(SelfCheckoutStation station) {
		if(station == null)
			throw new IllegalArgumentException("station cannot be null");
		if(station.isSupervised())
			throw new IllegalStateException("station is already supervised but cannot be");

		station.setSupervised(true);
		supervisedStations.add(station);
	}

	public boolean remove(SelfCheckoutStation station) {
		boolean result = supervisedStations.remove(station);

		if(result) {
			station.setSupervised(false);
		}

		return result;
	}
}
