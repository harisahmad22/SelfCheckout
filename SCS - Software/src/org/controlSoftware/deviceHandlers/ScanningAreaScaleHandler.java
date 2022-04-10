//Shufan Zhai - 30117333

package org.controlSoftware.deviceHandlers;

import org.controlSoftware.customer.CheckoutHandler;
import org.controlSoftware.general.TouchScreenSoftware;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutSoftware;
import org.driver.SelfCheckoutData.StationState;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

public class ScanningAreaScaleHandler implements ElectronicScaleObserver {

	/*
	 * (Shufan) DONT HAVE TO WORRY ABOUT: - 
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
	
	public ScanningAreaScaleHandler(SelfCheckoutData stationData, SelfCheckoutSoftware stationSoftware)
	{
		this.stationData = stationData;
		this.stationSoftware = stationSoftware;
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
		double weightOnScale = weightInGrams;

		if (!isOverloaded()) {
			if (stationData.getCurrentState() == StationState.WAITING_FOR_LOOKUP_ITEM) {
				stationData.addProductToCheckout(stationData.getLookedUpProduct(), weightOnScale);
				stationData.setLookedUpProduct(null);
			}
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
