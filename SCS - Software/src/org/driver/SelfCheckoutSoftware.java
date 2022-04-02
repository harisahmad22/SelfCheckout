package org.driver;

import org.controlSoftware.customer.CheckoutHandler;
import org.controlSoftware.deviceHandlers.ReceiptHandler;
import org.controlSoftware.deviceHandlers.BaggingAreaScaleHandler;
import org.controlSoftware.deviceHandlers.ScannerHandler;
import org.controlSoftware.general.TouchScreenSoftware;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class SelfCheckoutSoftware {

	private SelfCheckoutStation station;
	private SelfCheckoutData stationData;
	
	private CheckoutHandler checkoutHandler;
	private ScannerHandler scannerHandler;
	private BaggingAreaScaleHandler baggingAreaScaleHandler;
	private ReceiptHandler receiptHandler;
	
	private TouchScreenSoftware touchScreenSoftware;
	
	public SelfCheckoutSoftware(SelfCheckoutStation station, SelfCheckoutData stationData)
	{
		this.station = station;
		
		this.stationData = stationData;
		
		this.receiptHandler = new ReceiptHandler(this.station.printer);
		
		this.checkoutHandler = new CheckoutHandler(this.stationData, this);
		
		this.scannerHandler = new ScannerHandler(this.stationData, this);
		
		this.baggingAreaScaleHandler = new BaggingAreaScaleHandler(this.stationData, this);
		
		
		
	}

	public ReceiptHandler getReceiptHandler() {
		return receiptHandler;
	}
	
	public TouchScreenSoftware getTouchScreenSoftware() {
		return touchScreenSoftware;
	}
	
	public void updateTouchScreen(TouchScreenSoftware ts)
	{//Used for when we have to change the touchScreen's input stream during testing
		this.touchScreenSoftware = ts;		
	}

	public CheckoutHandler getCheckoutHandler() {
		return this.checkoutHandler;
	}
	
	public ScannerHandler getScannerHandler() {
		return this.scannerHandler;
	}
	
	public BaggingAreaScaleHandler getScaleHandler() {
		return this.baggingAreaScaleHandler;
	}
}
