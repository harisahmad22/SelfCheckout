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

public class BaggingAreaScaleHandler implements ElectronicScaleObserver {

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
	
	public BaggingAreaScaleHandler(SelfCheckoutData stationData, SelfCheckoutSoftware stationSoftware)
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
//		double scannerExpectedWeight = stationData.getExpectedWeightScanner(); // set to weight in observer
//		double checkoutExpectedWeight = stationData.getExpectedWeightCheckout();
//		double normalModeExpectedWeight = stationData.getExpectedWeightNormalMode();
		double thisExpectedWeight = stationData.getExpectedWeight();
		double weightOnScale = weightInGrams;

		if (!isOverloaded()) {
			if (stationData.getCurrentState() == StationState.NORMAL) {
				handleNormalModeWeightEvent(weightOnScale, thisExpectedWeight);
			}
			else if (stationData.getCurrentState() == StationState.WAITING_FOR_ITEM) {
				handleWaitingForWeightEvent(weightOnScale, thisExpectedWeight);
			}
			else if (stationData.getCurrentState() == StationState.WEIGHT_ISSUE) {
				handleWeightIssueEvent(weightOnScale, thisExpectedWeight);
			}
			else if (stationData.getCurrentState() == StationState.ADDING_BAGS)
			{
				handleAddingBagsEvent(weightOnScale, thisExpectedWeight);
			}
//			else if (stationData.getIsScannerWaitingForWeightChange()) {
//				handleScannerWeightEvent(weightOnScale, scannerExpectedWeight);
//			}
//			else if (stationData.isInCheckout()) {
			else if (stationData.getCurrentState() == StationState.CHECKOUT 
//				|| stationData.getCurrentState() == StationState.PAY_CASH
				|| stationData.getCurrentState() == StationState.PRINT_RECEIPT_PROMPT) {
				//For now If user is paying, ignore weight events
					handleCheckoutWeightEvent(weightOnScale, thisExpectedWeight);
			}
		}
//		weightAtLastEvent = weightInGrams; // Done handling weight change, store scale weight for next event
		System.out.println("Scale Weight: " + weightInGrams);
	}
	
	private void handleAddingBagsEvent(double weightOnScale, double thisExpectedWeight) {
		
		if (weightOnScale > thisExpectedWeight) 
		{ 
			stationData.changeState(StationState.ASK_MEMBERSHIP);
		}
		else { stationData.changeState(StationState.WEIGHT_ISSUE); return; }
		
		
		return;
		
	}

	private void handleNormalModeWeightEvent(double weightOnScale, double expectedWeight) {
		if (Math.abs(expectedWeight - weightOnScale) <= stationData.getBaggingAreaWeightVariablity()) {
//			stationData.setWeightValidNormalMode(true);
		} else {
			stationData.changeState(StationState.WEIGHT_ISSUE);
//			stationData.setWeightValidNormalMode(false);
//			if(!stationSoftware.getWeightIssueHandlerRunning()) //Only call software handler if not already running
//			{
//				stationSoftware.handleInvalidWeightNormalMode();		
//			}
		}
	}
	
	private void handleWaitingForWeightEvent(double weightOnScale, double expectedWeight) {
		if (Math.abs(expectedWeight - weightOnScale) <= stationData.getBaggingAreaWeightVariablity()) {
			//Weight is OK
//			stationData.setWeightValidScanner(true);
//			stationData.setIsScannerWaitingForWeightChange(false);
			//Return to NORMAL state
			if (stationData.getMidPaymentFlag())
			{
				stationData.changeState(StationState.PAY_CASH);
			}
			else
			{
				stationData.changeState(StationState.NORMAL);
			}
		} else {
			//Weight is Bad, go to Weight_Issue state
			stationData.changeState(StationState.WEIGHT_ISSUE);
//			stationData.setWeightValidScanner(false);
//			stationData.setIsScannerWaitingForWeightChange(false);
		}
	}
	
	private void handleWeightIssueEvent(double weightOnScale, double expectedWeight) {
		if (Math.abs(expectedWeight - weightOnScale) <= stationData.getBaggingAreaWeightVariablity()) {
			//Weight is OK
			//Return to previous state, if previous state was waiting for item, just go to Normal state
			if (stationData.getPreBlockedState() == StationState.WAITING_FOR_ITEM) 
			{ 
				if (stationData.getMidPaymentFlag()) { stationData.changeState(StationState.PAY_CASH); }
				else { stationData.changeState(StationState.NORMAL); } 
			}
			else if (stationData.getPreBlockedState() == StationState.ADDING_BAGS) { stationData.changeState(StationState.ASK_MEMBERSHIP); }
			else { stationData.changeState(stationData.getPreBlockedState()); }
		} else {
			//Weight is Bad, do nothing
		}
	}
	
	private void handleCheckoutWeightEvent(double weightOnScale, double expectedWeight) {
	// Weight is not supposed to change during checkout, unless during cleanup
	  // where all items on the scale need to be removed
		if (stationData.getCurrentState() == StationState.PRINT_RECEIPT_PROMPT)
		{ 
			if (weightOnScale > 0.1) 
			{
//				stationData.setWeightValidCheckout(false);
				//Remain in cleanup state
			}
			else 
			{
//				stationData.setWeightValidCheckout(true);
				//Weight is 0, can reset to welcome screen
				stationData.changeState(StationState.WELCOME);
			}
		}
		else
		{	
			if (Math.abs(expectedWeight - weightOnScale) <= stationData.getBaggingAreaWeightVariablity()) // Weight cannot change more than defined tolerance 
			{
//				stationData.setWeightValidCheckout(true);
				//Don't change state
			}
			else 
			{ 
//				stationData.setWeightValidCheckout(false);
				stationData.changeState(StationState.WEIGHT_ISSUE);
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
