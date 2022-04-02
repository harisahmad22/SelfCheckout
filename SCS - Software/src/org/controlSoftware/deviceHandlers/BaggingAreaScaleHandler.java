//Shufan Zhai - 30117333

package org.controlSoftware.deviceHandlers;

import org.controlSoftware.customer.CheckoutHandler;
import org.controlSoftware.general.TouchScreenSoftware;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutSoftware;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

public class BaggingAreaScaleHandler implements ElectronicScaleObserver {
//	private static final double BAG_WEIGHT = 5;
//	private boolean isOverloaded = false;
//	private ElectronicScale scale;
//	private ScannerHandler scannerObserver;
//	private TouchScreenSoftware display;
//	private CheckoutHandler checkout;
//	private double weightAtLastEvent = 0;
//	private double personalBagsWeight = 0;
//	private double weightVariablity = 25;

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


	private SelfCheckoutData stationData;
	private SelfCheckoutSoftware stationSoftware;
//	private String type;
	
	public BaggingAreaScaleHandler(SelfCheckoutData stationData, SelfCheckoutSoftware stationSoftware)
//			ElectronicScale scale, 
//			ScannerHandler scanner, 
//			TouchScreenSoftware display,
//			CheckoutHandler checkout) 
	{
//		this.scale = scale;
//		this.scannerObserver = scanner;
//		this.display = display;
//		this.checkout = checkout;
		this.stationData = stationData;
		this.stationSoftware = stationSoftware;
//		this.type = type; //Will be used to identify between bagging area scale and scanning area scale

	}

	public boolean isOverloaded() {
		return stationData.getIsBaggingAreaOverloaded();
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {

	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {

	}

	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		double scannerExpectedWeight = stationData.getExpectedWeightScanner(); // set to weight in observer
		double checkoutExpectedWeight = stationData.getExpectedWeightCheckout();
		double weightOnScale = weightInGrams;

		if (!isOverloaded()) {
			if (stationData.getIsScannerWaitingForWeightChange()) {
				handleScannerWeightEvent(weightOnScale, scannerExpectedWeight);
			}
			if (stationData.isInCheckout()) {
				handleCheckoutWeightEvent(weightOnScale, checkoutExpectedWeight);
			}
		}
//		weightAtLastEvent = weightInGrams; // Done handling weight change, store scale weight for next event
		System.out.println("Scale Weight: " + weightInGrams);
	}
	
	private void handleScannerWeightEvent(double weightOnScale, double scannerExpectedWeight) {
		if (Math.abs(scannerExpectedWeight - weightOnScale) <= stationData.getBaggingAreaWeightVariablity()) {
			stationData.setWeightValidScanner(true);
			stationData.setIsScannerWaitingForWeightChange(false);
		} else {
			stationData.setWeightValidScanner(false);
			stationData.setIsScannerWaitingForWeightChange(false);
		}
		
	}
	
	private void handleCheckoutWeightEvent(double weightOnScale, double checkoutExpectedWeight) {
	// Weight is not supposed to change during checkout, unless during cleanup
	  // where all items on the scale need to be removed
		if (stationData.isInCleanup())
		{ 
			if (weightOnScale != 0) 
			{
				stationData.setWeightValidCheckout(false);
			}
			else { stationData.setWeightValidCheckout(true); }
		}
		else
		{	
			if (Math.abs(checkoutExpectedWeight - weightOnScale) <= stationData.getBaggingAreaWeightVariablity()) // Weight cannot change more than defined tolerance 
			{
				stationData.setWeightValidCheckout(true);
			}
			else { stationData.setWeightValidCheckout(false); }
		}
		
	}
	
	@Override
	public void overload(ElectronicScale scale) {
		stationData.setIsBaggingAreaOverloaded(true);
		stationSoftware.getTouchScreenSoftware().scaleOverloaded();
	}

	@Override
	public void outOfOverload(ElectronicScale scale) {
		stationData.setIsBaggingAreaOverloaded(false);
		stationSoftware.getTouchScreenSoftware().overloadFixed();
	}
}
