package org.lsmr.selfcheckout.devices;

import java.util.Arrays;
import java.util.List;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.IllegalConfigurationPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalErrorPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalPhaseSimulationException;
import org.lsmr.selfcheckout.InvalidArgumentSimulationException;
import org.lsmr.selfcheckout.NullPointerSimulationException;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.observers.CoinTrayObserver;

/**
 * Simulates the tray where dispensed coins go for the user to collect them.
 */
public class CoinTray extends AbstractDevice<CoinTrayObserver> implements Acceptor<Coin> {
	private Coin[] coins;
	private int nextIndex = 0;

	/**
	 * Creates a coin tray.
	 * 
	 * @param capacity
	 *            The maximum number of coins that this tray can hold without
	 *            overflowing.
	 * @throws SimulationException
	 *             If the capacity is &le;0.
	 */
	public CoinTray(int capacity) {
		if(capacity <= 0)
			throw new InvalidArgumentSimulationException("capacity must be positive.");

		coins = new Coin[capacity];
	}

	/**
	 * Causes the indicated coin to be added to the tray. On success, announces
	 * "coinAdded" event.
	 * 
	 * @param coin
	 *            The coin to add.
	 * @throws SimulationException
	 *             If coin is null.
	 * @throws OverloadException
	 *             If the tray overflows.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public void accept(Coin coin) throws OverloadException, DisabledException {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(coin == null)
			throw new NullPointerSimulationException("coin");

		if(nextIndex < coins.length) {
			coins[nextIndex++] = coin;
			notifyCoinAdded();
		}
		else
			throw new OverloadException("The tray has overflowed.");
	}

	/**
	 * Simulates the act of physically removing coins from the try by a user.
	 * 
	 * @return The list of coins collected. May not be null. May be empty.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public List<Coin> collectCoins() {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		List<Coin> result = Arrays.asList(coins);

		coins = new Coin[coins.length];
		nextIndex = 0;

		return result;
	}

	/**
	 * Returns whether this coin receptacle has enough space to accept at least one
	 * more coin: always true. Causes no events.
	 */
	@Override
	public boolean hasSpace() {
		return nextIndex < coins.length;
	}

	private void notifyCoinAdded() {
		for(CoinTrayObserver l : observers)
			l.coinAdded(this);
	}
}
