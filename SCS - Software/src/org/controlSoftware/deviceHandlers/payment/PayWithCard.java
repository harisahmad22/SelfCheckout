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
	
	
	//put Div's giftcard class into same branch, import and remember to set up!!!
	private GiftCardDatabase giftCards;
	private CardPaymentSoftware paymentSoftware;
	
	
	//card issuers have unique card types that correspond with them (ex. "xxx company debit" or "a company visa credit")
	Map<String, CardIssuer> cardIssuers = new HashMap<>();
	
	protected String memberNumber;
	
	public PayWithCard(SelfCheckoutData stationData, SelfCheckoutSoftware stationSoftware) {
		this.stationData = stationData;
		this.stationSoftware = stationSoftware;
		this.station = stationData.getStationHardware();
//		station.cardReader.attach(this);
		
		this.paymentSoftware = this.stationSoftware.getCardPaymentSoftware();
		
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
			paymentSoftware.paid(stationData.getTransactionPaymentAmount());
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
			paymentSoftware.paid(stationData.getTransactionPaymentAmount());
		}
		return paymentSuccessful;		
	}
	
	/*
	public boolean payWithGiftCard(CardData data) {
		boolean paymentSuccessful = false;
		String cardNum = data.getNumber();
	
			
		BigDecimal amountOnGiftCard = BigDecimal.valueOf(giftCards.get(cardNum));
		
		//customer chooses to use entire giftcard
		//does not matter if giftcard has 0 or not, will process it as successful
		//(customer chooses to use entire giftcard, if $0, then customer chooses to use all of that :D
		//there should be something before hand that informs customer of amount left on card!!
		if(useAllGiftCard == true) {
			if(paymentTotal.compareTo(amountOnGiftCard) >= 0) {
				giftCards.updateGiftCard(cardNum, 0.00);
				payment.paid(amountOnGiftCard);
				paymentSuccessful = true;
			}
			else {
				BigDecimal amountLeft =  amountOnGiftCard.subtract(paymentTotal);
				giftCards.updateGiftCard(cardNum, amountLeft.doubleValue());
				paymentSuccessful = true;
			}
				
		}
		
		//customer chooses certain amount of the giftcard to use
		//paymentAmount is the amount inputed by customer to use
		else {
			if(amountOnGiftCard.compareTo(paymentAmount) >= 0) {
				amountOnGiftCard = amountOnGiftCard.subtract(paymentAmount);
				giftCards.updateGiftCard(cardNum, amountOnGiftCard.doubleValue());
				payment.paid(amountOnGiftCard);
				paymentSuccessful = true;
			}
			else {
				return paymentSuccessful;
			}
		}
		
		return paymentSuccessful;
		
	}
	*/
	/**
	    * Check if the customer has removed their card
	    */
		public void checkCardRemoved() {
			if (cardInserted == new AtomicBoolean(true)) {
				station.cardReader.disable();
			}
			
		}
		
		public void reset() {
//			cardIssuer = null;
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
		if ((data.getType() != "Membership") && 
			(stationData.getCurrentState() == StationState.PAY_CREDIT 
		  || stationData.getCurrentState() == StationState.PAY_DEBIT))
		{
			cardData = data;
			cardType = data.getType();
			cardIssuer = cardIssuers.get(cardType);

//			System.out.println("AAAAAA" + cardType);
//			String cardKind = findCardKind(cardType);

			if(cardIssuers.containsKey(cardType) == true) {
				cardIssuer = cardIssuers.get(cardType);
			}
			/*
			else if(cardKind == "Giftcard") {
				if(checkGiftCardBalance == true) {
					if(giftcards.getDatabase().containsKey(data.getNumber()) == true) {
						BigDecimal balance = BigDecimal.valueOf(giftcards.getDatabase().get(data.getNumber()));
						payment.getGiftCardBalance(balance, true);
						//no longer in checking balance phase
						checkGiftCardBalance = false;
						return;
					}
					else {
						payment.getGiftCardBalance(new BigDecimal(0), false);
						checkGiftCardBalance = false;
						return;
					}
				}
				else if(giftcards.getDatabase().containsKey(data.getNumber()) == true) {
					success = payWithGiftCard(cardData);
					if(success == true) {
						payment.giftCardPaymentSuccessful();
					}
					else {
						payment.giftCardPaymentUnsuccessful();
						
					}
				}
				else {
					success = false;
					paymentUnsuccessful();
				}
			}
			*/
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
					paymentSoftware.debitPaymentSuccessful();
					stationData.changeState(StationState.PRINT_RECEIPT_PROMPT);
				}
				else {
					paymentSoftware.debitPaymentUnsuccessful();
					//TODO Change state to handle unsuccessful payment
				}
				break;
				
			case "Credit":
				success = payWithCredit(cardData);
				if(success == true) {
					paymentSoftware.creditPaymentSuccessful();
					stationData.changeState(StationState.PRINT_RECEIPT_PROMPT);
				}
				else {
					paymentSoftware.creditPaymentUnsuccessful();
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
	public void paymentUnsuccessful() {
		
	}
	
	public void paymentSuccessful() {
		
	}
	

}
