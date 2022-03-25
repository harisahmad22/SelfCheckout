//Brody Long - 30022870 
//Kamrul Ahsan Noor- 30078754

package org.controlSoftware;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.CoinSlot;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.OverloadException;

public class Checkout {

	private static BigDecimal totalCost = BigDecimal.ZERO;
	private static BigDecimal totalMoneyPaid = BigDecimal.ZERO;
	private TouchScreen touchScreen;
	private BarcodeScanner scanner;
	private BanknoteSlot banknoteSlot;
	private CoinSlot coinSlot;
	private ElectronicScale scale;
	private AtomicBoolean inCheckout = new AtomicBoolean(false);
	private AtomicBoolean usingOwnBags = new AtomicBoolean(false);
	private AtomicBoolean inCleanup = new AtomicBoolean(false);
	private AtomicBoolean weightValid = new AtomicBoolean(true);
	private double expectedWeight;
	private static final double BAG_WEIGHT = 50; //Should have this be configurable

	public Checkout(TouchScreen touchScreen, BarcodeScanner scanner, BanknoteSlot banknoteSlot, CoinSlot coinSlot,
			ElectronicScale scale) {

		this.touchScreen = touchScreen;
		this.scanner = scanner;
		this.banknoteSlot = banknoteSlot;
		this.coinSlot = coinSlot;
		this.scale = scale;

	}

	public void startCheckout() throws InterruptedException, OverloadException {
		// User has begun checkout
		inCheckout.set(true); // Could use this as a signal to scale observer that weight is not allowed to
								// change

		// Set our expected Weight to the current scale weight
		// Allows scale observer to set weightValid
		expectedWeight = scale.getCurrentWeight();

		// First Disable scanner
		scanner.disable();
		
		//-------------Brody------------------
		
		//TouchScreen method that will ask user if they have their own bags
		//and how many if they do. If user does not have bags they will enter 0 bags
		touchScreen.usingOwnBagsPrompt();
		expectedWeight += (touchScreen.getNumberOfPersonalBags() * BAG_WEIGHT); // If user selcts 0 bags expected does not change
		//If the user has bags to add, the weight of all their bags will be added to expectedWeight, which will then
		//be checked for validity after the user chooses payment options
		
		//-------------Brody------------------
		
		
		// Then prompt touch screen to ask user how they would like to pay
		// Method will block until user input is received
		touchScreen.showPaymentOption();

		// Check if weight is still valid after waiting for user input
		if (!weightValid.get()) {
			handleInvalidWeight();
		}
		// Done
		return;
	}

	// This method will be called by the GUI after prompting user to select a
	// payment method,
	// We will just call it directly when testing to simulate the GUI interaction
	public void payWithCoins() throws InterruptedException, OverloadException {
		// Maybe disable Banknote slot?
		banknoteSlot.disable();

		while (compareTotals() < 0) { // compareTo returns -1 if less than, 0 if equal, and 1 if greater than

			if (!weightValid.get()) {
				handleInvalidWeight();
			}

			// CoinValidator observer will handle updating the total paid, just need to keep
			// checking
		}

		// Out of while loop so we can assume user has paid, may need change but
		// worry about that for next iteration
		BigDecimal changeAmount = totalMoneyPaid.subtract(totalCost);
		if (changeAmount.compareTo(new BigDecimal(0)) == 1) {
			// Handle giving out change here
			touchScreen.informChangeDispensed();
		}

		// Prompt touch screen to ask user if they would like a receipt
		touchScreen.askToPrintReceipt();

		// method call to handler that deals with waiting for all items in
		// bagging area to be picked up before reseting system to be ready for a new
		// user
		handlePostPaymentCleanup();

		// Maybe Re-enable devices here?
		enableDevices();

		inCheckout.set(false);
		touchScreen.resetToWelcomeScreen();
		return;
	}

	// This method will be called by the GUI after prompting user to select a
	// payment method,
	// We will just call it directly when testing to simulate the GUI interaction
	public void payWithBanknotes() throws InterruptedException, OverloadException {
		// Maybe disable coin slot?

		while (compareTotals() < 0) { // compareTotals returns -1 if less than, 0 if equal, and 1 if greater than

			if (!weightValid.get()) {
				handleInvalidWeight();
			}

			// BanknoteValidator observer will handle updating the total paid, just need to
			// keep checking
		}

		// Out of while loop so we can assume user has paid, may need change but
		// worry about that for next iteration
		BigDecimal changeAmount = totalMoneyPaid.subtract(totalCost);
		if (changeAmount.compareTo(new BigDecimal(0)) == 1) {
			// Handle giving out change here
			touchScreen.informChangeDispensed();
		}

		// Prompt touch screen to ask user if they would like a receipt
		touchScreen.askToPrintReceipt();

		// method call to handler that deals with waiting for all items in
		// bagging area to be picked up before reseting system to be ready for a new
		// user
		handlePostPaymentCleanup();

		// Maybe Re-enable devices here?
		enableDevices();

		inCheckout.set(false);
		touchScreen.resetToWelcomeScreen();
		return;
	}

	private void handlePostPaymentCleanup() throws InterruptedException, OverloadException {

		inCleanup.set(true); // Notify Scale that we are waiting for all items on the scale to be removed
								// (weight == 0)
		resetCheckoutTotals();
		touchScreen.takeItemsNotification();
		if (scale.getCurrentWeight() > 0) {
			weightValid.set(false);
			while (!weightValid.get()) {
//				TimeUnit.SECONDS.sleep(1); //Check every second
			}
			// Weight on scale is now equal to 0
		}

		inCleanup.set(false);
	}

	private void handleInvalidWeight() throws InterruptedException {

//		waitingForWeightChangeEvent.compareAndSet(false, true);

		disablePaymentDevices();
		touchScreen.invalidWeightInCheckout();
		// Loop until scale observer reports a valid weight
		while (!weightValid.get()) {
//			waitingForWeightChangeEvent.compareAndSet(false, true);
//			TimeUnit.SECONDS.sleep(1); //Check every second
		}

		// Weight is now valid, unblock and remove touchscreen message
		enablePaymentDevices();
		touchScreen.validWeightInCheckout();
	}

	private void resetWeightFlags() {
		// Reset weight change flags
//		waitingForWeightChangeEvent.set(false);
		weightValid.set(false);
	}

	private void disablePaymentDevices() {
		// TODO Auto-generated method stub
		coinSlot.disable();
		banknoteSlot.disable();
	}

	private void enablePaymentDevices() {
		// TODO Auto-generated method stub
		coinSlot.enable();
		banknoteSlot.enable();
	}

	// Disable all devices associated with this observer
	private void disableDevices() {
		scale.disable();
		scanner.disable();
		disablePaymentDevices();
	}

	// Enable all devices associated with this observer
	private void enableDevices() {
		scale.enable();
		scanner.enable();
		enablePaymentDevices();
	}

	public boolean isWeightValid() {
		return weightValid.get();
	}

	public void setWeightValid(boolean validity) {
		weightValid.set(validity);
	}

	public static int compareTotals() {
		return totalMoneyPaid.compareTo(totalCost);
	}

	public static void addToTotalCost(BigDecimal scannedItemPrice) {
		totalCost = totalCost.add(scannedItemPrice);

	}

	public static void addToTotalPaid(BigDecimal amount) {
		totalMoneyPaid = totalMoneyPaid.add(amount);

	}

	public static void setTotalCost(BigDecimal cost) {
		totalCost = cost;

	}

	public static void setTotalPaid(BigDecimal paid) {
		totalMoneyPaid = paid;

	}

	public void setInCheckout(boolean bool) {
		inCheckout.set(bool);

	}

	public boolean isInCheckout() {
		return inCheckout.get();
	}

	public BigDecimal getTotalMoneyPaid() {
		return totalMoneyPaid;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public boolean isInCleanup() {
		return inCleanup.get();
	}

	public void setInCleanup(boolean bool) {
		inCleanup.set(bool);
	}

	public static void resetCheckoutTotals() {
		totalMoneyPaid = BigDecimal.ZERO;
		totalCost = BigDecimal.ZERO;
	}

	public double getExpectedWeight() {
		// TODO Auto-generated method stub
		return expectedWeight;
	}

	public void setExpectedWeight(double weight) {
		expectedWeight = weight;
	}

	public boolean isUsingOwnBags() {
		return usingOwnBags.get();
	}
}
