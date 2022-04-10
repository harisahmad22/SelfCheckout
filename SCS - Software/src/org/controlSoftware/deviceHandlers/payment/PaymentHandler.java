package org.controlSoftware.deviceHandlers.payment;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

import org.controlSoftware.customer.CheckoutSoftware;
import org.controlSoftware.deviceHandlers.membership.UseMembershipCard;
import org.driver.SelfCheckoutData;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class PaymentHandler {

	private SelfCheckoutStation station;
	private CheckoutSoftware checkout;
	private SelfCheckoutData checkoutData;
	
	private BigDecimal amountOwed = new BigDecimal(0);
	private BigDecimal amountPaid = new BigDecimal(0);
	private AtomicBoolean isAllPaid = new AtomicBoolean(false);
	
	private int discount = 0;	//as a decimal percent
	private BigDecimal discountedAmountOwed = amountOwed.multiply(BigDecimal.valueOf(1.00 - discount));
	
	
	private PayWithCard cardPaymentHandler;
	private UseMembershipCard memberCardHandler;
	
	private AtomicBoolean willPayCash = new AtomicBoolean(false);
	private AtomicBoolean willPayCredit = new AtomicBoolean(false);
	private AtomicBoolean willPayDebit = new AtomicBoolean(false);
	private AtomicBoolean willPayGiftCard = new AtomicBoolean(false);
	
	private BigDecimal cashPayment = new BigDecimal(0);
	private BigDecimal creditPayment = new BigDecimal(0);
	private BigDecimal debitPayment = new BigDecimal(0);
	private BigDecimal giftCardPayment = new BigDecimal(0);
	
	private BigDecimal giftCardBalance = new BigDecimal(0);
	private boolean	giftCardExists = false;
	
	
	private AtomicBoolean validMember = new AtomicBoolean(false);
	
	
//NEEDS UPDATED SELFCHECKOUTDATA IN SAME BRANCH!!!
	
	public PaymentHandler(SelfCheckoutStation station, CheckoutSoftware checkout, SelfCheckoutData checkoutData) {
		this.station = station;
		this.checkout = checkout;
		
		this.cardPaymentHandler = new PayWithCard(station, this);
		this.memberCardHandler = new UseMembershipCard(station, this);
		this.checkoutData = checkoutData;
	}
	
	/* idea
	 * customer selects payment method(s) and the amount of the payment that they will
	 * pay for with each method.
	 * after confirming the portions add up, payment proceeds.
	 */
	
	public void beforePayment() {
		station.cardReader.disable();
		station.coinSlot.disable();
		station.banknoteInput.disable();
	}
	
	public void startPayment() {
		station.cardReader.enable();
		station.coinSlot.enable();
		station.banknoteInput.enable();
		
		amountOwed = checkoutData.getTotalDue();
		
		
	}
	
	//checks membership card when there is manual number input
	//calls method when buttons pressed
	public void inputMembershipCard() {
		String num = checkoutData.getMembershipID();
		memberCardHandler.membershipCardInput(num);
	}
	
	/**
	 * Customer chooses to use membership card (swipe before payment selection).
	 * Customer should swipe their card right after this method call.
	 */
	public void swipeMembershipCard() {
		station.cardReader.enable();
		memberCardHandler.wantToScan();	
	}
	
	public void membershipCardScanSuccessful() {
		validMember = new AtomicBoolean(true); 
		memberCardHandler.disableMemberScan();
		discount = memberCardHandler.getPercentDiscount();
	}
	
	public void membershipScanUnsuccessful() {
		validMember = new AtomicBoolean(false); 
		memberCardHandler.disableMemberScan();
	}
	
	//display on gui the balance of a giftcard if it does exist, if not, tell customer that giftcard does not exist
	public void getGiftCardBalance(BigDecimal balance, boolean cardExists) {
		giftCardBalance.add(balance);
		giftCardExists = cardExists;
	}
	
			
			//ask user to swipe or input giftcard
			//display giftCardBlance on screen or maybe customer swipes more giftcards?
			
			//ask user if they want to use a set amount in giftcard or as much as possible
			//proceed with payment
			
			//if giftcard payment covers entire cost, cancel payment for other options
			
	
	//adds to total amount paid
	public void paid(BigDecimal paid) {
		amountPaid.add(paid);
	}
	

	/**
	 * Customer choose to pay with bank note and coin
	 * @param payment
	 */
	public void payWithBankNoteAndCoin() {
		 willPayCash = new AtomicBoolean(true);	
		 station.coinSlot.enable();
		 station.banknoteInput.enable();
		 
		 cardPaymentHandler.getTransactionPaymentAmount();
		 
	}
	

	/**
	 * Customer choose to use credit card for payment
	 * @param payment
	 */
	public void payWithCredit() {
		 willPayCredit = new AtomicBoolean(true);
		 station.cardReader.enable();
		 
		 cardPaymentHandler.setCurrentPaymentAmount(checkoutData.getTransactionPaymentAmount());	}
	
	
	public void payWithDebit() {
		willPayDebit = new AtomicBoolean(true);
		station.cardReader.enable();
		
		cardPaymentHandler.setCurrentPaymentAmount(checkoutData.getTransactionPaymentAmount());
	
	}
	
	//customer can only swipe giftcard??
	public void payWithGiftcard() {
		willPayGiftCard = new AtomicBoolean(true);
		station.cardReader.enable();
		
		
		//customer chooses to use max funds in giftcard possible in transaction 
	    //  | here, some flag perhaps (can scrap this if too much of a hassle
		if(   ){
			cardPaymentHandler.setUseAllGiftCard();
		}
		//choose to input certain payment amount to use
		else {
			cardPaymentHandler.setCurrentPaymentAmount(checkoutData.getTransactionPaymentAmount());
		}

	}
	
	
	//whether customer can confirm a finished purchase or not (all paid for)
	public boolean confirmPurchase() {
		
		//customer has  not finished paying full amount
		if(amountPaid.compareTo(discountedAmountOwed) < 0) {
			isAllPaid = new AtomicBoolean(false);
			return false;
		}
		
		//customer has finished paying full amount
		if(amountPaid.equals(discountedAmountOwed)) {
			isAllPaid = new AtomicBoolean(true);
		
			station.mainScanner.disable();
			station.coinSlot.disable();
			station.banknoteInput.disable();
			station.cardReader.disable();
			
			cardPaymentHandler.resetEntireTransaction();
		}

		return true;
		
	}
	
	//for gui, check if total paid, and if payment was successful or not?, if payment changed?
	public void giftCardPaymentSuccessful(BigDecimal paidAmount) {
		if(amountPaid == discountedAmountOwed) {
			isAllPaid = new AtomicBoolean(true);
		}
		
		giftCardPayment = giftCardPayment.add(paidAmount);
		
	}
	
	public void giftCardPaymentUnsuccessful() {
		if(amountPaid == discountedAmountOwed) {
			isAllPaid = new AtomicBoolean(true);
		}
	}
	
	public void creditPaymentSuccessful(BigDecimal paidAmount) {
		if(amountPaid == discountedAmountOwed) {
			isAllPaid = new AtomicBoolean(true);
		}
		
		creditPayment = creditPayment.add(paidAmount);
		
	}

	public void creditPaymentUnsuccessful() {
		if(amountPaid == discountedAmountOwed) {
			isAllPaid = new AtomicBoolean(true);
		}
	}
	
	public void debitPaymentSuccessful(BigDecimal paidAmount) {
		if(amountPaid == discountedAmountOwed) {
			isAllPaid = new AtomicBoolean(true);
		}
		debitPayment = debitPayment.add(paidAmount);
	}
	
	public void debitPaymentUnsuccessful() {
		if(amountPaid == discountedAmountOwed) {
			isAllPaid = new AtomicBoolean(true);
		}
	}
	
	public void cashPaymentSuccessful(BigDecimal paidAmount) {
		if(amountPaid == discountedAmountOwed) {
			isAllPaid = new AtomicBoolean(true);
		}
		cashPayment = cashPayment.add(paidAmount);
	}
	
	public void cashPaymentUnsuccessful() {
		if(amountPaid == discountedAmountOwed) {
			isAllPaid = new AtomicBoolean(true);
		}

	}
	
	
	
	
	
	
	
	
	
	

}
