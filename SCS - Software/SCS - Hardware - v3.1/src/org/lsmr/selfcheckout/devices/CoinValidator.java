package org.lsmr.selfcheckout.devices;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.IllegalConfigurationPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalErrorPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalNormalPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalPhaseSimulationException;
import org.lsmr.selfcheckout.InvalidArgumentSimulationException;
import org.lsmr.selfcheckout.NullPointerSimulationException;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.observers.CoinValidatorObserver;

/**
 * Represents a device for optically and/or physically validating coins. Coins
 * deemed valid are moved to storage; coins deemed invalid are ejected.
 */
public final class CoinValidator extends AbstractDevice<CoinValidatorObserver> implements Acceptor<Coin> {
	/**
	 * Represents the currency that is recognized by this validator.
	 */
	public final Currency currency;
	private List<BigDecimal> denominations;
	private UnidirectionalChannel<Coin> rejectionSink, overflowSink;
	private Map<BigDecimal, UnidirectionalChannel<Coin>> standardSinks = null;

	/**
	 * Creates a coin validator that recognizes coins of the specified denominations
	 * (i.e., values) and currency.
	 * 
	 * @param currency
	 *            The kind of currency to accept.
	 * @param denominations
	 *            An array of the valid coin denominations (like $0.05, $0.10, etc.)
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
	public CoinValidator(Currency currency, List<BigDecimal> denominations) {
		if(currency == null)
			throw new NullPointerSimulationException("currency");

		if(denominations == null)
			throw new NullPointerSimulationException("denominations");

		if(denominations.size() < 1)
			throw new InvalidArgumentSimulationException("There must be at least one denomination.");

		this.currency = currency;
		Collections.sort(denominations);

		standardSinks = new HashMap<>();

		for(BigDecimal denomination : denominations) {
			if(denomination == null)
				throw new NullPointerSimulationException("denomination instance");

			if(denomination.compareTo(BigDecimal.ZERO) <= 0)
				throw new InvalidArgumentSimulationException(
					"Non-positive denomination detected: " + denomination + ".");

			if(standardSinks.containsKey(denomination))
				throw new InvalidArgumentSimulationException(
					"Each denomination must be unique, but " + denomination + " is repeated.");

			standardSinks.put(denomination, null);
		}

		this.denominations = denominations;
	}

	/**
	 * Connects input and output channels to the coin slot. Causes no events.
	 * 
	 * @param rejectionSink
	 *            The channel to which rejected coins are routed.
	 * @param overflowSink
	 *            The channel to which valid coins are routed when the normal sink
	 *            is full.
	 * @param standardSinks
	 *            The channels to which valid coins are normally routed. There must
	 *            be one sink to correspond to each valid currency denomination, and
	 *            they must be in the same order as the valid denominations.
	 * @throws SimulationException
	 *             If any argument is null.
	 * @throws SimulationException
	 *             If any standard sink is null.
	 * @throws SimulationException
	 *             If the number of standard sinks differs from the number of
	 *             denominations.
	 * @throws SimulationException
	 *             If any sink is used in more than one position.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the configuration phase.
	 */
	void connect(UnidirectionalChannel<Coin> rejectionSink, Map<BigDecimal, UnidirectionalChannel<Coin>> standardSinks,
		UnidirectionalChannel<Coin> overflowSink) {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.NORMAL)
			throw new IllegalNormalPhaseSimulationException();

		if(rejectionSink == null)
			throw new NullPointerSimulationException("rejectionSink");

		if(overflowSink == null)
			throw new NullPointerSimulationException("overflowSink");

		if(standardSinks == null)
			throw new NullPointerSimulationException("standardSinks");

		if(standardSinks.keySet().size() != denominations.size())
			throw new InvalidArgumentSimulationException(
				"The number of standard sinks must equal the number of denominations.");

		this.rejectionSink = rejectionSink;
		this.overflowSink = overflowSink;

		HashSet<UnidirectionalChannel<Coin>> set = new HashSet<>();

		for(BigDecimal denomination : standardSinks.keySet()) {
			UnidirectionalChannel<Coin> sink = standardSinks.get(denomination);
			if(sink == null)
				throw new NullPointerSimulationException("sink for denomination " + denomination);
			else {
				if(set.contains(sink))
					throw new InvalidArgumentSimulationException("Each channel must be unique.");

				set.add(sink);
			}
		}

		if(set.contains(rejectionSink))
			throw new InvalidArgumentSimulationException("Each channel must be unique.");
		else
			set.add(rejectionSink);

		if(set.contains(overflowSink))
			throw new InvalidArgumentSimulationException("Each channel must be unique.");

		this.standardSinks.putAll(standardSinks);
		this.overflowSink = overflowSink;
	}

	private final Random pseudoRandomNumberGenerator = new Random();
	private static final int PROBABILITY_OF_FALSE_REJECTION = 1; /* out of 100 */

	private boolean isValid(Coin coin) {
		if(currency.equals(coin.getCurrency()))
			for(BigDecimal denomination : denominations)
				if(denomination.equals(coin.getValue()))
					return pseudoRandomNumberGenerator.nextInt(100) >= PROBABILITY_OF_FALSE_REJECTION;

		return false;
	}

	/**
	 * Tells the coin validator that the indicated coin is being inserted. If the
	 * coin is valid, announces "validCoinDetected" event; otherwise, announces
	 * "invalidCoinDetected" event.
	 * <p>
	 * If there is space in the machine to store a valid coin, it is passed to the
	 * sink channel corresponding to the denomination of the coin.
	 * </p>
	 * <p>
	 * If there is no space in the machine to store it or the coin is invalid, the
	 * coin is ejected to the source.
	 * </p>
	 * 
	 * @param coin
	 *            The coin to be added. Cannot be null.
	 * @throws DisabledException
	 *             if the coin validator is currently disabled.
	 * @throws OverloadException
	 *             if the needed sink cannot accept the coin.
	 * @throws SimulationException
	 *             If the coin is null.
	 * @throws SimulationException
	 *             If the coin cannot be delivered.
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

		if(isValid(coin)) {
			notifyValidCoinDetected(coin);

			UnidirectionalChannel<Coin> sink = standardSinks.get(coin.getValue());

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
			else {
				try {
					overflowSink.deliver(coin);
				}
				catch(OverloadException e) {
					// Should never happen
					phase = Phase.ERROR;
					throw e;
				}
			}
		}
		else {
			notifyInvalidCoinDetected(coin);

			try {
				rejectionSink.deliver(coin);
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

		return true; // Since we cannot know yet where a coin will route, assume that it is OK.
	}

	private void notifyValidCoinDetected(Coin coin) {
		for(CoinValidatorObserver observer : observers)
			observer.validCoinDetected(this, coin.getValue());
	}

	private void notifyInvalidCoinDetected(Coin coin) {
		for(CoinValidatorObserver observer : observers)
			observer.invalidCoinDetected(this);
	}
}
