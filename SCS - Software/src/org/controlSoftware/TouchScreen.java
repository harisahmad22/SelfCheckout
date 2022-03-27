//Brody Long - 30022870 

package org.controlSoftware;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.TouchScreenObserver;

public class TouchScreen implements TouchScreenObserver {

	// (Brody) Methods made here will handle inputs from the simulated touch screen
	// / GUI
	// E.g Customer wants to checkout, they press the checkout button and all
	// devices/observers not
	// relevant to checking out will be disabled, and enable all payment related
	// devices/observers if necessary
	// then call the method(s) in the Checkout class to handle the logic of checking
	// out.

	public AtomicBoolean waitingForItemAfterScanDetected = new AtomicBoolean(false);
	public AtomicBoolean waitingForItemAfterScanCorrected = new AtomicBoolean(false);
	public AtomicBoolean scanWeightIssueDetected = new AtomicBoolean(false);
	public AtomicBoolean scanWeightIssueCorrected = new AtomicBoolean(false);
	public AtomicBoolean overloadDetected = new AtomicBoolean(false);
	public AtomicBoolean askedToPrintReceipt = new AtomicBoolean(false);
	public AtomicBoolean paymentOptionsDisplayed = new AtomicBoolean(false);
	public AtomicBoolean changeDispensed = new AtomicBoolean(false);
	public AtomicBoolean invalidWeightInCheckoutCorrected = new AtomicBoolean(false);
	public AtomicBoolean invalidWeightInCheckoutDetected = new AtomicBoolean(false);
	public AtomicBoolean resetSuccessful = new AtomicBoolean(false);
	public AtomicBoolean invalidBarcodeDetected = new AtomicBoolean(false);
	public AtomicBoolean informedToTakeItems = new AtomicBoolean(false);
	public int numberOfPersonalBags = 0;

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		System.out.println("A device of type <" + device.getClass().getSimpleName() + "> has been enabled!");
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		System.out.println("A device of type <" + device.getClass().getSimpleName() + "> has been disabled!");
	}

	public void waitingForScannedItem() {
		System.out.println("Please place the item in the bagging area!");
		// Put message on screen that does not go away until weight is valid
		waitingForItemAfterScanDetected.set(true);
	}

	public void doneWaitingForScannedItem() {
		System.out.println("Item has been placed in the bagging area!");
		// Remove message from screen and resume previous state
		waitingForItemAfterScanCorrected.set(true);
	}

	public void invalidWeightAfterScan() {
		scanWeightIssueDetected.set(true);
		System.out.println("Invalid Weight detected after Scan! Please correct the issue before continuing!");
		// Put message on screen that does not go away until weight is valid

	}

	public void validWeightAfterScan() {
		System.out.println("Weight issue corrected! Thank You!");
		// Remove message from screen and resume previous state
		scanWeightIssueCorrected.set(true);
	}

	public void scaleOverloaded() {
		System.out.println("Scale max weight exceeded! Please contact an attendant for assistance");
		// Put message on screen that does not go away until system is not overloaded
		overloadDetected.set(true);
	}

	public void overloadFixed() {
		System.out.println("Scale max weight issue corrected! Please continue your purchase");
		// Remove message from screen and resume previous state
		overloadDetected.set(false);
	}

	public void askToPrintReceipt(ReceiptHandler receipt) {
		System.out.println("Would You like to print a receipt?");
		askedToPrintReceipt.set(true);
		
		//Until GUI is implemented, for testing purposes we will print the receipt here
		receipt.printReceipt();
		
	}

	public int showPaymentOption() throws InterruptedException {
		System.out.println("How would you like to pay? Cash or Card? (Not Implemented yet!)");
		paymentOptionsDisplayed.set(true);
		TimeUnit.SECONDS.sleep(2); // Sleep for 2 seconds to simulate user selecting option
		
		//Random values for testing
//		Random rand = new Random();
//		int choice = rand.nextInt(3);
//		return choice; //0 = Cash, 1 = Credit, 2 = Debt
		
		return 0; //For now always default to cash
	}

	public void informChangeDispensed() {
		System.out.println("Change has been dispensed!");
		changeDispensed.set(true);
	}

	public void validWeightInCheckout() {
		System.out.println("Weight issue During Checkout corrected!");
		invalidWeightInCheckoutCorrected.set(true);
	}

	public void invalidWeightInCheckout() {
		System.out.println("Invalid Weight Detected during Checkout! Please correct the issue before continuing!");
		invalidWeightInCheckoutDetected.set(true);

	}

	public void resetToWelcomeScreen() throws InterruptedException {
		System.out.println("Thank You for shopping with us!");

		TimeUnit.SECONDS.sleep(1);

		System.out.println("Returning to Welcome Screen.");

		TimeUnit.SECONDS.sleep(1);

		System.out.println("Welcome! Press Start to continue.");

		resetSuccessful.set(true);
	}

	public void invalidBarcodeScanned() {
		System.out.println("ERROR! COULD NOT FIND BARCODE IN LOOKUP!");
		invalidBarcodeDetected.set(true);

	}

	public void takeItemsNotification() {
		System.out.println("Please take your items!");
		informedToTakeItems.set(true);

	}

	public void usingOwnBagsPrompt() {
		try (Scanner bagInput = new Scanner(System.in)) {
			System.out.println("How many bags did you bring today?");
//			numberOfPersonalBags = bagInput.nextInt();
			if (numberOfPersonalBags < 0 || numberOfPersonalBags > 10) { throw new InputMismatchException(); }
			// determine # of bags customer brought
			
			//Brody - Should maybe limit to 10 bags max? can worry about when doing GUI
		} catch (InputMismatchException inputMismatchExcpetion) {
			System.out.println("Sorry, please try again!");
		}
	}

	public int getNumberOfPersonalBags() {
		return numberOfPersonalBags;
	}

	public BigDecimal choosePaymentAmount(BigDecimal totalDue) {
		System.out.println("Would you like you make a partial payment?");
		Random rand = new Random();
		int choice = rand.nextInt(2);
		BigDecimal partialPaymentAmount = new BigDecimal("27.35"); //Test value
		if (choice == 0)
		{ //Simulate choosing partial
			System.out.println("Partial Payment");
			if (partialPaymentAmount.compareTo(BigDecimal.ZERO) <= 0)
			{ //For now if user chooses to pay <= $0, default to full payment
				return totalDue;
			}
			else if (partialPaymentAmount.compareTo(totalDue) == 1)
			{ //If user enters in more than totalDue, default to full payment
				return totalDue;
			}
			return partialPaymentAmount;
		}
		else 
		{//Simulate paying full amount
			return totalDue; 
		}
	}
}
