package org.lsmr.selfcheckout.devices;

import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Random;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.IllegalConfigurationPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalErrorPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalNormalPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalPhaseSimulationException;
import org.lsmr.selfcheckout.InvalidArgumentSimulationException;
import org.lsmr.selfcheckout.NullPointerSimulationException;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.observers.BanknoteValidatorObserver;

/**
 * Represents a device for optically and/or magnetically validating banknotes.
 * Banknotes deemed valid are moved to storage; banknotes deemed invalid are
 * ejected.
 */
public final class BanknoteValidator extends AbstractDevice<BanknoteValidatorObserver>
	implements Acceptor<Banknote>, Emitter<Banknote> {
	private final Currency currency;
	private final int[] denominations;
	private BidirectionalChannel<Banknote> source;
	private UnidirectionalChannel<Banknote> sink;

	/**
	 * Creates a banknote validator that recognizes banknotes of the specified
	 * denominations (i.e., values) and currency.
	 * 
	 * @param currency
	 *            The kind of currency to accept.
	 * @param denominations
	 *            An array of the valid banknote denominations (like $5, $10, etc.)
	 *            to accept. Each value must be &gt;0 and unique in this array.
	 * @throws SimulationException
	 *             If either argument is null.
	 * @throws SimulationException
	 *             If the denominations array does not contain at least one value.
	 * @throws SimulationException
	 *             If any value in the denominations array is non-positive.
	 * @throws SimulationException
	 *             If any value in the denominations array is non-unique.
	 */
	public BanknoteValidator(Currency currency, int[] denominations) {
		if(currency == null)
			throw new NullPointerSimulationException("currency");

		if(denominations == null)
			throw new NullPointerSimulationException("denominations");

		if(denominations.length < 1)
			throw new InvalidArgumentSimulationException("There must be at least one denomination.");

		this.currency = currency;
		Arrays.sort(denominations);

		HashSet<Integer> set = new HashSet<>();

		for(int denomination : denominations) {
			if(denomination <= 0)
				throw new InvalidArgumentSimulationException(
					"Non-positive denomination detected: " + denomination + ".");

			if(set.contains(denomination))
				throw new InvalidArgumentSimulationException(
					"Each denomination must be unique, but " + denomination + " is repeated.");

			set.add(denomination);
		}

		this.denominations = denominations;
	}

	/**
	 * Connects input and output channels to the banknote slot. Causes no events.
	 * 
	 * @param source
	 *            The channel from which banknotes normally arrive for validation,
	 *            and to which invalid banknotes will be ejected.
	 * @param sink
	 *            The channel to which all valid banknotes are routed.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the configuration phase.
	 */
	void connect(BidirectionalChannel<Banknote> source, UnidirectionalChannel<Banknote> sink) {
		if(phase == Phase.NORMAL)
			throw new IllegalNormalPhaseSimulationException();
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();

		this.source = source;
		this.sink = sink;
	}

	private final Random pseudoRandomNumberGenerator = new Random();
	private static final int PROBABILITY_OF_FALSE_REJECTION = 1; /* out of 100 */

	private boolean isValid(Banknote banknote) {
		if(currency.equals(banknote.getCurrency()))
			for(int denomination : denominations)
				if(denomination == banknote.getValue())
					return pseudoRandomNumberGenerator.nextInt(100) >= PROBABILITY_OF_FALSE_REJECTION;

		return false;
	}

	/**
	 * Tells the banknote validator that the indicated banknote is being inserted.
	 * If the banknote is valid, announces "validBanknoteDetected" event; otherwise,
	 * announces "invalidBanknoteDetected" event.
	 * <p>
	 * If there is space in the machine to store a valid banknote, it is passed to
	 * the sink channel.
	 * </p>
	 * <p>
	 * If there is no space in the machine to store it or the banknote is invalid,
	 * the banknote is ejected to the source.
	 * </p>
	 * 
	 * @param banknote
	 *            The banknote to be added. Cannot be null.
	 * @throws DisabledException
	 *             if the banknote validator is currently disabled.
	 * @throws SimulationException
	 *             If the banknote is null.
	 * @throws OverloadException
	 *             If the sink throws an OverloadException.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	@Override
	public void accept(Banknote banknote) throws DisabledException, OverloadException {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(isDisabled())
			throw new DisabledException();

		if(banknote == null)
			throw new NullPointerSimulationException("banknote");

		if(isValid(banknote)) {
			notifyValidBanknoteDetected(banknote);

			if(sink.hasSpace()) {
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
				try {
					source.eject(banknote);
				}
				catch(OverloadException e) {
					// Should never happen
					phase = Phase.ERROR;
					throw e;
				}
			}
		}
		else {
			notifyInvalidBanknoteDetected();

			try {
				source.eject(banknote);
			}
			catch(OverloadException e) {
				// Should never happen
				phase = Phase.ERROR;
				throw e;
			}
		}
	}

	@Override
	public boolean hasSpace() {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();

		return true;
	}

	private void notifyValidBanknoteDetected(Banknote banknote) {
		for(BanknoteValidatorObserver observer : observers)
			observer.validBanknoteDetected(this, banknote.getCurrency(), banknote.getValue());
	}

	private void notifyInvalidBanknoteDetected() {
		for(BanknoteValidatorObserver observer : observers)
			observer.invalidBanknoteDetected(this);
	}
}
