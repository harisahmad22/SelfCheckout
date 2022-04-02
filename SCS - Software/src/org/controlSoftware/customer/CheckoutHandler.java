//Brody Long - 30022870 
//Shufan Zhai - 30117333

package org.controlSoftware.customer;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.controlSoftware.data.NegativeNumberException;
import org.controlSoftware.deviceHandlers.ReceiptHandler;
import org.controlSoftware.deviceHandlers.payment.GiveChange;
import org.controlSoftware.deviceHandlers.payment.PayWithCreditCard;
import org.controlSoftware.deviceHandlers.payment.PayWithDebitCard;
import org.controlSoftware.general.TouchScreenSoftware;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutSoftware;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.CoinSlot;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation; // needed for GiveChange

public class CheckoutHandler {

//	private static BigDecimal totalDue = BigDecimal.ZERO;
//	private static BigDecimal totalMoneyPaid = BigDecimal.ZERO;
//	private static BigDecimal totalPaidThisTransaction = BigDecimal.ZERO;
////    private SelfCheckoutStation station; //needed for GiveChange
////	private TouchScreenSoftware touchScreen;
////	private BarcodeScanner scanner;
////	private BanknoteSlot banknoteSlot;
////	private CoinSlot coinSlot;
////	private ElectronicScale scale;
////	private CardReader reader;
//	private AtomicBoolean inCheckout = new AtomicBoolean(false);
//	private AtomicBoolean usingOwnBags = new AtomicBoolean(false);
//	private AtomicBoolean inCleanup = new AtomicBoolean(false);
//	private AtomicBoolean weightValid = new AtomicBoolean(true);
//	private AtomicBoolean cardSwiped = new AtomicBoolean(false);
//	private AtomicBoolean cardTapped = new AtomicBoolean(false);
//	private AtomicBoolean cardInserted = new AtomicBoolean(false);
//	private AtomicBoolean waitingForMembership = new AtomicBoolean(false);

	//Might not be needed
//	private AtomicBoolean waitingForCash = new AtomicBoolean(false);
//	private AtomicBoolean waitingForCreditCard = new AtomicBoolean(false);
//	private AtomicBoolean waitingForDebtCard = new AtomicBoolean(false);
	//Might not be needed
	
//	private double expectedWeight;
//	private double bagWeight = 50; // Should have this be configurable
//	private String membershipNum = "null";
//	private String creditNum = "null";
//	private ReceiptHandler receiptHandler;
//	private boolean isFirstCheckout = true;
	
//	private PayWithDebitCard debitCard;
//	private PayWithCreditCard creditCard;
	private SelfCheckoutData stationData;
	private SelfCheckoutSoftware stationSoftware;

	public CheckoutHandler(SelfCheckoutData stationData, SelfCheckoutSoftware stationSoftware) {
//					TouchScreenSoftware touchScreenSoftware, 
//					BarcodeScanner scanner, 
//					BanknoteSlot banknoteSlot, 
//					CoinSlot coinSlot,
//					ElectronicScale scale,
//					SelfCheckoutStation station, //needed for GiveChange
//					ReceiptHandler receiptHandler, 
////					PayWithDebitCard debitCard,
////					PayWithCreditCard creditCard,
//					CardReader reader) 
//	{ 
//		this.stationData = stationData;
//		this.station = stationData.getStation(); //needed for GiveChange
//		this.touchScreen = stationSoftware.getTouchScreenSoftware();
//		this.scanner = stationData.getScanner();
//		this.banknoteSlot = stationData.getBanknoteInputSlot();
//		this.coinSlot = stationData.getCoinSlot();
//		this.scale = stationData.getScale();
//		this.receiptHandler = stationSoftware.getReceiptHandler();
////		this.debitCard = debitCard;
////		this.creditCard = creditCard;
//		this.reader = stationData.getCardReader();
		
		this.stationData = stationData;
		this.stationSoftware = stationSoftware;
		
	}

	public void startCheckout() throws InterruptedException, OverloadException, EmptyException, DisabledException {
		// User has begun checkout
		stationData.setInCheckout(true); // Could use this as a signal to scale observer that weight is not allowed to change

		// Set our expected Weight to the current scale weight
		// Allows scale observer to set weightValid
		stationData.setExpectedWeightCheckout(stationData.getScale().getCurrentWeight());

		// First Disable scanner
		stationData.getScanner().disable();
		
		// TouchScreen method that will ask user if they have their own bags
		// and how many if they do. If user does not have bags they will enter 0 bags
		if (stationData.isFirstCheckout())
		{ //Only prompt user for bags and membership if they haven't already been
			stationSoftware.getTouchScreenSoftware().usingOwnBagsPrompt();
			int bag_num = stationSoftware.getTouchScreenSoftware().getNumberOfPersonalBags();
			double bagWeight = stationData.getBagWeight();
			if (bag_num > 0)
			{
				double newExpectedWeight = stationData.getExpectedWeightCheckout() + (bag_num * bagWeight);
				stationData.setExpectedWeightCheckout(newExpectedWeight);
				 // If user selects 0 bags expected does not change
				stationData.setWeightValidCheckout(false);
				handleWaitingForBagWeight();
			}
			
			// If the user has bags to add, the weight of all their bags will be added to
			// expectedWeight, which will then
			// be checked for validity after the user chooses payment options
			stationData.setWaitingForMembership(true);
			stationSoftware.getTouchScreenSoftware().inputMembershipPrompt(this);
			stationSoftware.getReceiptHandler().setMembershipID(stationData.getMembershipID());
			stationData.setWaitingForMembership(false);
			
			stationData.setIsFirstCheckout(false); 
		}
		
		//Ask user if they would like to pay partial or full
		BigDecimal paymentAmount = 
				stationSoftware.getTouchScreenSoftware().choosePaymentAmount(
						stationData.getTotalDue(), stationData.getTotalMoneyPaid());

		// Then prompt touch screen to ask user how they would like to pay
		// Method will block until user input is received
		// Returns an int: 0 = Cash, 1 = Credit, 2 = Debt
		// TESTING - always chooses to pay with cash!
		int paymentMethod = stationSoftware.getTouchScreenSoftware().showPaymentOption(); 

		// Check if weight is still valid after waiting for user input
		if (!stationData.getWeightValidCheckout()) 
		{
			handleInvalidWeight();
		}
		
		if (paymentMethod == 1) 
		{ 
//			// Idea for how payWithCreditCard() will work: 
//			/*
//			 * 1) Inform user to input their card
//			 * 2) Wait until a credit card has been inserted, swiped, tapped
//			 * 3) Once card has been input, get the relevant card data
//			 * 4) Send this to the 'bank' stub
//			 * 5) if the bank authorizes the card data, then add paymentAmount to Checkout's totalMoneyPaid
//			 * 6) return 
//			 */
//			boolean cardPaymentVerified = false;
//			int cardPaymentMethod = touchScreen.showCardPaymentOption(); 
//			if (cardPaymentMethod == 0) {
//				creditCard.cardInserted(reader);
//				
//			}
//			
//			else if (cardPaymentMethod == 1) {
//				creditCard.cardTapped(reader);
//			}
//
//			else {
//				creditCard.cardSwiped(reader);
//			}	
//			
//			cardPaymentVerified = creditCard.checkBankClientInfo(reader, paymentAmount);
//			
//			if(cardPaymentVerified == false) {
//				System.out.println("Transaction Error: Please try again");
////				startCheckout();
//			} else {
//				creditCard.cardRemoved(reader);
//			}
		}
		else if (paymentMethod == 2) 
		{ 
//			boolean cardPaymentVerified = false;
//			int cardPaymentMethod = touchScreen.showCardPaymentOption(); 
//			if (cardPaymentMethod == 0) {
//				debitCard.cardInserted(reader);
//				
//			}
//			
//			else if (cardPaymentMethod == 1) {
//				debitCard.cardTapped(reader);
//			}
//
//			else {
//				debitCard.cardSwiped(reader);
//			}	
//			
//			cardPaymentVerified = debitCard.verifyBankingInfo(reader, paymentAmount);
//			
//			if(cardPaymentVerified == false) {
//				System.out.println("Transaction Error: Please try again");
////				startCheckout();
//			} else {
//				debitCard.cardRemoved(reader);
//			}
      
		}
		else 
		{
			System.out.println("Cash Payment Chosen");
			payWithCash(paymentAmount);
		}
		
		//Need to handle when they pay partially, maybe payWithCash etc returns a boolean informing us
		//if more payments are required (false when we need to pay more, true when we dont)
		//Would have to move post payment logic to this method, but only reset system back to the 
		//Welcome screen if payWithCash etc returns true. 
		
		//================================================================================================
		
		BigDecimal changeAmount = BigDecimal.ZERO;
		// Out of while loop so we can assume user has paid
		// Check if we have paid full amount
		
		if (stationData.getTotalMoneyPaid().compareTo(stationData.getTotalDue()) >= 0)
		{ //Total Paid >= total Due, check for change
		  //Ask to print Receipt, wait for cleanup, and return to welcome screen
			
			if (stationData.getTotalMoneyPaid().compareTo(stationData.getTotalDue()) == 1)
			{ //Payment has exceeded totalDue, get the change amount
				changeAmount = stationData.getTotalMoneyPaid().subtract(stationData.getTotalDue());
				GiveChange someChange = new GiveChange(stationData.getStation(), changeAmount);
	            someChange.dispense();
				stationSoftware.getTouchScreenSoftware().informChangeDispensed();
			}//Otherwise change is defaulted to 0 when a partial payment is completed

			stationSoftware.getReceiptHandler().setFinalTotal(stationData.getTotalDue().toString());
			stationSoftware.getReceiptHandler().setMoneyPaid(stationData.getTotalMoneyPaid().toString());
			stationSoftware.getReceiptHandler().setFinalChange(changeAmount.toString());
			
			// Prompt touch screen to ask user if they would like a receipt
			stationSoftware.getTouchScreenSoftware().askToPrintReceipt(stationSoftware.getReceiptHandler());
			
			// method call to handler that deals with waiting for all items in
			// bagging area to be picked up before reseting system to be ready for a new
			// user
			handlePostPaymentCleanup();

			// Maybe Re-enable devices here?
			stationData.enableDevices();

			stationData.setInCheckout(false);
			stationSoftware.getTouchScreenSoftware().resetToWelcomeScreen();
		}
		else
		{ //Total Paid < total Due, Ask to Print Receipt, and return to Adding Items mode 
			System.out.println("Total Due: " + stationData.getTotalDue());
			System.out.println("Total Paid: " + stationData.getTotalMoneyPaid());
			stationSoftware.getReceiptHandler().setFinalTotal(stationData.getTotalDue().toString());
			stationSoftware.getReceiptHandler().setMoneyPaid(stationData.getTotalMoneyPaid().toString());
			stationSoftware.getReceiptHandler().setFinalChange(changeAmount.toString());
			
			// Prompt touch screen to ask user if they would like a receipt
			stationSoftware.getTouchScreenSoftware().askToPrintReceipt(stationSoftware.getReceiptHandler());
			
			// Maybe Re-enable devices here?
			stationData.enableDevices();

			stationData.setInCheckout(false);
			stationSoftware.getTouchScreenSoftware().returnToAddingItems();
		}
		//================================================================================================
	}

	// This method will be called by the GUI after prompting user to select a
	// payment method,
	// We will just call it directly when testing to simulate the GUI interaction
	public void payWithCash(BigDecimal amount) throws InterruptedException, OverloadException, EmptyException, DisabledException {
        
		//Cash payments should only be allowed once this method is entered!
		BigDecimal amountToPay = amount;
		BigDecimal initialTotalDue = stationData.getTotalDue();
		double initialExpectedWeight = stationData.getExpectedWeightCheckout();
		System.out.println("Starting pay with cash, total due: " + initialTotalDue);
		System.out.println("Starting pay with cash, total paid so far: " + stationData.getTotalMoneyPaid());
		System.out.println("Starting pay with cash, total paid this round: " + stationData.getTotalPaidThisTransaction());
		System.out.println("Starting pay with cash, amount to pay: " + amountToPay);

		stationData.getScanner().enable();
		
		while (stationData.getTotalPaidThisTransaction().compareTo(amountToPay) == -1) { // compareTo returns -1 if less than, 0 if equal, and 1 if greater than
			
			if (stationData.getExpectedWeightCheckout() > initialExpectedWeight)
			{//Checkout's expected weight has changed, this will happen when a user scans an item
			 //Mid payment, we need to update amountToPay to account for the cost of the new item
			 //Even if the user chose partial payment, make them pay for the just added item
				amountToPay = amountToPay.add((stationData.getTotalDue().subtract(initialTotalDue)));
				initialExpectedWeight = stationData.getExpectedWeightCheckout();
			}
			
			if (!stationData.getWeightValidCheckout()) { handleInvalidWeight(); }
 
			// CoinValidator/BanknotValidator observer will handle updating the total paid, just need to keep
			// checking
			
			TimeUnit.MILLISECONDS.sleep(50); 
		}
		System.out.println("(TESTING) Finished payWithCash!");
		stationData.resetTotalPaidThisTransaction(); //Payment complete, reset for next payment run
		return;
	}
  
	private void handlePostPaymentCleanup() throws InterruptedException, OverloadException {

		stationData.setInCleanup(true); // Notify Scale that we are waiting for all items on the scale to be removed
								// (weight == 0)
		stationData.resetCheckoutTotals();
		stationSoftware.getTouchScreenSoftware().takeItemsNotification();
//		System.out.println("(TESTING) Current Scale weight at cleanup time: " + this.scale.getCurrentWeight());
		if (stationData.getScale().getCurrentWeight() > 0.1) { stationData.setWeightValidCheckout(false); } // 0.1 to account for floating point issues
		else { stationData.setWeightValidCheckout(true); }
		
		while (!stationData.getWeightValidCheckout()) 
		{
//			TimeUnit.SECONDS.sleep(1); //Check every second
		}
		// Weight on scale is now equal to 0

		stationData.resetCheckoutTotals();
		stationData.setInCleanup(false);
	}

	private void handleInvalidWeight() throws InterruptedException {

//		waitingForWeightChangeEvent.compareAndSet(false, true);

		stationData.disablePaymentDevices();
		stationSoftware.getTouchScreenSoftware().invalidWeightInCheckout();
		// Loop until scale observer reports a valid weight
		while (!stationData.getWeightValidCheckout()) {
//			waitingForWeightChangeEvent.compareAndSet(false, true);
//			TimeUnit.SECONDS.sleep(1); //Check every second
		}

		// Weight is now valid, unblock and remove touchscreen message
		stationData.enablePaymentDevices();
		stationSoftware.getTouchScreenSoftware().validWeightInCheckout();
	}

	private void handleWaitingForBagWeight() throws InterruptedException {

//		waitingForWeightChangeEvent.compareAndSet(false, true);

		stationData.disablePaymentDevices();
		stationSoftware.getTouchScreenSoftware().addBagsToBaggingArea();
		// Loop until scale observer reports a valid weight
		while (!stationData.getWeightValidCheckout()) {
//			waitingForWeightChangeEvent.compareAndSet(false, true);
//			TimeUnit.SECONDS.sleep(1); //Check every second
		}

		// Weight is now valid, unblock and remove touchscreen message
		stationData.enablePaymentDevices();
		stationSoftware.getTouchScreenSoftware().bagsPutInBaggingArea();
	}

	
//	private void resetWeightFlags() {
//		// Reset weight change flags
////		waitingForWeightChangeEvent.set(false);
//		weightValid.set(false);
//	}

//	private void disablePaymentDevices() {
//		// TODO Auto-generated method stub
//		coinSlot.disable();
//		banknoteSlot.disable();
//	}
//
//	private void enablePaymentDevices() {
//		// TODO Auto-generated method stub
//		coinSlot.enable();
//		banknoteSlot.enable();
//	}
//
//	// Disable all devices associated with this observer
//	private void disableDevices() {
//		scale.disable();
//		scanner.disable();
//		disablePaymentDevices();
//	}
//
//	// Enable all devices associated with this observer
//	private void enableDevices() {
//		scale.enable();
//		scanner.enable();
//		enablePaymentDevices();
//	}

//	public boolean isWeightValid() {
//		return weightValid.get();
//	}
//
//	public void setWeightValid(boolean validity) {
//		weightValid.set(validity);
//	}
//
//	public static void addToTotalCost(BigDecimal scannedItemPrice) {
//		totalDue = getTotalDue().add(scannedItemPrice);
//
//	}
//
//	public static void addToTotalPaid(BigDecimal amount) {
//		totalMoneyPaid = totalMoneyPaid.add(amount);
//		
//		//This will be used to track how much money has been paid during one
//		//payment run
//		totalPaidThisTransaction  = totalPaidThisTransaction.add(amount);
//
//	}
//
//	public static void setTotalCost(BigDecimal cost) {
//		totalDue = cost;
//
//	}
//
//	public static void setTotalPaid(BigDecimal paid) {
//		totalMoneyPaid = paid;
//
//	}
//
//	public void setInCheckout(boolean bool) {
//		inCheckout.set(bool);
//
//	}
//
//	public boolean isInCheckout() {
//		return inCheckout.get();
//	}
//
//	public BigDecimal getTotalMoneyPaid() {
//		return totalMoneyPaid;
//	}
//
//	public BigDecimal getTotalCost() {
//		return getTotalDue();
//	}
//
//	public boolean isInCleanup() {
//		return inCleanup.get();
//	}
//
//	public void setInCleanup(boolean bool) {
//		inCleanup.set(bool);
//	}
//
//	public static void resetCheckoutTotals() {
//		totalMoneyPaid = BigDecimal.ZERO;
//		totalDue = BigDecimal.ZERO;
//	}
//
//	public double getExpectedWeight() {
//		// TODO Auto-generated method stub
//		return expectedWeight;
//	}
//
//	public void setExpectedWeight(double weight) {
//		expectedWeight = weight;
//	}

//	public boolean isUsingOwnBags() {
//		return usingOwnBags.get();
//	}
//	
//	public void configureBagWeight() {
//	
//			try (Scanner weightInput = new Scanner(System.in)) {
//				System.out.println("Enter new weight of bags");
//				bagWeight = weightInput.nextDouble();
//				// configure new weight of bags
//				if (bagWeight < 0) {
//					throw new NegativeNumberException();
//				}
//			} catch (InputMismatchException e) {
//				System.out.println("Must enter a valid weight for bags!");
//			}	
//	}
//
//	public int compareTotals() {
//		return totalMoneyPaid.compareTo(getTotalDue());
//	}
//	
//	public void resetMembershipInfo() {
//		membershipNum = "null";
//	}
//
//	public void setMembershipNumber(String num) {
//		membershipNum = num;
//	}
//	
//	public String getMembershipNumber() {
//		return membershipNum;
//	}
//	
//	public boolean isWaitingForMembership() {
//		return waitingForMembership.get();
//	}
//	
//	public void setWaitingForMembership(boolean bool) {
//		waitingForMembership.set(bool);;
//	}
//	
//	public boolean getCardSwiped() {
//		return cardSwiped.get();
//	}
//	
//	public void setCardSwiped(boolean bool) {
//		cardSwiped.set(bool);
//	}
//	
//	public void setCreditNumber(String num) {
//		creditNum = num;
//	}
//
//	public double getBagWeight() {
//		return bagWeight;
//	}
//
//	public boolean isWaitingForCreditCard() {
//		return waitingForCreditCard.get();
//	}
//	
//	public void setWaitingForCreditCard(boolean bool) {
//		waitingForCreditCard.set(bool);
//	}

//	public void updateTouchScreen(TouchScreenSoftware ts)
//	{//Used for when we have to change the touchScreen's input stream during testing
//		this.stationSoftware.getTouchScreenSoftware() = ts;		
//	}

//	public static BigDecimal getTotalDue() {
//		return totalDue;
//	}
//
////	public static void setTotalPaidThisTransaction(BigDecimal val) {
////		totalPaidThisTransaction = val;
////		
////	}
	
}
