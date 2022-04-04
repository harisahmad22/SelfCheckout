package org.lsmr.selfcheckout.devices;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.IllegalConfigurationPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalErrorPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalNormalPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalPhaseSimulationException;
import org.lsmr.selfcheckout.InvalidArgumentSimulationException;
import org.lsmr.selfcheckout.NullPointerSimulationException;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.observers.BanknoteDispenserObserver;

/**
 * Represents a device that stores banknotes (as known as bills, paper money,
 * etc.) of a particular denomination to dispense them as change.
 * <p>
 * Banknote dispensers can receive banknotes from other sources. To simplify the
 * simulation, no check is performed on the value of each banknote.
 * </p>
 */
public final class BanknoteDispenser extends AbstractDevice<BanknoteDispenserObserver>
	implements FromStorageEmitter<Banknote> {
	private int maxCapacity;
	private Queue<Banknote> queue = new LinkedList<Banknote>();
	private UnidirectionalChannel<Banknote> sink;

	/**
	 * Creates a banknote dispenser with the indicated maximum capacity.
	 * 
	 * @param capacity
	 *            The maximum number of banknotes that can be stored in the
	 *            dispenser. Must be positive.
	 * @throws SimulationException
	 *             If capacity is not positive.
	 */
	public BanknoteDispenser(int capacity) {
		if(capacity <= 0)
			throw new InvalidArgumentSimulationException("Capacity must be positive: " + capacity);

		this.maxCapacity = capacity;
	}

	/**
	 * Accesses the current number of banknotes in the dispenser.
	 * 
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 * @return The number of banknotes currently in the dispenser.
	 */
	public int size() {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		return queue.size();
	}

	/**
	 * Allows a set of banknotes to be loaded into the dispenser directly. Existing
	 * banknotes in the dispenser are not removed. Announces "banknotesLoaded"
	 * event.
	 * 
	 * @param banknotes
	 *            A sequence of banknotes to be added. Each may not be null.
	 * @throws OverloadException
	 *             if the number of banknotes to be loaded exceeds the capacity of
	 *             the dispenser.
	 * @throws SimulationException
	 *             If any banknote is null.
	 * @throws IllegalErrorPhaseSimulationException
	 *             If the device is in the error phase.
	 */
	public void load(Banknote... banknotes) throws OverloadException {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();

		if(maxCapacity < queue.size() + banknotes.length)
			throw new OverloadException("Capacity of dispenser is exceeded by load");

		for(Banknote banknote : banknotes)
			if(banknote == null)
				throw new NullPointerSimulationException("banknote instance");
			else
				queue.add(banknote);

		notifyBanknotesLoaded(banknotes);
	}

	/**
	 * Unloads banknotes from the dispenser directly. Announces "banknotesUnloaded"
	 * event.
	 * 
	 * @throws IllegalErrorPhaseSimulationException
	 *             If the device is in the error phase.
	 * @return A list of the banknotes unloaded. May be empty. Will never be null.
	 */
	public List<Banknote> unload() {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();

		List<Banknote> result = new ArrayList<>(queue);
		queue.clear();

		notifyBanknotesUnoaded(result.toArray(new Banknote[result.size()]));

		return result;
	}

	/**
	 * Connects an output channel to this banknote dispenser. Any existing output
	 * channels are disconnected. Causes no events to be announced.
	 * 
	 * @param sink
	 *            The new output device to act as output. Can be null, which leaves
	 *            the channel without an output.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the configuration phase.
	 */
	void connect(UnidirectionalChannel<Banknote> sink) {
		if(phase == Phase.NORMAL)
			throw new IllegalNormalPhaseSimulationException();
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();

		this.sink = sink;
	}

	/**
	 * Returns the maximum capacity of this banknote dispenser.
	 * 
	 * @return The capacity. Will be positive.
	 * @throws IllegalErrorPhaseSimulationException
	 *             If the device is in the error phase.
	 */
	public int getCapacity() {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();

		return maxCapacity;
	}

	/**
	 * Emits a single banknote from this banknote dispenser. If successful,
	 * announces "banknoteRemoved" event. If a successful banknote removal causes
	 * the dispenser to become empty, announces "banknotesEmpty" event.
	 * 
	 * @throws OverloadException
	 *             if the output channel is unable to accept another banknote.
	 * @throws EmptyException
	 *             if no banknotes are present in the dispenser to release.
	 * @throws DisabledException
	 *             if the dispenser is currently disabled.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public void emit() throws EmptyException, DisabledException, OverloadException {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(isDisabled())
			throw new DisabledException();

		if(queue.size() == 0)
			throw new EmptyException();

		Banknote banknote = queue.remove();

		if(sink.hasSpace())
			try {
				sink.deliver(banknote);
			}
			catch(OverloadException e) {
				// Should never happen
				phase = Phase.ERROR;
				throw e;
			}
		else
			throw new OverloadException("The sink is full.");

		notifyBanknoteRemoved(banknote);

		if(queue.isEmpty())
			notifyBanknotesEmpty();
	}

	private void notifyBanknoteRemoved(Banknote banknote) {
		for(BanknoteDispenserObserver observer : observers)
			observer.banknoteRemoved(this, banknote);
	}

	private void notifyBanknotesEmpty() {
		for(BanknoteDispenserObserver observer : observers)
			observer.banknotesEmpty(this);
	}

	private void notifyBanknotesLoaded(Banknote[] banknotes) {
		for(BanknoteDispenserObserver observer : observers)
			observer.banknotesLoaded(this, banknotes);
	}

	private void notifyBanknotesUnoaded(Banknote[] banknotes) {
		for(BanknoteDispenserObserver observer : observers)
			observer.banknotesUnloaded(this, banknotes);
	}
}
