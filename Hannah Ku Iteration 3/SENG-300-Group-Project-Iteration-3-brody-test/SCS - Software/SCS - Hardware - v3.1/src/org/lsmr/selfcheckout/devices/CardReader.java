package org.lsmr.selfcheckout.devices;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.ChipFailureException;
import org.lsmr.selfcheckout.IllegalConfigurationPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalErrorPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalPhaseSimulationException;
import org.lsmr.selfcheckout.MagneticStripeFailureException;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;

/**
 * Represents the card reader, capable of tap, chip insert, and swipe. Either
 * the reader or the card may fail, or the data read in can be corrupted, with
 * varying probabilities.
 */
public class CardReader extends AbstractDevice<CardReaderObserver> {
	private boolean cardIsInserted = false;

	/**
	 * Create a card reader.
	 */
	public CardReader() {}

	private final static ThreadLocalRandom random = ThreadLocalRandom.current();
	private final static double PROBABILITY_OF_TAP_FAILURE = 0.01;
	private final static double PROBABILITY_OF_INSERT_FAILURE = 0.01;
	private final static double PROBABILITY_OF_SWIPE_FAILURE = 0.1;

	/**
	 * Tap the card.
	 * 
	 * @param card
	 *            The card to tap.
	 * @return The card's (possibly corrupted) data, or null if the card is not tap
	 *             enabled.
	 * @throws IOException
	 *             If the tap failed (lack of failure does not mean that the data is
	 *             not corrupted).
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public CardData tap(Card card) throws IOException {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(card.isTapEnabled) {
			notifyCardTapped();

			if(random.nextDouble(0.0, 1.0) > PROBABILITY_OF_TAP_FAILURE) {
				CardData data = card.tap();

				notifyCardDataRead(data);

				return data;
			}
			else
				throw new ChipFailureException();
		}

		// else ignore
		return null;
	}

	/**
	 * Swipe the card.
	 * 
	 * @param card
	 *            The card to swipe.
	 * @return The card data.
	 * @throws IOException
	 *             If the swipe failed.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public CardData swipe(Card card) throws IOException {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		notifyCardSwiped();

		if(random.nextDouble(0.0, 1.0) > PROBABILITY_OF_SWIPE_FAILURE) {
			CardData data = card.swipe();

			notifyCardDataRead(data);

			return data;
		}

		throw new MagneticStripeFailureException();
	}

	/**
	 * Insert the card.
	 * 
	 * @param card
	 *            The card to insert.
	 * @param pin
	 *            The customer's PIN.
	 * @return The card data.
	 * @throws SimulationException
	 *             If there is already a card in the slot.
	 * @throws IOException
	 *             The insertion failed.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public CardData insert(Card card, String pin) throws IOException {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(cardIsInserted)
			throw new IllegalStateException("There is already a card in the slot");

		cardIsInserted = true;

		notifyCardInserted();

		if(card.hasChip && random.nextDouble(0.0, 1.0) > PROBABILITY_OF_INSERT_FAILURE) {
			CardData data = card.insert(pin);

			notifyCardDataRead(data);

			return data;
		}

		throw new ChipFailureException();
	}

	/**
	 * Remove the card from the slot.
	 * 
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public void remove() {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		cardIsInserted = false;
		notifyCardRemoved();
	}

	private void notifyCardTapped() {
		for(CardReaderObserver l : observers)
			l.cardTapped(this);
	}

	private void notifyCardInserted() {
		for(CardReaderObserver l : observers)
			l.cardInserted(this);
	}

	private void notifyCardSwiped() {
		for(CardReaderObserver l : observers)
			l.cardSwiped(this);
	}

	private void notifyCardDataRead(CardData data) {
		for(CardReaderObserver l : observers)
			l.cardDataRead(this, data);
	}

	private void notifyCardRemoved() {
		for(CardReaderObserver l : observers)
			l.cardRemoved(this);
	}
}
