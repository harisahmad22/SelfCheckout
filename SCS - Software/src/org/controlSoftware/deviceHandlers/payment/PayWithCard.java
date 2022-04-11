package org.controlSoftware.deviceHandlers.payment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutData.StationState;
import org.driver.SelfCheckoutSoftware;
import org.driver.databases.GiftCardDatabase;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;
import org.lsmr.selfcheckout.external.CardIssuer;

//for payments involving a card (credit, debit, giftcard)
public class PayWithCard implements CardReaderObserver {
	
	private SelfCheckoutData stationData;
	private SelfCheckoutSoftware stationSoftware;
	private SelfCheckoutStation station;
	private CardData cardData;
	private String cardType;
	private CardIssuer cardIssuer;
	
	private AtomicBoolean cardInserted = new AtomicBoolean(false);
	private boolean success = false;
	
	protected BigDecimal paymentAmount = new BigDecimal("0");
	protected BigDecimal paymentTotal = new BigDecimal("0");
	
	protected boolean useAllGiftCard = false;
	protected boolean checkGiftCardBalance = true;
	
	private GiftCardDatabase giftCards;
	
	
	//card issuers have unique card types that correspond with them (ex. "xxx company debit" or "a company visa credit")
	Map<String, CardIssuer> cardIssuers = new HashMap<>();
	
	protected String memberNumber;
	
	public PayWithCard(SelfCheckoutData stationData, SelfCheckoutSoftware stationSoftware) {
		this.stationData = stationData;
		this.stationSoftware = stationSoftware;
		this.station = stationData.getStationHardware();		
	}
	
	//map card type to specific card issuer
	public void addCardIssuer(String cardType, CardIssuer issuer) {
		if (cardType == null || issuer == null) {return;}
		cardIssuers.put(cardType, issuer);
	}
	
	//set the giftcards database
	public void addGiftCardDatabase(GiftCardDatabase database) {
		giftCards = database;
	}
	
	public void setPaymentTotal(BigDecimal total) {
		paymentTotal = total;
	}
	
	
		/**
	    * Customer chooses to pay with debit card
	    * 
	    * @param CardData
	    * 			Debit card information
	    */
	public boolean payWithDebit(CardData cardData) {
		boolean paymentSuccessful = false;
		
		String cardNum = cardData.getNumber();
		int holdNum = cardIssuer.authorizeHold(cardNum, stationData.getTransactionPaymentAmount());
		if(holdNum == -1) {
			return paymentSuccessful;
		}
		if(holdNum >= 0) {
			paymentSuccessful = cardIssuer.postTransaction(cardNum, holdNum, stationData.getTransactionPaymentAmount());
			paid(stationData.getTransactionPaymentAmount());
		}
		
		return paymentSuccessful;
	}
	
		/**
	    * Customer chooses to pay with credit card
	    * 
	    * @param CardData
	    * 			Credit card information
	    */
	public boolean payWithCredit(CardData cardData) {
		boolean paymentSuccessful = false;
		
		String cardNum = cardData.getNumber();
		int holdNum = cardIssuer.authorizeHold(cardNum, stationData.getTransactionPaymentAmount());
		if(holdNum == -1) {
			return paymentSuccessful;
		}
		if(holdNum >= 0) {
			paymentSuccessful = true;		//credit card transactions are not posted immediately?
			paid(stationData.getTransactionPaymentAmount());
		}
		return paymentSuccessful;		
	}
	
	/**
	    * Check if the customer has removed their card
	    */
		public void checkCardRemoved() {
			if (cardInserted == new AtomicBoolean(true)) {
				station.cardReader.disable();
			}
			
		}
		
		public void reset() {
			cardData = null;
			success = false;
			paymentAmount = new BigDecimal("0");
		}
	
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		
	}
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		
	}
	@Override
	public void cardInserted(CardReader reader) {
		cardInserted = new AtomicBoolean(true);
	}
	@Override
	public void cardRemoved(CardReader reader) {
		cardInserted = new AtomicBoolean(false);
	}
	@Override
	public void cardTapped(CardReader reader) {
		
	}
	@Override
	public void cardSwiped(CardReader reader) {
		
	}
	@Override
	public void cardDataRead(CardReader reader, CardData data) {
		if (stationData.getCurrentState() == StationState.PAY_CREDIT 
		    || stationData.getCurrentState() == StationState.PAY_DEBIT)
		{
			cardData = data;
			cardType = data.getType();
			cardIssuer = cardIssuers.get(cardType);

			if(cardIssuers.containsKey(cardType) == true) {
				cardIssuer = cardIssuers.get(cardType);
			}
			else {
				paymentUnsuccessful();
				return;
				//if a membership card, should not be a part of cardIssuers available
				//thus, payment with membership card is not possible
			}
			
			switch(cardType) {
			case "Debit":
				success = payWithDebit(cardData);
				if(success == true) {
					stationData.changeState(StationState.PRINT_RECEIPT_PROMPT);
				}
				else {
					stationData.changeState(StationState.BAD_CARD);
				}
				break;
				
			case "Credit":
				success = payWithCredit(cardData);
				if(success == true) {
					stationData.changeState(StationState.PRINT_RECEIPT_PROMPT);
				}
				else {
					stationData.changeState(StationState.BAD_CARD);
				}
				break;
			}
			checkCardRemoved();
			reset();
		}
	}
	
	public String findCardKind(String cardType) {
		String type = cardType;
		String kind = null;

		String[] debit = {"Debit", "debit"};
		String[] credit = {"Credit", "credit"};
		String[] giftCard = {"Giftcard", "giftcard"};
		String[] memberCard = {"member", "Member"};

		if((type.indexOf(debit[0]) > 0) || (type.indexOf(debit[1]) > 0) ) {
			kind = "Debit";
		}
		if((type.indexOf(credit[0]) > 0) || (type.indexOf(credit[1]) > 0) ) {
			kind = "Credit";
		}
		if((type.indexOf(giftCard[0]) > 0) || (type.indexOf(giftCard[1]) > 0) ) {
			kind = "Giftcard";
		}
		if((type.indexOf(memberCard[0]) > 0) || (type.indexOf(memberCard[1]) > 0) ) {
			kind = "Membership";
		}
		
		return kind;
		
	}
	
	public void paid(BigDecimal paid) {
		stationData.addToTotalPaid(paid);
	}
	
	public void paymentUnsuccessful() {
		
	}
	
	public void paymentSuccessful() {
		
	}
	

}
