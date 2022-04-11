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
import org.controlSoftware.deviceHandlers.ScanningAreaScaleHandler;
import org.controlSoftware.deviceHandlers.membership.ScansMembershipCard;
import org.controlSoftware.deviceHandlers.payment.CashPaymentHandler;
import org.controlSoftware.deviceHandlers.payment.GiftCardScannerHandler;
import org.controlSoftware.deviceHandlers.payment.PayWithCard;
import org.controlSoftware.deviceHandlers.payment.CardPaymentSoftware;
import org.driver.SelfCheckoutData.StationState;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.InvalidArgumentSimulationException;
import org.lsmr.selfcheckout.NullPointerSimulationException;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.BarcodeScannerObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class SelfCheckoutSoftware {

	private SelfCheckoutStationUnit stationUnit;
	private SelfCheckoutStation stationHardware;
	private SelfCheckoutData stationData;
	
	public CheckoutHandler checkoutHandler;
	public CardPaymentSoftware cardPaymentSoftware;
	public CashPaymentHandler cashPaymentHandler;
	public GiftCardScannerHandler giftCardHandler;
	public ScannerHandler scannerHandler;
	public ScanningAreaScaleHandler scanningAreaScaleHandler;
	public BaggingAreaScaleHandler baggingAreaScaleHandler;
	public ReceiptHandler receiptHandler;
	
	private ScansMembershipCard membershipCardHandler;
	 
	
	
	private AtomicBoolean weightIssueHandlerRunning = new AtomicBoolean(false);
	
	ScheduledExecutorService blockedStateChecker = Executors.newScheduledThreadPool(1);
	private PayWithCard cardPaymentHandler;
	
	/***
	 * This Class will deal with initializing all the handlers in the system and attaching them
	 * to the relevant hardware devices.
	 * 
	 * Methods will also be provided to access individual handlers. (May not be needed) 
	 */
	
	public SelfCheckoutSoftware(SelfCheckoutStationUnit stationUnit, SelfCheckoutData newStationData)
	{
		this.stationUnit = stationUnit;
		
		this.stationHardware = stationUnit.getSelfCheckoutStationHardware();
		
		this.stationData = newStationData;
		
		
		this.receiptHandler = new ReceiptHandler(this.stationUnit, this.stationHardware.printer);
		
		this.checkoutHandler = new CheckoutHandler(this.stationData, this);
		
		this.scannerHandler = new ScannerHandler(this.stationData, this);
		
		this.baggingAreaScaleHandler = new BaggingAreaScaleHandler(this.stationData, this);
		
		this.scanningAreaScaleHandler = new ScanningAreaScaleHandler(this.stationData, this);
		
		this.cardPaymentHandler = new PayWithCard(this.stationData, this);
		
		this.membershipCardHandler = new ScansMembershipCard(this.stationData, this);
		
		this.giftCardHandler = new GiftCardScannerHandler(this.stationData);
		
		//CashPaymentHandler will deal with attaching to hardware
		this.cashPaymentHandler = new CashPaymentHandler(this.stationData);
		
		attachObservers();
		
		this.cardPaymentHandler.addCardIssuer("Credit", this.stationData.getCreditCardIssuer());
		this.cardPaymentHandler.addCardIssuer("Debit", stationData.getDebitCardIssuer());
		
	}
	
	public void attachObservers()
	{
		this.stationHardware.mainScanner.attach((BarcodeScannerObserver) scannerHandler);
		this.stationHardware.handheldScanner.attach((BarcodeScannerObserver) scannerHandler);
		
		this.stationHardware.baggingArea.attach((ElectronicScaleObserver) baggingAreaScaleHandler);
		this.stationHardware.scanningArea.attach((ElectronicScaleObserver) scanningAreaScaleHandler);
				
		this.stationHardware.cardReader.attach(cardPaymentHandler);
		this.stationHardware.cardReader.attach(membershipCardHandler);
		this.stationHardware.cardReader.attach(giftCardHandler);
	}
	public void detachObservers()
	{
		this.stationHardware.mainScanner.detach((BarcodeScannerObserver) scannerHandler);
		this.stationHardware.handheldScanner.detach((BarcodeScannerObserver) scannerHandler);
		
		this.stationHardware.baggingArea.detach((ElectronicScaleObserver) baggingAreaScaleHandler);
		this.stationHardware.scanningArea.detach((ElectronicScaleObserver) scanningAreaScaleHandler);
				
		this.stationHardware.cardReader.detach(cardPaymentHandler);
		this.stationHardware.cardReader.detach(membershipCardHandler);
		this.stationHardware.cardReader.detach(giftCardHandler);
	}

	public ReceiptHandler getReceiptHandler() {
		return receiptHandler;
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
			attachObservers();
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
		detachObservers();
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
//			attendantBlockCheck("Normal State");
			return;
		}
		else if (stationData.getCurrentState() == StationState.INACTIVE)
		{
			System.out.println("Error cannot block Inactive station!");
			this.stationUnit.sendAttendantMessage("Error cannot block Inactive station!");
		}
		else if (stationData.getCurrentState() != StationState.PAY_CASH
			  || stationData.getCurrentState() != StationState.PAY_CREDIT
			  || stationData.getCurrentState() != StationState.PAY_DEBIT
			  || stationData.getCurrentState() != StationState.PAY_GIFTCARD)
		{ 
			//Station is not mid payment, inactive, or in welcome/normal state
			//Station must be in some handling state (Checkout, processing scan, lookup product)
			//When in these states there are checks for the attendant block before and after
			//methods that wait for User input, and methods that block when an issue is detected
			
			this.stationData.changeState(StationState.BLOCKED);
//			this.stationData.setATTENDANT_BLOCK(true); 
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

	public CardPaymentSoftware getCardPaymentSoftware() {
		return cardPaymentSoftware;
	}

	
	public void manualMembershipCheck(String membershipID)
	{
		if(membershipCardHandler.getMembershipCards().containsKey(membershipID) == true) {
			stationData.setMembershipID(membershipID);
			stationData.changeState(StationState.PAYMENT_AMOUNT_PROMPT);
			
		}
		else {
			//membership number does not exist
			stationData.changeState(StationState.BAD_MEMBERSHIP);
		}
	}
	public BarcodedProduct getBarcodedItem(Barcode barcode) {
		try {
			BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
			return product;
		}
		catch (NullPointerException e) {
			return null;
		}
	}
	
	public PLUCodedProduct getPLUCodedItem(PriceLookupCode pluCode) {
		try {
			System.out.println("CHECK");
			PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(pluCode);
			return product;
		}
		catch (Exception e) {
			return null;
		}
	}
	

}
