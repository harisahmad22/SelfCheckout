package org.lsmr.selfcheckout.devices;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.IllegalConfigurationPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalErrorPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalNormalPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalPhaseSimulationException;
import org.lsmr.selfcheckout.NullPointerSimulationException;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.observers.CoinSlotObserver;

/**
 * Represents a simple coin slot device that has one output channel. The slot is
 * stupid: it has no functionality other than being enabled/disabled, and cannot
 * determine the value and currency of the coin.
 */
public final class CoinSlot extends AbstractDevice<CoinSlotObserver> implements Acceptor<Coin> {
	private UnidirectionalChannel<Coin> sink;

	/**
	 * Creates a coin slot.
	 */
	public CoinSlot() {}

	/**
	 * Connects channels to the coin slot. Causes no events.
	 * <p>
	 * This operation is permissible only during the configuration phase.
	 * 
	 * @param sink
	 *            Where coins will always be passed.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the configuration phase.
	 */
	void connect(UnidirectionalChannel<Coin> sink) {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.NORMAL)
			throw new IllegalNormalPhaseSimulationException();

		this.sink = sink;
	}

	/**
	 * Tells the coin slot that the indicated coin is being inserted. If the slot is
	 * enabled, announces "coinInserted" event.
	 * 
	 * @param coin
	 *            The coin to be added. Cannot be null.
	 * @throws DisabledException
	 *             If the coin slot is currently disabled.
	 * @throws OverloadException
	 *             If the sink has no space.
	 * @throws SimulationException
	 *             If coin is null.
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

		notifyCoinInserted();

		if(sink.hasSpace()) {
			try {
				sink.deliver(coin);
			}
			catch(OverloadException e) {
				// Should never happen
				phase = Phase.ERROR;
				throw e;
			}
		}
		else
			throw new OverloadException("Unable to route coin: Output channel is full");
	}

	@Override
	public boolean hasSpace() {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		return sink.hasSpace();
	}

	private void notifyCoinInserted() {
		for(CoinSlotObserver observer : observers)
			observer.coinInserted(this);
	}
}
