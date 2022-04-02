package org.controlSoftware.deviceHandlers.membership;

import java.io.IOException;

import org.controlSoftware.customer.CheckoutSoftware;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;

public class ScansMembershipCard implements CardReaderObserver{
	private CheckoutSoftware checkout;
	
	public ScansMembershipCard(CheckoutSoftware checkout_instance) {
		this.checkout = checkout_instance;
	}
	
	/**
	 * Announces that the indicated device has been enabled.
	 * 
	 * @param device
	 *                 The device that has been enabled.
	 */
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device){}

	/**
	 * Announces that the indicated device has been disabled.
	 * 
	 * @param device
	 *                 The device that has been enabled.
	 */
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {}
	
	/**
	 * Announces that a card has been inserted in the indicated card reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	@Override
	public void cardInserted(CardReader reader) {
	}

	/**
	 * Announces that a card has been removed from the indicated card reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	@Override
	public void cardRemoved(CardReader reader) {
	}

	/**
	 * Announces that a (tap-enabled) card has been tapped on the indicated card
	 * reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	@Override
	public void cardTapped(CardReader reader) {
	}
	
	/**
	 * Announces that a card has swiped on the indicated card reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	@Override
	public void cardSwiped(CardReader reader) {
		System.out.println("A Card has been Swiped!");
		checkout.setCardSwiped(true); //Inform checkout of swipe
	}

	/**
	 * Announces that the data has been read from a card.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 * @param data
	 *            The data that was read. Note that this data may be corrupted.
	 */
	@Override
	public void cardDataRead(CardReader reader, CardData data) {
		if (checkout.getCardSwiped() && (data.getType() == "Membership") && (checkout.isWaitingForMembership())) {
			checkout.setMembershipNumber(data.getNumber());
		}
	}

}
