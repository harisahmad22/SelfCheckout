package org.controlSoftware;

import java.io.IOException;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;

public class PayWithDebitCard implements CardReaderObserver {

	private Card card;
	private CardData cardData;
	private String pin;
	
	public PayWithDebitCard(Card card, CardData cardData) {
		this.card = card;
		this.cardData = cardData;
		
	}
	
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {				
	}

	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {		
	}

	public void cardInserted(CardReader reader) {
		
		// Read PIN (touchscreen?)
		
		try {
			cardData = reader.insert(card, pin);
		} catch (IOException e) {
			System.out.println("Error inserting card, please ensure that you have entered the correct PIN.");
			e.printStackTrace();
		}
	}

	public void cardRemoved(CardReader reader) {
		reader.remove();
	}

	public void cardTapped(CardReader reader) {
		try {
			cardData = reader.tap(card);
			
		} catch (IOException e) {
			System.out.println("Error tapping card, please try again or try a different card payment method.");
			e.printStackTrace();
		}
		
	}

	public void cardSwiped(CardReader reader) {
		try {
			cardData = reader.swipe(card);
		} catch (IOException e) {
			System.out.println("Error swiping card, please try again or try a different card payment method.");
			e.printStackTrace();
		}
	}

	public void cardDataRead(CardReader reader, CardData data) {
		
		// Implement Banking info
		
	}
	
	

}
