package org.driver;

import java.util.concurrent.TimeUnit;

import org.controlSoftware.customer.CheckoutHandler;
import org.controlSoftware.deviceHandlers.ReceiptHandler;
import org.controlSoftware.deviceHandlers.BaggingAreaScaleHandler;
import org.controlSoftware.deviceHandlers.ScannerHandler;
import org.controlSoftware.deviceHandlers.membership.MembershipCardScannerHandler;
import org.controlSoftware.deviceHandlers.payment.CashPaymentHandler;
import org.controlSoftware.general.TouchScreenSoftware;
import org.driver.SelfCheckoutData.StationState;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.BarcodeScannerObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

public class SelfCheckoutSoftware {

	private SelfCheckoutStationUnit stationUnit;
	private SelfCheckoutStation stationHardware;
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
	
	public SelfCheckoutSoftware(SelfCheckoutStationUnit stationUnit, SelfCheckoutData stationData)
	{
		this.stationUnit = stationUnit;
		
		this.stationHardware = stationUnit.getSelfCheckoutStationHardware();
		
		this.stationData = stationData;
		
		this.touchScreenSoftware = new TouchScreenSoftware(System.in, stationUnit.getTouchScreen(), stationData);
		
		this.receiptHandler = new ReceiptHandler(this.stationData, this.stationHardware.printer);
		
		this.checkoutHandler = new CheckoutHandler(this.stationData, this);
		
		this.scannerHandler = new ScannerHandler(this.stationData, this);
		
		this.baggingAreaScaleHandler = new BaggingAreaScaleHandler(this.stationData, this);
		
		this.membershipCardScannerHandler = new MembershipCardScannerHandler(this.stationData);
		
		//CashPaymentHandler will deal with attaching to hardware
		this.cashPaymentHandler = new CashPaymentHandler(this.stationData);
		
		this.stationHardware.mainScanner.attach((BarcodeScannerObserver) scannerHandler);
		this.stationHardware.handheldScanner.attach((BarcodeScannerObserver) scannerHandler);
		
		this.stationHardware.baggingArea.attach((ElectronicScaleObserver) baggingAreaScaleHandler);
				
		this.stationHardware.cardReader.attach(membershipCardScannerHandler);
		
	}

	public ReceiptHandler getReceiptHandler() {
		return receiptHandler;
	}
	
	public TouchScreenSoftware getTouchScreenSoftware() {
		return touchScreenSoftware;
	}
	
	public void updateTouchScreenSoftware(TouchScreenSoftware tss)
	{//Used for when we have to change the touchScreen's input stream during testing
		this.touchScreenSoftware = tss;		
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
	
	public void startupStation()
	{
		//ONLY start up station if it is in the INACTIVE state
		//Otherwise ignore
		if (stationData.getCurrentState() != StationState.INACTIVE)
		{
			System.out.println("Error! Cannot startup a system if it's already running!");
			return;
		}
		
		//Perform pre-startup checks
		if (preStartupChecks())
		{
			//Pre startup checks succeeded, now transition to WELCOME state
			
			//Inform Attendant of startup
			stationUnit.informAttendantOfStartup();
			
			//Switch to WELCOME state, which will inform GUI to display the welcome screen
			//and wait for user interaction
			stationData.changeState(StationState.WELCOME);
			return;
		}
		
	}
	

	private boolean preStartupChecks() {
		
		return true;
	}

	public void shutdownStation()
	{
		//ONLY shutdown station if it is WELCOME state
		//Otherwise ignore, to prevent attendant being able to shut down station
		//while a customer is using it
		
		if (stationData.getCurrentState() != StationState.WELCOME)
		{
			System.out.println("Error! Cannot shutdown system while it's in use!");
			return;
		}
		
		//Inform Attendant of shutdown
		stationUnit.informAttendantOfShutdown();
		
		//Switch to INACTIVE state, which will inform GUI to close all active windows
		//Will wipe session data
		stationData.setCurrentState(StationState.INACTIVE);
		return;		
		//Not sure if this is a good idea:
//		this.stationUnit = null;
//		
//		this.stationHardware = null;
//		
//		this.stationData = null;
//		
//		this.touchScreenSoftware = null;
//		
//		this.receiptHandler = null;
//		
//		this.checkoutHandler = null;
//		
//		this.scannerHandler = null;
//		
//		this.baggingAreaScaleHandler = null;
//		
//		this.membershipCardScannerHandler = null;
//		
//		//CashPaymentHandler will deal with attaching to hardware
//		this.cashPaymentHandler = null;
		//Not sure if this is a good idea ^
		
	}

	public void performAttendantWeightOverride() {
		//TODO Set the Weight Override flag in SelfCheckoutData to true, will cause all loop tests in weight handlers to eval to true
		
	}

	public void blockStation() {
		this.stationData.changeState(StationState.BLOCKED);
	}
	public void unBlockStation() {
		this.stationData.changeState(stationData.getPreBlockedState());
	}
	
	public void attendantBlockCheck() {
		if (stationData.getATTENDANT_BLOCK()) { 
			try { handleAttendantBlock("Scanner Handler"); } 
			catch (InterruptedException e) {} }
	}
	
	public void handleAttendantBlock(String tag) throws InterruptedException {
		System.out.println("Method called from: " + tag);
		while(stationData.getATTENDANT_BLOCK())
		{
			TimeUnit.MILLISECONDS.sleep(500);
		}
		System.out.println("Unblocked! Returning to caller: " + tag);
	}
}
