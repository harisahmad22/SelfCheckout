package org.lsmr.selfcheckout.devices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.IllegalConfigurationPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalErrorPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalNormalPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalPhaseSimulationException;
import org.lsmr.selfcheckout.NullPointerSimulationException;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.observers.BanknoteSlotObserver;

/**
 * Represents a simple banknote slot device that can either accept a banknote or
 * eject the most recently inserted banknote, leaving it dangling until the
 * customer removes it, via {@link #removeDanglingBanknotes()}.
 */
public final class BanknoteSlot extends AbstractDevice<BanknoteSlotObserver>
	implements Acceptor<Banknote>, FlowThroughEmitter<Banknote> {
	private BidirectionalChannel<Banknote> sink;
	private boolean invert;

	/**
	 * Creates a banknote slot.
	 * 
	 * @param invert
	 *            If the slot is to work in reverse.
	 */
	public BanknoteSlot(boolean invert) {
		this.invert = invert;
	}

	/**
	 * Connects an output channel to the banknote slot. Causes no events. Disabling
	 * has no effect on this method.
	 * 
	 * @param sink
	 *            Where banknotes are passed into the machine.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the configuration phase.
	 */
	void connect(BidirectionalChannel<Banknote> sink) {
		if(phase == Phase.NORMAL)
			throw new IllegalNormalPhaseSimulationException();
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();

		this.sink = sink;
	}

	/**
	 * Tells the banknote slot that the indicated banknote is being inserted. If the
	 * sink can accept the banknote, the banknote is passed to the sink and a
	 * "banknoteInserted" event is announced; otherwise, a "banknoteEjected" event
	 * is announced, meaning that the banknote is returned to the user.
	 * 
	 * @param banknote
	 *            The banknote to be added. Cannot be null.
	 * @throws DisabledException
	 *             if the banknote slot is currently disabled.
	 * @throws SimulationException
	 *             If the banknote is null.
	 * @throws OverloadException
	 *             If a banknote is dangling from the slot.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public void accept(Banknote banknote) throws DisabledException, OverloadException {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(isDisabled())
			throw new DisabledException();

		if(!danglingEjectedBanknotes.isEmpty())
			throw new OverloadException("A banknote is dangling from the slot. Remove that before adding another.");

		notifyBanknoteInserted();

		if(!invert && sink.hasSpace()) {
			try {
				sink.deliver(banknote);
			}
			catch(OverloadException e) {
				// Should never happen
				phase = Phase.ERROR;
				throw e;
			}
		}
		else {
			danglingEjectedBanknotes.add(banknote);
			notifyBanknotesEjected();
		}
	}

	private final List<Banknote> danglingEjectedBanknotes = new ArrayList<>();

	/**
	 * Ejects the indicated banknotes, leaving them dangling until the customer
	 * grabs it.
	 * 
	 * @param banknotes
	 *            The banknotes to be ejected.
	 * @throws DisabledException
	 *             If the device is disabled.
	 * @throws SimulationException
	 *             If the argument is null.
	 * @throws OverloadException
	 *             If a banknote is already dangling from the slot.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public void emit(Banknote... banknotes) throws DisabledException, OverloadException {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(isDisabled())
			throw new DisabledException();

		if(banknotes == null)
			throw new NullPointerSimulationException("banknotes");

		if(!danglingEjectedBanknotes.isEmpty())
			throw new OverloadException(
				"A banknote is already dangling from the slot. Remove that before ejecting another.");

		danglingEjectedBanknotes.addAll(Arrays.asList(banknotes));

		notifyBanknotesEjected();
	}

	/**
	 * Ejects the indicated banknote, leaving it dangling until the customer grabs
	 * it.
	 * <p>
	 * Deprecated in favour of {@link #emit(Banknote...)}.
	 * 
	 * @param banknote
	 *            The banknotes to be ejected.
	 * @throws DisabledException
	 *             If the device is disabled.
	 * @throws SimulationException
	 *             If the argument is null.
	 * @throws OverloadException
	 *             If a banknote is already dangling from the slot.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	@Deprecated
	public void emit(Banknote banknote) throws DisabledException, OverloadException {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(isDisabled())
			throw new DisabledException();

		if(banknote == null)
			throw new NullPointerSimulationException("banknote");

		if(!danglingEjectedBanknotes.isEmpty())
			throw new OverloadException(
				"A banknote is already dangling from the slot. Remove that before ejecting another.");

		danglingEjectedBanknotes.add(banknote);

		notifyBanknotesEjected();
	}

	/**
	 * Simulates the user removing one or more banknotes that are dangling from the
	 * slot. Announces "banknotesRemoved" event. Disabling has no effect on this
	 * method.
	 * 
	 * @return The formerly dangling banknotes.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public Banknote[] removeDanglingBanknotes() {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(danglingEjectedBanknotes.isEmpty())
			throw new NullPointerSimulationException("danglingEjectedBanknote");

		Banknote[] b = danglingEjectedBanknotes.toArray(new Banknote[danglingEjectedBanknotes.size()]);
		danglingEjectedBanknotes.clear();
		notifyBanknotesRemoved();

		return b;
	}

	/**
	 * Tests whether a banknote can be accepted by or ejected from this slot.
	 * Disabling has no effect on this method.
	 * 
	 * @return True if the slot is not occupied by a dangling banknote; otherwise,
	 *             false.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public boolean hasSpace() {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		return danglingEjectedBanknotes.isEmpty();
	}

	private void notifyBanknoteInserted() {
		for(BanknoteSlotObserver observer : observers)
			observer.banknoteInserted(this);
	}

	private void notifyBanknotesEjected() {
		for(BanknoteSlotObserver observer : observers)
			observer.banknotesEjected(this);
	}

	private void notifyBanknotesRemoved() {
		for(BanknoteSlotObserver observer : observers)
			observer.banknoteRemoved(this);
	}
}
