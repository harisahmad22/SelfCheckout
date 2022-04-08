package org.driver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.controlSoftware.customer.CheckoutHandler;
import org.controlSoftware.deviceHandlers.ReceiptHandler;
import org.controlSoftware.deviceHandlers.BaggingAreaScaleHandler;
import org.controlSoftware.deviceHandlers.ScannerHandler;
import org.controlSoftware.deviceHandlers.membership.MembershipCardScannerHandler;
import org.controlSoftware.deviceHandlers.payment.CashPaymentHandler;
import org.controlSoftware.general.TouchScreenSoftware;
import org.driver.SelfCheckoutData.StationState;
import org.lsmr.selfcheckout.devices.OverloadException;
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
	
	private AtomicBoolean weightIssueHandlerRunning = new AtomicBoolean(false);
	
	ScheduledExecutorService blockedStateChecker = Executors.newScheduledThreadPool(1); 
	
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
		
		this.receiptHandler = new ReceiptHandler(this.stationUnit, this.stationHardware.printer);
		
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
	
	public boolean getWeightIssueHandlerRunning() {
		return weightIssueHandlerRunning.get();
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
		stationData.changeState(StationState.INACTIVE);
		return;
    	}

	// Login and logout
	public void LogInStation(String AttendantID, String password)
    	{
		//ONLY shutdown station if it is WELCOME state
		//Otherwise ignore, to prevent attendant being able to shut down station
		//while a customer is using it

		if (stationData.getCurrentState() != StationState.INACTIVE)
		{
		    System.out.println("Error! Only Log in system while it's in use!");
		    return;
		}

			if (preStartupChecks())
			{
				//Pre startup checks succeeded, now transition to WELCOME state

				//Inform Attendant of log in
				stationUnit.informAttendantLogin(AttendantID, password);

				//Switch to WELCOME state, which will inform GUI to display the welcome screen
				//and wait for user interaction
				stationData.changeState(StationState.WELCOME);
				return;
			}

	}


	public void LogOutStation()
    	{
		//ONLY shutdown station if it is WELCOME state

		if (stationData.getCurrentState() != StationState.WELCOME)
		{
		    System.out.println("Error! Cannot shutdown system while it's in use!");
		    return;
		}

		//Inform Attendant of log out
		stationUnit.informAttendantLogout();

		//Switch to INACTIVE state, which will inform GUI to close all active windows
		//Will wipe session data
		stationData.changeState(StationState.INACTIVE);
		return;
	}


//	public void performAttendantWeightOverride() {
//		//TODO Set the Weight Override flag in SelfCheckoutData to true, will cause all loop tests in weight handlers to eval to true
//		
//	}

	public void blockStation() {
		if (stationData.getCurrentState() == StationState.NORMAL
		 || stationData.getCurrentState() == StationState.WELCOME)
		{
			//In normal/welcome state, need to change to blocked state and immediately perform 
			//an attendant block check
			this.stationData.changeState(StationState.BLOCKED);
			attendantBlockCheck("Normal State");
			return;
		}
		else if (stationData.getCurrentState() == StationState.INACTIVE)
		{
			System.out.println("Error cannot block Inactive station!");
			this.stationUnit.sendAttendantMessage("Error cannot block Inactive station!");
		}
		else if (stationData.getCurrentState() != StationState.PAY_CASH
			  || stationData.getCurrentState() != StationState.PAY_CREDIT
			  || stationData.getCurrentState() != StationState.PAY_DEBIT)
		{ 
			//Station is not mid payment, inactive, or in welcome/normal state
			//Station must be in some handling state (Checkout, processing scan, lookup product)
			//When in these states there are checks for the attendant block before and after
			//methods that wait for User input, and methods that block when an issue is detected
			this.stationData.setATTENDANT_BLOCK(true); 
		}		
		else 
		{ 
			System.out.println("Error! Cannot block state during payment process.");
			this.stationUnit.sendAttendantMessage("Error! Cannot block state during payment process.");
		}
			
	}
	
	public void unBlockStation() {
		if (stationData.getCurrentState() == StationState.BLOCKED)
		{
			this.stationData.changeState(stationData.getPreBlockedState());
		}
		else 
		{
			System.out.println("Error! Cannot unblock a non-blocked station.");
			this.stationUnit.sendAttendantMessage("Error! Cannot unblock a non-blocked station.");
		}
	}
	
	public void attendantBlockCheck() {
		if (stationData.getATTENDANT_BLOCK()) { 
			try { handleAttendantBlock("Unknown"); } 
			catch (InterruptedException e) {} }
	}
	
	public void attendantBlockCheck(String tag) {
		if (stationData.getATTENDANT_BLOCK()) { 
			try { handleAttendantBlock(tag); } 
			catch (InterruptedException e) {} }
	}
	
	public void handleAttendantBlock(String tag) throws InterruptedException {
		System.out.println("Method called from: " + tag);
		this.stationData.changeState(StationState.BLOCKED);
//		System.out.println("(TESTING) SIMULATING GUI BLOCK");
////		while(stationData.getATTENDANT_BLOCK())
////		{
//			TimeUnit.MILLISECONDS.sleep(1000);
////		}
//			System.out.println("(TESTING) SIMULATING GUI UNBLOCK");
//		System.out.println("Unblocked!");
	}

	public void handleInvalidWeightNormalMode() {
		weightIssueHandlerRunning.set(true);
		stationData.disableAllDevices();
		getTouchScreenSoftware().invalidWeightInNormalMode();
		// Loop until scale observer reports a valid weight
		while (!stationData.getWeightValidNormalMode()) {
//			TimeUnit.SECONDS.sleep(1); //Check every second
		}

		//Attendant Block check
		attendantBlockCheck("SCSoftware");
		
		// Weight is now valid, unblock and remove touchscreen message
		stationData.enableAllDevices();
		getTouchScreenSoftware().validWeightInNormalMode();
		weightIssueHandlerRunning.set(false);
	}
	
	//Hannah Ku
	public void performAttendantWeightOverride() {
		if (stationData.getCurrentState() == StationState.WEIGHT_ISSUE)
		{
//			stationData.setIsWeightOverride(true);
			try { stationData.setExpectedWeight(stationHardware.baggingArea.getCurrentWeight()); } 
			catch (OverloadException e) { e.printStackTrace(); }
			stationData.changeState(stationData.getPreBlockedState());
			return;
		}
		else 
		{
			System.out.println("Error! Cannot override station that is not in the WEIGHT_ISSUE state!");
		}
		
		
	}
	
	//Hannah Ku
	public void removeProduct(String description) {
		if (stationData.getCurrentState() == StationState.NORMAL)
		{
			stationData.removeProductFromCheckoutHashMap(description);
		}
		else
		{
			System.out.println("Error! Cannot remove item when station is not in NORMAL state!");
		}
		
	}

	
	

}
