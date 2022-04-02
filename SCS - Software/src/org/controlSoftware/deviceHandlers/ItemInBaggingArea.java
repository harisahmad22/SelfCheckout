//Shufan Zhai - 30117333

package org.controlSoftware.deviceHandlers;

import org.controlSoftware.customer.CheckoutSoftware;
import org.controlSoftware.general.TouchScreenSoftware;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

public class ItemInBaggingArea implements ElectronicScaleObserver {
	private static final double BAG_WEIGHT = 5;
	private boolean isOverloaded = false;
	private ElectronicScale scale;
	private ProcessScannedItem scannerObserver;
	private TouchScreenSoftware display;
	private CheckoutSoftware checkout;
	private double weightAtLastEvent = 0;
	private double personalBagsWeight = 0;
	private double weightVariablity = 25;

	/*
	 * (Shufan) DONT HAVE TO WORRY ABOUT: - the graphical user interface - products
	 * without barcodes - credit/debit cards - returning change to the customer
	 * 
	 * 1) weightChanged is called by ProcessScannedItem observer
	 * 
	 * 2) Check if observers are calling weightChanged() - if not then disable
	 * scanning and touchscreen input until signal received or employee override
	 * 
	 * 3) Check if expected weight from the observer is similar to weight obtained
	 * from scale - within a specific weightVariability - set weightValid to true if
	 * conditionals met - otherwise set weightvalid to false
	 * 
	 * 4) Done
	 */

	public ItemInBaggingArea(ElectronicScale scale, ProcessScannedItem scanner, TouchScreenSoftware display,
			CheckoutSoftware checkout) {
		this.scale = scale;
		this.scannerObserver = scanner;
		this.display = display;
		this.checkout = checkout;

	}

	public boolean isOverloaded() {
		return isOverloaded;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {

	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {

	}

	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		double scannerExpectedWeight = scannerObserver.getTargetWeight(); // set to weight in observer
		double checkoutExpectedWeight = checkout.getExpectedWeight();
		double weightOnScale = weightInGrams;

		if (!isOverloaded) {
			if (scannerObserver.isWaitingForWeightChange()) {
				handleScannerWeightEvent(weightOnScale, scannerExpectedWeight);
			}
			if (checkout.isInCheckout()){ 
				handleCheckoutWeightEvent(weightOnScale, checkoutExpectedWeight);
			}
		}
		weightAtLastEvent = weightInGrams; // Done handling weight change, store scale weight for next event
		System.out.println("Scale Weight: " + weightInGrams);
	}
	
	private void handleScannerWeightEvent(double weightOnScale, double scannerExpectedWeight) {
		if (Math.abs(scannerExpectedWeight - weightOnScale) <= weightVariablity) {
			scannerObserver.setWeightValid(true);
			scannerObserver.setWaitingForWeightChange(false);
		} else {
			scannerObserver.setWeightValid(false);
			scannerObserver.setWaitingForWeightChange(false);
		}
		
	}
	
	private void handleCheckoutWeightEvent(double weightOnScale, double checkoutExpectedWeight) {
	// Weight is not supposed to change during checkout, unless during cleanup
	  // where all items on the scale need to be removed
		if (checkout.isInCleanup())
		{ 
			if (weightOnScale != 0) 
			{
				checkout.setWeightValid(false);
			}
			else { checkout.setWeightValid(true); }
		}
		else
		{	
			if (Math.abs(checkoutExpectedWeight - weightOnScale) <= weightVariablity) // Weight cannot change more than defined tolerance 
			{
				checkout.setWeightValid(true);
			}
			else { checkout.setWeightValid(false); }
		}
		
	}
	
	@Override
	public void overload(ElectronicScale scale) {
		this.isOverloaded = true;
		display.scaleOverloaded();
	}

	@Override
	public void outOfOverload(ElectronicScale scale) {
		this.isOverloaded = false;
		display.overloadFixed();
	}
}
