//Shufan Zhai - 30117333

package org.controlSoftware;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

public class ItemInBaggingArea implements ElectronicScaleObserver {
	private boolean isOverloaded = false;
	private double weightVariablity = 25; // The maximum weight in grams that the ProcessedScannedItem observer weight
											// can differ from the scale weight
	private ElectronicScale scale;
	private ProcessScannedItem scannerObserver;
	private TouchScreen display;
	private Checkout checkout;
	private double weightAtLastEvent = 0;
	

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

	public ItemInBaggingArea(ElectronicScale scale, ProcessScannedItem scanner, TouchScreen display, Checkout checkout) {
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
				if (Math.abs(scannerExpectedWeight - weightOnScale) <= weightVariablity) {
					scannerObserver.setWeightValid(true);
					scannerObserver.setWaitingForWeightChange(false);
				} else {
					scannerObserver.setWeightValid(false);
					scannerObserver.setWaitingForWeightChange(false);
				}
			}
			if (checkout.isInCheckout()) 
			{ // Weight is not supposed to change during checkout, unless during cleanup
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
					if (Math.abs(checkoutExpectedWeight - weightOnScale) > weightVariablity) // Weight cannot change more than defined tolerance 
					{
						checkout.setWeightValid(false);
					}
					else { checkout.setWeightValid(true); }
				}
			}
		}
		weightAtLastEvent = weightInGrams; // Done handling weight change, store scale weight for next event
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