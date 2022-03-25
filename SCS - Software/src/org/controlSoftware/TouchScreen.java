//Brody Long - 30022870 

package org.controlSoftware;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lsmr.selfcheckout.devices.AbstractDevice;
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
	public int numberOfPersonalBags;

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

	public void askToPrintReceipt() {
		System.out.println("Would You like to print a receipt?");
		askedToPrintReceipt.set(true);
	}

	public void showPaymentOption() throws InterruptedException {
		System.out.println("How would you like to pay? Coins or Banknotes.");
		paymentOptionsDisplayed.set(true);
		TimeUnit.SECONDS.sleep(3); // Sleep for 3 seconds to simulate user selecting option
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
			numberOfPersonalBags = bagInput.nextInt();
			// determine # of bags customer brought
		} catch (InputMismatchException inputMismatchExcpetion) {
			System.out.println("Sorry, please try again!");
		}
	}

	public int getNumberOfPersonalBags() {
		return numberOfPersonalBags;
	}
}
