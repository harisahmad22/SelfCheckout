package org.controlSoftware;

import java.io.IOException;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;

/**
 * @author Harrison Drew - 30115014
 * Simulates Paying with a Debit Card
 */
public class PayWithDebitCard implements CardReaderObserver {

	private SelfCheckoutStation scs;
	private Card card;
	private CardData cardData;
	private String pin;
	
	public PayWithDebitCard(SelfCheckoutStation scs, Card card, CardData cardData, String pin) {
		this.scs = scs;
		this.card = card;
		this.cardData = cardData;
		this.pin = pin;
		
	}
	
	/* (non-Javadoc)
	 * @see org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver#enabled(org.lsmr.selfcheckout.devices.AbstractDevice)
	 */
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {				
	}

	/* (non-Javadoc)
	 * @see org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver#disabled(org.lsmr.selfcheckout.devices.AbstractDevice)
	 */
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {		
	}

	/* (non-Javadoc)
	 * @see org.lsmr.selfcheckout.devices.observers.CardReaderObserver#cardInserted(org.lsmr.selfcheckout.devices.CardReader)
	 */
	public void cardInserted(CardReader reader) {
		
		try {
			cardData = reader.insert(card, pin);
		} catch (IOException e) {
			System.out.println("Error inserting card, please ensure that you have entered the correct PIN.");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.lsmr.selfcheckout.devices.observers.CardReaderObserver#cardRemoved(org.lsmr.selfcheckout.devices.CardReader)
	 */
	public void cardRemoved(CardReader reader) {
		reader.remove();
	}

	/* (non-Javadoc)
	 * @see org.lsmr.selfcheckout.devices.observers.CardReaderObserver#cardTapped(org.lsmr.selfcheckout.devices.CardReader)
	 */
	public void cardTapped(CardReader reader) {
		try {
			cardData = reader.tap(card);
			
		} catch (IOException e) {
			System.out.println("Error tapping card, please try again or try a different card payment method.");
			e.printStackTrace();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.lsmr.selfcheckout.devices.observers.CardReaderObserver#cardSwiped(org.lsmr.selfcheckout.devices.CardReader)
	 */
	public void cardSwiped(CardReader reader) {
		try {
			cardData = reader.swipe(card);
		} catch (IOException e) {
			System.out.println("Error swiping card, please try again or try a different card payment method.");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.lsmr.selfcheckout.devices.observers.CardReaderObserver#cardDataRead(org.lsmr.selfcheckout.devices.CardReader, org.lsmr.selfcheckout.Card.CardData)
	 */
	public void cardDataRead(CardReader reader, CardData data) {	
		
		// Implement Banking info
		
	}
	
	

}
