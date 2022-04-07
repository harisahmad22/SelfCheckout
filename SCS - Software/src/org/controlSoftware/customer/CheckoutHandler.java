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
import org.driver.SelfCheckoutData.StationState;
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
	private SelfCheckoutData stationData;
	private SelfCheckoutSoftware stationSoftware;

	public CheckoutHandler(SelfCheckoutData stationData, SelfCheckoutSoftware stationSoftware) {		
		this.stationData = stationData;
		this.stationSoftware = stationSoftware;
		
	}

		
	public void startCheckout() throws InterruptedException, OverloadException, EmptyException, DisabledException {
		// User has begun checkout
//		stationData.setInCheckout(true); // Could use this as a signal to scale observer that weight is not allowed to change
		
		stationData.changeState(StationState.CHECKOUT);
		
		// Set our expected Weight to the current scale weight
		// Allows scale observer to set weightValid
		stationData.setExpectedWeightCheckout(stationData.getBaggingAreaScale().getCurrentWeight());

		// First Disable scanner
//		stationData.getScanner().disable();
		
		//Attendant Block check
		stationSoftware.attendantBlockCheck();
		
		// TouchScreen method that will ask user if they have their own bags
		// and how many if they do. If user does not have bags they will enter 0 bags
		if (stationData.isFirstCheckout())
		{ //Only prompt user for bags and membership if they haven't already been
			
			//If user has bags, system will change to adding bags state and wait for user to inform system that
			//they have put their bags down
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
			
			//Attendant Block check
			stationSoftware.attendantBlockCheck();
			
			// If the user has bags to add, the weight of all their bags will be added to
			// expectedWeight, which will then
			
			//NEEDS TO BE CHANGED TO JUST HAVE USER PUT BAGS DOWN, SIGNAL GUI
			//THEN UPDATE EXPECTED WEIGHTS TO WHATS ON THE SCALE
			
			// be checked for validity after the user chooses payment options
			stationData.changeState(StationState.ADD_MEMBERSHIP);
			stationData.setWaitingForMembership(true);
			stationSoftware.getTouchScreenSoftware().inputMembershipPrompt(this);
			stationSoftware.getReceiptHandler().setMembershipID(stationData.getMembershipID());
			stationData.setWaitingForMembership(false);
			stationData.changeState(StationState.CHECKOUT);
			
			stationData.setIsFirstCheckout(false);
			
			//Attendant Block check
			stationSoftware.attendantBlockCheck();
		}
		
		//Ask user if they would like to pay partial or full
		BigDecimal paymentAmount = 
				stationSoftware.getTouchScreenSoftware().choosePaymentAmount(
						stationData.getTotalDue(), stationData.getTotalMoneyPaid());

		//Attendant Block check
		stationSoftware.attendantBlockCheck();
		
		// Then prompt touch screen to ask user how they would like to pay
		// Method will block until user input is received
		// Returns an int: 0 = Cash, 1 = Credit, 2 = Debt
		// TESTING - always chooses to pay with cash!
		int paymentMethod = stationSoftware.getTouchScreenSoftware().showPaymentOption(); 

		//Attendant Block check
		stationSoftware.attendantBlockCheck();
		
		// Check if weight is still valid after waiting for user input
		//Hannah Ku
		if (!stationData.getWeightValidCheckout() && !stationData.isWeightOverride()) 
		{
			handleInvalidWeight();
		}
		
		if (paymentMethod == 1) 
		{ 	
			stationData.changeState(StationState.PAY_CREDIT);
//			// Idea for how payWithCreditCard(BigDecimal paymentAmount) will work: 
//			/*
//			 * 1) Inform user to input their card
//			 * 2) Wait until a credit card has been inserted, swiped, tapped
//			 * 3) Once card has been input, get the relevant card data
//			 * 4) Send this to the 'bank' card issuer
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
			stationData.changeState(StationState.CHECKOUT);
			//Attendant Block check
			stationSoftware.attendantBlockCheck();
			
		}
		else if (paymentMethod == 2) 
		{ 
			stationData.changeState(StationState.PAY_DEBIT);
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
			stationData.changeState(StationState.CHECKOUT);
			//Attendant Block check
			stationSoftware.attendantBlockCheck();
			
		}
		else 
		{
			stationData.changeState(StationState.PAY_CASH);
			System.out.println("Cash Payment Chosen");
			payWithCash(paymentAmount);
			stationData.changeState(StationState.CHECKOUT);
			
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
				GiveChange someChange = new GiveChange(stationData.getStationHardware(), changeAmount);
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
			
			//Attendant Block check
			stationSoftware.attendantBlockCheck();
			
			
			stationData.changeState(StationState.CLEANUP);
			handlePostPaymentCleanup();
			stationData.changeState(StationState.CHECKOUT);

			// Maybe Re-enable devices here?
//			stationData.enableAllDevices();

//			stationData.setInCheckout(false);
			stationSoftware.getTouchScreenSoftware().resetToWelcomeScreen();
			stationData.changeState(StationState.WELCOME); //Should maybe move this into Touch screen software
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
//			stationData.enableAllDevices();
			
			//Attendant Block check
			stationSoftware.attendantBlockCheck();
			

//			stationData.setInCheckout(false);
			stationSoftware.getTouchScreenSoftware().returnToAddingItems();
			stationData.changeState(StationState.NORMAL); //Should maybe move this into Touch screen software
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
			
			//Hannah Ku
			if (!stationData.getWeightValidCheckout() && !stationData.isWeightOverride()) { handleInvalidWeight(); }
 
			// CoinValidator/BanknotValidator observer will handle updating the total paid, just need to keep
			// checking
			
			TimeUnit.MILLISECONDS.sleep(50);
			
			//Attendant Block check
			stationSoftware.attendantBlockCheck();
		}
		System.out.println("(TESTING) Finished payWithCash!");
		stationData.resetTotalPaidThisTransaction(); //Payment complete, reset for next payment run
		
		//Attendant Block check
		stationSoftware.attendantBlockCheck();
		
		return;
	}
  
	private void handlePostPaymentCleanup() throws InterruptedException, OverloadException {

		stationData.setInCleanup(true); // Notify Scale that we are waiting for all items on the scale to be removed
								// (weight == 0)
		stationData.resetCheckoutTotals();
		stationSoftware.getTouchScreenSoftware().takeItemsNotification();
//		System.out.println("(TESTING) Current Scale weight at cleanup time: " + this.scale.getCurrentWeight());
		if (stationData.getBaggingAreaScale().getCurrentWeight() > 0.1) { stationData.setWeightValidCheckout(false); } // 0.1 to account for floating point issues
		else { stationData.setWeightValidCheckout(true); }
		
		//Hannah Ku
		while (!stationData.getWeightValidCheckout() && !stationData.isWeightOverride()) 
		{
//			TimeUnit.SECONDS.sleep(1); //Check every second
		}
		// Weight on scale is now equal to 0
		
		//Attendant Block check
		stationSoftware.attendantBlockCheck();		

		stationData.resetCheckoutTotals();
		stationData.setInCleanup(false);
	}

	private void handleInvalidWeight() throws InterruptedException {

//		waitingForWeightChangeEvent.compareAndSet(false, true);

		stationData.disablePaymentDevices();
		stationSoftware.getTouchScreenSoftware().invalidWeightInCheckout();
		// Loop until scale observer reports a valid weight
		//Hannah Ku
		while (!stationData.getWeightValidCheckout() && !stationData.isWeightOverride()) {
//			waitingForWeightChangeEvent.compareAndSet(false, true);
//			TimeUnit.SECONDS.sleep(1); //Check every second
		}

		//Attendant Block check
		stationSoftware.attendantBlockCheck();
		
		// Weight is now valid, unblock and remove touchscreen message
		stationData.enablePaymentDevices();
		stationSoftware.getTouchScreenSoftware().validWeightInCheckout();
	}

	private void handleWaitingForBagWeight() throws InterruptedException {

//		waitingForWeightChangeEvent.compareAndSet(false, true);

		stationData.disablePaymentDevices();
		stationSoftware.getTouchScreenSoftware().addBagsToBaggingArea();
		// Loop until scale observer reports a valid weight
		//Hannah Ku
		while (!stationData.getWeightValidCheckout() && !stationData.isWeightOverride()) {
//			waitingForWeightChangeEvent.compareAndSet(false, true);
//			TimeUnit.SECONDS.sleep(1); //Check every second
		}

		//Attendant Block check
		stationSoftware.attendantBlockCheck();
		
		// Weight is now valid, unblock and remove touchscreen message
		stationData.enablePaymentDevices();
		stationSoftware.getTouchScreenSoftware().bagsPutInBaggingArea();
	}	
}
