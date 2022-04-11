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

		
	public void startCheckout() {
		// User has begun checkout
//		stationData.setInCheckout(true); // Could use this as a signal to scale observer that weight is not allowed to change
		
		//Attendant Block check
		stationSoftware.attendantBlockCheck();				
		
		// Set our expected Weight to the current scale weight
		// Allows scale observer to set weightValid
		try {
			stationData.setExpectedWeight(stationData.getBaggingAreaScale().getCurrentWeight());
		} catch (OverloadException e) {
			System.out.println("Overloaded scale at start of checkout!");
		}

		// TouchScreen method that will ask user if they have their own bags
		// and how many if they do. If user does not have bags they will enter 0 bags
		if (stationData.isFirstCheckout())
		{ //Only prompt user for bags and membership if they haven't already been
			
			//If user has bags, system will change to adding bags state and wait for user to inform system that
			//they have put their bags down
			//Once bags have been put down, the expected scale weight is updated to the current scale weight
			stationData.setIsFirstCheckout(false);
			stationData.changeState(StationState.ADD_BAGS_PROMPT);
			return;
		}
		else 
		{	
			stationData.changeState(StationState.PAYMENT_AMOUNT_PROMPT);
			return;			
		}
		
		//Attendant Block check
//		stationSoftware.attendantBlockCheck();
		
//		// Then prompt touch screen to ask user how they would like to pay
//		// Method will block until user input is received
//		// Returns an int: 0 = Cash, 1 = Credit, 2 = Debt
//		// TESTING - always chooses to pay with cash!
//		stationSoftware.getTouchScreenSoftware().showPaymentMethods(); 
//
//		//Attendant Block check
//		stationSoftware.attendantBlockCheck();
//		
//		// Check if weight is still valid after waiting for user input
//		//Hannah Ku
//		if (!stationData.getWeightValidCheckout() && !stationData.isWeightOverride()) 
////		if (!stationData.getWeightValidCheckout()) 
//		{
//			handleInvalidWeight();
//		}
//		
//		if (paymentMethod == 1) 
//		{ 	
//			stationData.changeState(StationState.PAY_CREDIT);
//
//			//Payment			
//			
//			stationData.changeState(StationState.CHECKOUT);
//			//Attendant Block check
//			stationSoftware.attendantBlockCheck();
//			
//		}
//		else if (paymentMethod == 2) 
//		{ 
//			stationData.changeState(StationState.PAY_DEBIT);
//			
//			//Payment
//			
//			stationData.changeState(StationState.CHECKOUT);
//			//Attendant Block check
//			stationSoftware.attendantBlockCheck();
//			
//		}
//		else 
//		{
//			stationData.changeState(StationState.PAY_CASH);
//			System.out.println("Cash Payment Chosen");
//			payWithCash(stationData.getTransactionPaymentAmount());
//			stationData.changeState(StationState.CHECKOUT);
//			
//		}
//		
//		//Need to handle when they pay partially, maybe payWithCash etc returns a boolean informing us
//		//if more payments are required (false when we need to pay more, true when we dont)
//		//Would have to move post payment logic to this method, but only reset system back to the 
//		//Welcome screen if payWithCash etc returns true. 
//		
//		//================================================================================================
//		
//		BigDecimal changeAmount = BigDecimal.ZERO;
//		// Out of while loop so we can assume user has paid
//		// Check if we have paid full amount
//		handleChange();
//		if (stationData.getTotalMoneyPaid().compareTo(stationData.getTotalDue()) >= 0)
//		{ //Total Paid >= total Due, check for change
//		  //Ask to print Receipt, wait for cleanup, and return to welcome screen
//			
//						
//			// method call to handler that deals with waiting for all items in
//			// bagging area to be picked up before reseting system to be ready for a new
//			// user
//			
//			//Attendant Block check
//			stationSoftware.attendantBlockCheck();
//			
//			
//			stationData.changeState(StationState.CLEANUP);
//			handlePostPaymentCleanup();
//			stationData.changeState(StationState.CHECKOUT);
//
//			// Maybe Re-enable devices here?
////			stationData.enableAllDevices();
//
////			stationData.setInCheckout(false);
//			stationSoftware.getTouchScreenSoftware().resetToWelcomeScreen();
//			stationData.changeState(StationState.WELCOME); //Should maybe move this into Touch screen software
//		}
//		else
//		{ //Total Paid < total Due, Ask to Print Receipt, and return to Adding Items mode 
//			System.out.println("Total Due: " + stationData.getTotalDue());
//			System.out.println("Total Paid: " + stationData.getTotalMoneyPaid());
//			stationSoftware.getReceiptHandler().setFinalTotal(stationData.getTotalDue().toString());
//			stationSoftware.getReceiptHandler().setMoneyPaid(stationData.getTotalMoneyPaid().toString());
//			stationSoftware.getReceiptHandler().setFinalChange(changeAmount.toString());
//			
//			// Prompt touch screen to ask user if they would like a receipt
//			stationSoftware.getTouchScreenSoftware().askToPrintReceipt(stationSoftware.getReceiptHandler());
//			
//			// Maybe Re-enable devices here?
////			stationData.enableAllDevices();
//			
//			//Attendant Block check
//			stationSoftware.attendantBlockCheck();
//			
//
////			stationData.setInCheckout(false);
//			stationSoftware.getTouchScreenSoftware().returnToAddingItems();
//			stationData.changeState(StationState.NORMAL); //Should maybe move this into Touch screen software
//		}
		//================================================================================================
	}

	public void handleChange() {
		BigDecimal changeAmount = BigDecimal.ZERO;
		
		if (stationData.getTotalMoneyPaid().compareTo(stationData.getTotalDue()) == 1)
		{ //Payment has exceeded totalDue, get the change amount
			changeAmount = stationData.getTotalMoneyPaid().subtract(stationData.getTotalDue());
			GiveChange someChange = new GiveChange(stationData.getStationHardware(), changeAmount);
	        
			try {
				someChange.dispense();
			} catch (EmptyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DisabledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			stationSoftware.getTouchScreenSoftware().informChangeDispensed();
		}//Otherwise change is defaulted to 0 when a partial payment is completed
	
		stationSoftware.getReceiptHandler().setFinalTotal(stationData.getTotalDue().toString());
		stationSoftware.getReceiptHandler().setMoneyPaid(stationData.getTotalMoneyPaid().toString());
		stationSoftware.getReceiptHandler().setFinalChange(changeAmount.toString());
	
	}


	// This method will be called by the GUI after prompting user to select a
	// payment method,
	// We will just call it directly when testing to simulate the GUI interaction
	public void payWithCash(BigDecimal amount) throws InterruptedException, OverloadException, EmptyException, DisabledException {
        
		//Cash payments should only be allowed once this method is entered!
		BigDecimal amountToPay = amount;
		BigDecimal initialTotalDue = stationData.getTotalDue();
		stationData.setExpectedWeight(stationData.getExpectedWeight());
		System.out.println("Starting pay with cash, total due: " + initialTotalDue);
		System.out.println("Starting pay with cash, total paid so far: " + stationData.getTotalMoneyPaid());
		System.out.println("Starting pay with cash, total paid this round: " + stationData.getTotalPaidThisTransaction());
		System.out.println("Starting pay with cash, amount to pay: " + amountToPay);

		stationData.getScanner().enable();
		
		while (stationData.getTotalPaidThisTransaction().compareTo(amountToPay) == -1) { // compareTo returns -1 if less than, 0 if equal, and 1 if greater than
//			
//			if (stationData.getExpectedWeight() > stationData.getExpectedWeightCheckout())
//			{//Checkout's expected weight has changed, this will happen when a user scans an item
//			 //Mid payment, we need to update amountToPay to account for the cost of the new item
//			 //Even if the user chose partial payment, make them pay for the just added item
//				amountToPay = amountToPay.add((stationData.getTotalDue().subtract(initialTotalDue)));
//				initialExpectedWeight = stationData.getExpectedWeightCheckout();
//			}
//			
			//Hannah Ku
			if (!stationData.getWeightValidCheckout() && !stationData.isWeightOverride()) { handleInvalidWeight(); }
//			if (!stationData.getWeightValidCheckout()) { handleInvalidWeight(); }
 
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
//		System.out.println("(TESTING) Current Scale weight at cleanup time: " + this.scale.getCurrentWeight());
		if (stationData.getBaggingAreaScale().getCurrentWeight() > 0.1) { stationData.setWeightValidCheckout(false); } // 0.1 to account for floating point issues
		else { stationData.setWeightValidCheckout(true); }
		
		//Hannah Ku
		while (!stationData.getWeightValidCheckout() && !stationData.isWeightOverride()) 
//		while (!stationData.getWeightValidCheckout()) 
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
		
		// Loop until scale observer reports a valid weight
		//Hannah Ku
		while (!stationData.getWeightValidCheckout() && !stationData.isWeightOverride()) {
//		while (!stationData.getWeightValidCheckout()) {
//			waitingForWeightChangeEvent.compareAndSet(false, true);
//			TimeUnit.SECONDS.sleep(1); //Check every second
		}

		//Attendant Block check
		stationSoftware.attendantBlockCheck();
		
		// Weight is now valid, unblock and remove touchscreen message
		stationData.enablePaymentDevices();
	}

	private void handleWaitingForBagWeight() throws InterruptedException {

//		waitingForWeightChangeEvent.compareAndSet(false, true);

		stationData.disablePaymentDevices();
		
		// Loop until scale observer reports a valid weight
		//Hannah Ku
		while (!stationData.getWeightValidCheckout() && !stationData.isWeightOverride()) {
//		while (!stationData.getWeightValidCheckout()) {
//			waitingForWeightChangeEvent.compareAndSet(false, true);
//			TimeUnit.SECONDS.sleep(1); //Check every second
		}

		//Attendant Block check
		stationSoftware.attendantBlockCheck();
		
		// Weight is now valid, unblock and remove touchscreen message
		stationData.enablePaymentDevices();
	}	
}
