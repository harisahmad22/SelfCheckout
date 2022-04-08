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
import org.lsmr.selfcheckout.devices.observers.CoinStorageUnitObserver;

/**
 * Represents devices that store coins. They only receive coins, not dispense
 * them. To access the coins inside, a human operator needs to physically remove
 * the coins, simulated with the {@link #unload()} method. A
 * {@link #load(Coin...)} method is provided for symmetry.
 */
public class CoinStorageUnit extends AbstractDevice<CoinStorageUnitObserver> implements Acceptor<Coin> {
	private Coin[] storage;
	private int nextIndex = 0;

	/**
	 * Creates a coin storage unit that can hold the indicated number of coins.
	 * 
	 * @param capacity
	 *            The maximum number of coins that the unit can hold.
	 * @throws SimulationException
	 *             If the capacity is not positive.
	 */
	public CoinStorageUnit(int capacity) {
		if(capacity <= 0)
			throw new InvalidArgumentSimulationException("The capacity must be positive.");

		storage = new Coin[capacity];
	}

	/**
	 * Gets the maximum number of coins that this storage unit can hold.
	 * 
	 * @return The capacity.
	 */
	public int getCapacity() {
		return storage.length;
	}

	/**
	 * Gets the current count of coins contained in this storage unit.
	 * 
	 * @return The current count.
	 */
	public int getCoinCount() {
		return nextIndex;
	}

	/**
	 * Allows a set of coins to be loaded into the storage unit directly. Existing
	 * coins in the dispenser are not removed. Announces "coinsLoaded" event.
	 * Disabling has no effect on loading/unloading.
	 * 
	 * @param coins
	 *            A sequence of coins to be added. Each cannot be null.
	 * @throws SimulationException
	 *             if the number of coins to be loaded exceeds the capacity of the
	 *             unit.
	 * @throws SimulationException
	 *             If coins is null.
	 * @throws SimulationException
	 *             If any coin is null.
	 * @throws OverloadException
	 *             If too many coins are loaded.
	 * @throws IllegalErrorPhaseSimulationException
	 *             If the device is in the error phase.
	 */
	public void load(Coin... coins) throws SimulationException, OverloadException {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();

		if(coins == null)
			throw new NullPointerSimulationException("coins");

		if(coins.length + nextIndex > storage.length)
			throw new OverloadException("You tried to stuff too many coins in the storage unit.");

		for(Coin coin : coins)
			if(coin == null)
				throw new NullPointerSimulationException("coin instance");

		System.arraycopy(coins, 0, storage, nextIndex, coins.length);
		nextIndex += coins.length;

		notifyCoinsLoaded();
	}

	/**
	 * Unloads coins from the storage unit directly. Announces "coinsUnloaded"
	 * event.
	 * 
	 * @return A list of the coins unloaded. May be empty. Will never be null.
	 * @throws IllegalErrorPhaseSimulationException
	 *             If the device is in the error phase.
	 */
	public List<Coin> unload() {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();

		List<Coin> coins = Arrays.asList(storage);

		storage = new Coin[storage.length];
		nextIndex = 0;
		notifyCoinsUnloaded();

		return coins;
	}

	/**
	 * Causes the indicated coin to be added to the storage unit. If successful,
	 * announces "coinAdded" event. If a successful coin addition instead causes the
	 * unit to become full, announces "coinsFull" event.
	 * 
	 * @throws DisabledException
	 *             If the unit is currently disabled.
	 * @throws SimulationException
	 *             If coin is null.
	 * @throws OverloadException
	 *             If the unit is already full.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public void accept(Coin coin) throws DisabledException, OverloadException {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(isDisabled())
			throw new DisabledException();

		if(coin == null)
			throw new NullPointerSimulationException("coin");

		if(nextIndex < storage.length) {
			storage[nextIndex++] = coin;

			notifyCoinAdded();

			if(nextIndex == storage.length)
				notifyCoinsFull();
		}
		else
			throw new OverloadException();
	}

	@Override
	public boolean hasSpace() {
		return nextIndex < storage.length;
	}

	private void notifyCoinsLoaded() {
		for(CoinStorageUnitObserver l : observers)
			l.coinsLoaded(this);
	}

	private void notifyCoinsUnloaded() {
		for(CoinStorageUnitObserver l : observers)
			l.coinsUnloaded(this);
	}

	private void notifyCoinsFull() {
		for(CoinStorageUnitObserver l : observers)
			l.coinsFull(this);
	}

	private void notifyCoinAdded() {
		for(CoinStorageUnitObserver l : observers)
			l.coinAdded(this);
	}
}
