package org.controlSoftware.deviceHandlers.payment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;
import org.lsmr.selfcheckout.external.CardIssuer;

//for payments involving a card (credit, debit, giftcard)
public class PayWithCard implements CardReaderObserver {
	
	private SelfCheckoutStation station;
	private CardData cardData;
	private String cardType;
	private CardIssuer cardIssuer;
	
	private AtomicBoolean cardInserted = new AtomicBoolean(false);
	private boolean success = false;
	
	protected BigDecimal paymentAmount = new BigDecimal("0");
	protected BigDecimal paymentTotal = new BigDecimal("0");
	
	protected boolean useAllGiftCard = true;
	protected boolean checkGiftCardBalance = true;
	
	
	//put Div's giftcard class into same branch, import!!!!!!!!!!!!!
	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	//
	
	//FOR FULL PAYMENTS, MAKE THE PAYMENTAMOUNT THE TOTAL DISCOUNTED PAYMENT AMOUNT 
	private GiftCardDatabase giftCards;
	private PaymentHandler payment;
	
	
	//card issuers have unique card types that correspond with them (ex. "xxx company debit" or "a company visa credit")
	Map<String, CardIssuer> cardIssuers = new HashMap<>();
	
	protected String memberNumber;
	
	//constructor
	public PayWithCard(SelfCheckoutStation station, PaymentHandler pay) {
		this.station = station;
		station.cardReader.attach(this);
		
		this.payment = pay;
		
	}
	
	//map card type to specific card issuer
	public void addCardIssuer(String cardType, CardIssuer issuer) {
		if (cardType == null || issuer == null) {return;}
		cardIssuers.put(cardType, issuer);
	}
	
	//set the giftcards database
	public void addGiftCardDatabase(Map<String, Double> database) {
		giftCards = database;
	}
	
	public void setPaymentTotal(BigDecimal total) {
		paymentTotal = total;
	}
	
	public void setCurrentPaymentAmount(BigDecimal amount) {
		paymentAmount = amount;
	}
	
	public void setUseAllGiftCard() {
		useAllGiftCard = true;

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
		int holdNum = cardIssuer.authorizeHold(cardNum, paymentAmount);
		if(holdNum == -1) {
			return paymentSuccessful;
		}
		if(holdNum >= 0) {
			//if payment amount inputed by customer is greater than amount left to pay
			if(paymentAmount.compareTo((paymentTotal.subtract(payment.getAmountPaid()))) > 0) {
				paymentSuccessful = cardIssuer.postTransaction(cardNum, holdNum, paymentTotal.subtract(payment.getAmountPaid()));
				payment.paid(paymentTotal);
			
			}
			else {
				paymentSuccessful = cardIssuer.postTransaction(cardNum, holdNum, paymentAmount);
				payment.paid(paymentAmount);
			}
			
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
		//first check
		int holdNum = cardIssuer.authorizeHold(cardNum, paymentAmount);
		if(holdNum == -1) {
			return paymentSuccessful;
		}
		if(holdNum >= 0) {
			//if payment amount inputed by customer is greater than amount left to pay
			if(paymentAmount.compareTo((paymentTotal.subtract(payment.getAmountPaid()))) > 0) {
				
				paymentSuccessful = cardIssuer.releaseHold(cardNum, holdNum);
				holdNum = cardIssuer.authorizeHold(cardNum, paymentTotal.subtract(payment.getAmountPaid()));
				if(holdNum>=0) {
					payment.paid(paymentTotal.subtract(payment.getAmountPaid()));
				}
				else {
					paymentSuccessful = false;
					return paymentSuccessful;
				}
			
			}
			//payment inputed by customer is less than amount left to pay 
			else {
				paymentSuccessful = true;		//credit card transactions are not posted immediately?
				payment.paid(paymentAmount);
			}
	
		}
		return paymentSuccessful;		
	}
	
	
	public boolean payWithGiftCard(CardData data) {
		boolean paymentSuccessful = false;
		String cardNum = data.getNumber();
	
			
		BigDecimal amountOnGiftCard = BigDecimal.valueOf(giftCards.get(cardNum));
		
		//customer chooses to use entire giftcard
		//does not matter if giftcard has 0 or not, will process it as successful
		//(customer chooses to use entire giftcard, if $0, then customer chooses to use all of that :D
		//there should be something before hand that informs customer of amount left on card!!
		if(useAllGiftCard == true) {
			//if amount left to pay is greater or equal to amount on gift card
			if((paymentTotal.subtract(payment.getAmountPaid())).compareTo(amountOnGiftCard) >= 0) {
				giftCards.updateGiftCard(cardNum, 0.00);
				payment.paid(amountOnGiftCard);
				paymentSuccessful = true;
			}
			//if amount left to pay is less than amount on giftcard
			else {
				BigDecimal amountLeft =  amountOnGiftCard.subtract(paymentTotal.subtract(payment.getAmountPaid()));
				giftCards.updateGiftCard(cardNum, amountLeft.doubleValue());
				payment.paid(paymentTotal.subtract(payment.getAmountPaid()));
				paymentSuccessful = true;
			}
				
		}
		
		//customer chooses certain amount of the giftcard to use
		//paymentAmount is the amount inputed by customer to use
		else {
			//if amount on giftcard is greater than or equal to payment amount selected by customer
			if(amountOnGiftCard.compareTo(paymentAmount) >= 0) {
				amountOnGiftCard = amountOnGiftCard.subtract(paymentAmount);
				giftCards.updateGiftCard(cardNum, amountOnGiftCard.doubleValue());
				payment.paid(paymentAmount);
				paymentSuccessful = true;

			}
			//if amount on giftcard is less than amount to pay selected by customer (uses all of giftcard)
			else {
				BigDecimal amountCanPay = amountOnGiftCard;
				amountOnGiftCard = amountOnGiftCard.subtract(amountOnGiftCard);
				giftCards.updateGiftCard(cardNum, amountOnGiftCard.doubleValue());
				payment.paid(amountCanPay);
				paymentSuccessful = true;

				
			}
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
		
		public void resetPayment() {
			cardIssuer = null;
			cardData = null;
			cardType = null;
			success = false;
			paymentAmount = new BigDecimal("0");
		    useAllGiftCard = true;
		    checkGiftCardBalance = true;
			
		}
		
		public void resetEntireTransaction() {
			resetPayment();
			paymentTotal = new BigDecimal(0);
			memberNumber = null;
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
		cardIssuer = cardIssuers.get(cardType);
		cardData = data;
		cardType = data.getType();
		String cardKind = findCardKind(cardType);
		
		if(cardIssuers.containsKey(cardType) == true) {
			cardIssuer = cardIssuers.get(cardType);
		}
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
		else {
			paymentUnsuccessful();
			//if a membership card, should not be a part of cardIssuers available
			//thus, payment with membership card is not possible
		}
		

	
		switch(cardKind) {
		case "Debit":
			success = payWithDebit(cardData);
			if(success == true) {
				payment.debitPaymentSuccessful();
			}
			else {
				payment.debitPaymentUnsuccessful();
			}
			break;
			
		case "Credit":
			success = payWithCredit(cardData);
			if(success == true) {
				payment.creditPaymentSuccessful();
			}
			else {
				payment.creditPaymentUnsuccessful();
			}
			break;
		}
		
		checkCardRemoved();
		resetPayment();
		
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
