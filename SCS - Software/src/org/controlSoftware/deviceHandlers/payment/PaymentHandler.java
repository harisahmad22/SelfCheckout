package org.controlSoftware.deviceHandlers.payment;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

import org.controlSoftware.customer.CheckoutSoftware;
import org.controlSoftware.deviceHandlers.membership.ScansMembershipCard;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class PaymentHandler {

	private SelfCheckoutStation station;
	private CheckoutSoftware checkout;
	
	private BigDecimal amountOwed = new BigDecimal(0);
	private BigDecimal amountPaid = new BigDecimal(0);
	
	private PayWithCard cardPaymentHandler;
	private ScansMembershipCard memberCardHandler;
	
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
	private int discount = 0;
	
	
	
	public PaymentHandler(SelfCheckoutStation station, CheckoutSoftware checkout) {
		this.station = station;
		this.checkout = checkout;
		
		this.cardPaymentHandler = new PayWithCard(station, this);
		this.memberCardHandler = new ScansMembershipCard(station, this);
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
	

	/**
	 * Customer chooses to use membership card (scan before payment selection).
	 * Customer should scan/input their number right after this method call.
	 */
	public void useMembershipCard() {
		station.cardReader.enable();
		memberCardHandler.wantToScan();
	}
	
	public void membershipCardScanSuccessful(int discountPercent) {
		validMember = new AtomicBoolean(true); 
		discount = discountPercent;
		memberCardHandler.disableMemberScan();
	}
	
	public void membershipScanUnsuccessful() {
		validMember = new AtomicBoolean(false); 
		memberCardHandler.disableMemberScan();
	}
	
	public void getGiftCardBalance(BigDecimal balance, boolean cardExists) {
		giftCardBalance.add(balance);
		giftCardExists = cardExists;
	}
	
			
			//ask user to swipe or input giftcard
			//display giftCardBlance on screen or maybe customer swipes more giftcards?
			
			//ask user if they want to use a set amount in giftcard or as much as possible
			//proceed with payment
			
			//if giftcard payment covers entire cost, cancel payment for other options
			
	
	
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
		 
	}
	

	/**
	 * Customer choose to use credit card for payment
	 * @param payment
	 */
	public void payWithCredit() {
		 willPayCredit = new AtomicBoolean(true);
		 station.cardReader.enable();
		 
	}
	
	
	public void payWithDebit() {
		willPayDebit = new AtomicBoolean(true);
		station.cardReader.enable();
	
	}
	
	public void payWithGiftcard() {
		willPayGiftCard = new AtomicBoolean(true);
		station.cardReader.enable();
	}
	
	
	/**
	 * Customer choose this as final option they are done with all payment, prints receipt
	 * @return Returns true if everything is paid for
	 */
	public boolean confirmPurchase() {
	
	     
		station.mainScanner.disable();
		station.coinSlot.disable();
		station.banknoteInput.disable();
		station.cardReader.disable();
		

		return true;
		
	}
	
	public void giftCardPaymentSuccessful() {
		
	}
	
	public void giftCardPaymentUnsuccessful() {
		
	}
	
	public void creditPaymentSuccessful() {
		
	}

	public void creditPaymentUnsuccessful() {
		
	}
	
	public void debitPaymentSuccessful() {
		
	}
	
	public void debitPaymentUnsuccessful() {
		
	}
	
	public void cashPaymentSuccessful() {
		
	}
	
	public void cashPaymentUnsuccessful() {
		
	}
	
	
	
	
	
	
	
	
	
	

}
