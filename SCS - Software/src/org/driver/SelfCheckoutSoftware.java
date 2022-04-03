package org.driver;

import org.controlSoftware.customer.CheckoutHandler;
import org.controlSoftware.deviceHandlers.ReceiptHandler;
import org.controlSoftware.deviceHandlers.BaggingAreaScaleHandler;
import org.controlSoftware.deviceHandlers.ScannerHandler;
import org.controlSoftware.deviceHandlers.membership.MembershipCardScannerHandler;
import org.controlSoftware.deviceHandlers.payment.CashPaymentHandler;
import org.controlSoftware.general.TouchScreenSoftware;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.BarcodeScannerObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

public class SelfCheckoutSoftware {

	private SelfCheckoutStation station;
	private SelfCheckoutData stationData;
	
	private CheckoutHandler checkoutHandler;
	private ScannerHandler scannerHandler;
	private BaggingAreaScaleHandler baggingAreaScaleHandler;
	private ReceiptHandler receiptHandler;
	
	private TouchScreenSoftware touchScreenSoftware;
	private CashPaymentHandler cashPaymentHandler;
	private CardReaderObserver membershipCardScannerHandler;
	
	/***
	 * This Class will deal with initializing all the handlers in the system and attaching them
	 * to the relevant hardware devices.
	 * 
	 * Methods will also be provided to access individual handlers. (May not be needed) 
	 */
	
	public SelfCheckoutSoftware(SelfCheckoutStation station, SelfCheckoutData stationData)
	{
		this.station = station;
		
		this.stationData = stationData;
		
		this.receiptHandler = new ReceiptHandler(this.station.printer);
		
		this.checkoutHandler = new CheckoutHandler(this.stationData, this);
		
		this.scannerHandler = new ScannerHandler(this.stationData, this);
		
		this.baggingAreaScaleHandler = new BaggingAreaScaleHandler(this.stationData, this);
		
		this.membershipCardScannerHandler = new MembershipCardScannerHandler(this.stationData);
		
		//CashPaymentHandler will deal with attaching to hardware
		this.cashPaymentHandler = new CashPaymentHandler(this.stationData);
		
		this.station.mainScanner.attach((BarcodeScannerObserver) scannerHandler);
		this.station.handheldScanner.attach((BarcodeScannerObserver) scannerHandler);
		
		this.station.baggingArea.attach((ElectronicScaleObserver) baggingAreaScaleHandler);
				
		this.station.cardReader.attach(membershipCardScannerHandler);
		
	}

	public ReceiptHandler getReceiptHandler() {
		return receiptHandler;
	}
	
	public TouchScreenSoftware getTouchScreenSoftware() {
		return touchScreenSoftware;
	}
	
	public void updateTouchScreenSoftware(TouchScreenSoftware ts)
	{//Used for when we have to change the touchScreen's input stream during testing
		this.touchScreenSoftware = ts;		
	}

	public CheckoutHandler getCheckoutHandler() {
		return this.checkoutHandler;
	}
	
	public ScannerHandler getScannerHandler() {
		return this.scannerHandler;
	}
	
	public BaggingAreaScaleHandler getBaggingAreaScaleHandler() {
		return this.baggingAreaScaleHandler;
	}
}
