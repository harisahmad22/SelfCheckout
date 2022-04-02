package org.controlSoftware.deviceHandlers.payment;

import java.io.IOException;
import java.math.BigDecimal;

import org.controlSoftware.customer.CheckoutSoftware;
import org.controlSoftware.data.BankClientInfo;
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
	private BankClientInfo bankInfo;
	private boolean wasCardSwiped = false;
	
	/**
	 * @param scs
	 * @param card
	 * @param cardData
	 * @param pin
	 * @param bankInfo
	 * Creates a new PayWithDebitCard Simulation
	 */
	public PayWithDebitCard(SelfCheckoutStation scs, Card card, CardData cardData, String pin, BankClientInfo bankInfo) {
		this.scs = scs;
		this.card = card;
//		this.cardData = cardData;
		this.pin = pin;
		this.bankInfo = bankInfo;
		
	}
	
	/**
	 * Enabled State
	 */
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {				
	}

	/**
	 * Disabled State
	 */
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {		
	}

	/** 
	 * Simulates a card being inserted
	 */
	public void cardInserted(CardReader reader) {
		try {
			cardData = reader.insert(card, pin);
		} catch (IOException e) {
			System.out.println("Error inserting card, please ensure that you have entered the correct PIN.");
			e.printStackTrace();
		}
	}

	/** 
	 * Simulates a card being removed
	 */
	public void cardRemoved(CardReader reader) {
		reader.remove();
	}

	/** 
	 * Simulates a card being tapped
	 */
	public void cardTapped(CardReader reader) {
		try {
			cardData = reader.tap(card);
			
		} catch (IOException e) {
			System.out.println("Error tapping card, please try again or try a different card payment method.");
			e.printStackTrace();
		}
		
	}

	/**
	 * Simulates a card being swiped
	 */
	public void cardSwiped(CardReader reader) {
		try {
			wasCardSwiped = true;
			cardData = reader.swipe(card);
		} catch (IOException e) {
			System.out.println("Error swiping card, please try again or try a different card payment method.");
			e.printStackTrace();
		}
	}	

	
	/**
	 * Read Card Data
	 */
	public void cardDataRead(CardReader reader, CardData data) {
	}
	
	/**
	 * @return cardData
	 * Returns CardData
	 */
	public CardData getCardData() {
		return cardData;		
		
	}

	/**
	 * @param reader
	 * @param totalDue
	 * @return true if banking info is correct, false if not
	 * Verifies a purchase using a card
	 */
	public boolean verifyBankingInfo(CardReader reader, BigDecimal totalDue) {	
		
		if(bankInfo.getBalance().compareTo(totalDue) >= 0 
				&& bankInfo.getCardholder() == cardData.getCardholder() 
				&& bankInfo.getNumber() == cardData.getNumber()) 
		{
			if (!wasCardSwiped)
			{
				if (bankInfo.getCVV() == cardData.getCVV())
				{
					completeTransaction(totalDue);
					return true;
				}
				else { return false; }
			}
			else
			{
				completeTransaction(totalDue);
				wasCardSwiped = false; //reset
				return true;
			}
		} else {
			wasCardSwiped = false; //reset
			return false;
		}
	}

	private void completeTransaction(BigDecimal totalDue)
	{
		CheckoutSoftware.addToTotalPaid(totalDue);
		bankInfo.updateBalance(totalDue);
	}
	

}
