package org.driver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import org.controlSoftware.data.NegativeNumberException;
import org.controlSoftware.general.TouchScreenSoftware;
import org.driver.databases.BarcodedProductDatabase;
import org.driver.databases.TestProducts;
import org.driver.databases.BarcodedProductDatabase;
import org.driver.databases.PLUDatabase;
import org.driver.databases.ProductInfo;
import org.driver.databases.StoreInventory;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.CoinSlot;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

/*
 *  
 *  Jonah Richards
 *
 *  Defines object to be associated with each self checkout machine that contains any data that
 *  might be needed across individual parts of the system. Running weights, totals, etc..
 *  
 *  Just attributes with getters/setters.
 *  
 *  **TO-DO
 *  Exception handling
 *  Input validation?
 *  
 */

public class SelfCheckoutData {
	
//	private TouchScreenSoftware touchScreenSoftware;
	
	//============================Hardware Devices============================ 
	private SelfCheckoutStation station;
	private BarcodeScanner handScanner;
	private BarcodeScanner mainScanner;
	private BanknoteSlot banknoteInputSlot;
	private CoinSlot coinSlot;
	private ElectronicScale baggingAreaScale;
	private ElectronicScale scanningAreaScale;
	private CardReader cardReader;
	//============================Hardware Devices============================	
	
	// Can't make attributes static, unfortunately, as multiple machines will all have their own data.
	private BigDecimal totalDue = BigDecimal.ZERO;
	private BigDecimal totalMoneyPaid = BigDecimal.ZERO;
	private BigDecimal totalPaidThisTransaction = BigDecimal.ZERO;
	
	private double expectedWeightNormalMode = 0;
	private double expectedWeightCheckout = 0;
	private double expectedWeightScanner = 0;
	
	//The amount a product's weight can differ +/- from the listed weight in the lookup
	private double baggingAreaWeightVariability = 15;
	
	private static double bagWeight = 40;
	private String membershipID = "null"; //Default to null, change when membership card is scanned in
	// No implementation yet
	private String membershipPoints = "0\n";
		
	// List of scanned products for receipt generation and GUI
	//The ProductInfo class is a wrapper for a product that allows you to get its associated weight
	//Which may come from a database or the scanning area scale
	//There are two methods to add to this map, either using a BarcodedProduct or a PLUCodedProduct
	private HashMap<String, ProductInfo> productsAddedToCheckout = new HashMap<String, ProductInfo>(); //Key is product description, could be changed

	//Product Lookup
	private ProductDatabases productDatabases;
	
	//============================Software Flags============================
	private AtomicBoolean isWeightValidNormalMode = new AtomicBoolean(true);
	private AtomicBoolean isWeightValidCheckout = new AtomicBoolean(true);
	private AtomicBoolean isWeightValidScanner = new AtomicBoolean(false);
	
	private AtomicBoolean inCheckout = new AtomicBoolean(false);
	private AtomicBoolean inCleanup = new AtomicBoolean(false);
	private AtomicBoolean isUsingOwnBags = new AtomicBoolean(false);
	private AtomicBoolean cardSwipedCheckout = new AtomicBoolean(false);

	private AtomicBoolean isFirstCheckout = new AtomicBoolean(true);
	private AtomicBoolean isBaggingAreaScaleOverloaded = new AtomicBoolean(false);
	private AtomicBoolean isScanningAreaScaleOverloaded = new AtomicBoolean(false);
	private AtomicBoolean isScannerWaitingForWeightChange = new AtomicBoolean(false);
	
	private AtomicBoolean isCheckoutWaitingForCreditCard = new AtomicBoolean(false);
	private AtomicBoolean isCheckoutWaitingForDebitCard = new AtomicBoolean(false);
	private AtomicBoolean isCheckoutWaitingForGiftCard = new AtomicBoolean(false);
	private AtomicBoolean isCheckoutWaitingForMembership = new AtomicBoolean(false);
	
	private AtomicBoolean ATTENDANT_BLOCK = new AtomicBoolean(false);
	//============================Software Flags============================
	
	private PLUDatabase PLU_Product_Database;
	private BarcodedProductDatabase Barcoded_Product_Database;
	private StoreInventory Store_Inventory;
	

	public SelfCheckoutData(SelfCheckoutStation station) {
		//This class will give the software access to 
		//the hardware devices
		this.station = station;
//		this.touchScreenSoftware = touchScreenSoftware;
		this.mainScanner = this.station.mainScanner;
		this.handScanner = this.station.handheldScanner;
		this.banknoteInputSlot = this.station.banknoteInput;
		this.coinSlot = this.station.coinSlot;
		this.baggingAreaScale = this.station.baggingArea;
		this.scanningAreaScale = this.station.scanningArea;
		this.cardReader = this.station.cardReader;
		
		//Initialize Product Databases
		
		//TODO
		PLU_Product_Database = new PLUDatabase();
		
		//Initialize some test Proudcts
		//3 Products, milk, orange juice, and corn flakes
		TestProducts testProducts = new TestProducts();
		Barcoded_Product_Database = new BarcodedProductDatabase(testProducts.getBarcodedProductList());
		
		//TODO
		Store_Inventory = new StoreInventory();
		
	}
	
	
	/* 
	 * High level states the self checkout station can be in
	 * Boolean state checks can be done in logic.
	 * 
	 *		**I have no idea how this is going to mesh with multithreading. Consultation needed.
	 */
	public enum StationState {
		// Welcome screen
		WELCOME,
		
		//Default state when waiting for items to be added
		NORMAL,
		
		// Item has been scanned, enter processing state to block new scans/additions until item has been put in bagging area. 
		PROCESSING_SCAN,
		
		// Scanned item need be bagged. Should return to scanning once bagged.
		BAGGING,
		
		// State for adding bags. Help scale observers differentiate reason for weight change.
		ADDING_BAGS,
		
		// Interim checkout menu, can go back and scan more items or proceed to some payment option
		CHECKOUT,
		
		//State for when user has paid total due, and system is waiting for them to take their items
		CLEANUP,
		
		// Make full or partial cash payment. Return to CHECKOUT when completed.
		PAY_CASH,		// **Only change with cash payment?
		
		// Make full or partial credit payment. Return to CHECKOUT when completed.
		PAY_CREDIT,	
		
		// Make full or partial debit payment. Return to CHECKOUT when completed.
		PAY_DEBIT,		// **Combine credit/debit into PAY_CARD? Any difference?
		
		// Dedicated state for scanning membership card/typing number
		ADD_MEMBERSHIP,
		
		// General post-checkout state. Print receipt? Dispense change? Just a thank you message? Returns to WELCOME
		FINISHED,
		
		//Blocking State, triggered by the attendant
		BLOCKED,
		
		// General error state. No implementation yet. Potentially when item is not bagged? Notify attendant?
		// Maybe error sub-states are required? Maintenance state?
		ERROR, 
		
		//State that Checkout station will default to on initialization
		//Represents the 'off' state, No GUI.
		//Any other state would represent an 'on' state.
		//This State can only be entered if system is in WELCOME state. (Not being used by a customer)
		//Later could have all system's methods except startupStation() not work if the state == INACTIVE
		INACTIVE
	}

	private StationState currentState = StationState.WELCOME;
	private StationState preBlockedState = getCurrentState();
	
	// Getters/setters
	
	public void setTotalDue(BigDecimal total) {
		totalDue = total;
	}
	public BigDecimal getTotalDue() {
		return totalDue;
	}
	
	public void setTotalMoneyPaid(BigDecimal total) {
		totalMoneyPaid = total;
	}
	public BigDecimal getTotalMoneyPaid() {
		return totalMoneyPaid;
	}
	
	public void setMembershipID(String ID) {
		membershipID = ID;
	}
	public String getMembershipID() {
		return membershipID;
	}
	
	public void addProductToCheckout(BarcodedProduct product) {
		ProductInfo PI = new ProductInfo(product);
		productsAddedToCheckout.put(product.getDescription(), PI);
	}
	public void addProductToCheckout(PLUCodedProduct product, double weight) {
		ProductInfo PI = new ProductInfo(product, weight);
		productsAddedToCheckout.put(product.getDescription(), PI);
	}
	public HashMap<String, ProductInfo> getProductsAddedToCheckoutHashMap() {
		return productsAddedToCheckout;
	}
	public ProductInfo getProductAddedToCheckout(String productDescription) {
		return productsAddedToCheckout.get(productDescription);
	}
	public void removeProductFromCheckoutHashMap(String description) {
		if (productsAddedToCheckout.remove(description) == null)
		{
			System.out.println("Error! Could not remove product with description: " + description);
		}
	}
	
	/*
	 *  State changing methods
	 */
	
	// Changes to new state while properly exiting old one (enabling/disabling relevant hardware)
	public void changeState(StationState targetState) {
		// Disable hardware for old state
		exitState(getCurrentState());
		// Enable hardware for new state
		switch(targetState) {
		
		case INACTIVE:
			station.mainScanner.disable();
			station.handheldScanner.disable();
			station.scanningArea.disable();
			disableAllDevices();
			wipeSessionData();
			
			//SIGNAL GUI TO CLOSE ALL WINDOWS
			break;
		
		case WELCOME:
			station.mainScanner.disable();
			station.handheldScanner.disable();
			station.scanningArea.disable();
			disablePaymentDevices();
			wipeSessionData();
			
			//SIGNAL GUI TO DISPLAY WELCOME SCREEN WINDOW
			
			break;
		
		case NORMAL:
			station.mainScanner.enable();
			station.handheldScanner.enable();
			station.scanningArea.enable();
			break;
		
		case PROCESSING_SCAN:
			station.mainScanner.disable();
			station.handheldScanner.disable();
			station.scanningArea.disable();
			break;
			
		case CHECKOUT:
			station.mainScanner.disable();
			station.handheldScanner.disable();
			disablePaymentDevices();
			break;
		
		case CLEANUP:
			station.mainScanner.disable();
			station.handheldScanner.disable();
			station.scanningArea.disable();
			disablePaymentDevices();
			break;
			
		case BAGGING:
			//Bagging Area should always be enabled
//			station.baggingArea.enable(); 
			break;
			
		case ADDING_BAGS:
			station.baggingArea.enable();
			break;
			
		case PAY_CASH:
			station.banknoteInput.enable();
			station.coinSlot.enable();
			break;
			
		case PAY_CREDIT:
			station.cardReader.enable();
			break;
			
		case PAY_DEBIT:
			station.cardReader.enable();
			break;
			
		case ADD_MEMBERSHIP:
			station.cardReader.enable();
			break;
			
		case FINISHED:
			station.printer.enable(); 	// **Not sure where we want receipt printed. Can be changed.
			break;
		
		case BLOCKED:
			ATTENDANT_BLOCK.set(true);
			setPreBlockedState(this.getCurrentState());
			disableAllDevices();
			//Add a method to inform the station of the block
			break;
			
		case ERROR:
			break;
			
		default:
			return;
		} 
		//Made it here, assume target state is valid
		setCurrentState(targetState);
	}
	
	private void exitState(StationState state) {
		switch(state) {
		
		case INACTIVE:
			break;
		
		case WELCOME:
			station.mainScanner.enable();
			station.handheldScanner.enable();
			station.scanningArea.enable();
			break;
		
		case NORMAL:
			break;
		
		case PROCESSING_SCAN:
			station.mainScanner.enable();
			station.handheldScanner.enable();
			station.scanningArea.enable();
			break;
		
		case CHECKOUT:
			break;
			
		case CLEANUP:
			break;
			
		case BAGGING:
			//Should always be enabled
//			station.baggingArea.disable();
			break;
			
		case ADDING_BAGS:
			station.baggingArea.disable();
			break;
			
		case PAY_CASH:
			station.banknoteInput.disable();
			station.coinSlot.disable();
			break;
			
		case PAY_CREDIT:
			station.cardReader.disable();
			break;
			
		case PAY_DEBIT:
			station.cardReader.disable();
			break;
			
		case ADD_MEMBERSHIP:
			station.cardReader.disable();
			break;
			
		case FINISHED:
			station.printer.disable();
			break;
			
		case BLOCKED:
			ATTENDANT_BLOCK.set(false);
			//Add a method to inform the station of the block removal
			
			break;
			
		case ERROR:
			break;
			
		default:
			return;
		}
	}
		
	private void wipeSessionData() {
		totalDue = BigDecimal.ZERO;
		totalMoneyPaid = BigDecimal.ZERO;
		expectedWeightNormalMode = 0.0;
		expectedWeightCheckout = 0.0;
		expectedWeightScanner = 0.0;
		membershipID = "null\n"; //Default to null, change when membership card is scanned in
		membershipPoints = "0\n";
		productsAddedToCheckout = new HashMap<String, ProductInfo>();
	}
	
	public BarcodeScanner getScanner(String type) {
		if (type.equals("main"))
		{
			return mainScanner;
		}
		else if (type.equals("hand"))
		{
			return handScanner;
		}
		else
		{
			System.out.println("Error! Unexpected Type!");
			return mainScanner;
		}
	}
	
	public BarcodeScanner getScanner() {
		//If no arg, default to main scanner
		return mainScanner;
	}
	
	public BanknoteSlot getBanknoteInputSlot() {
		return banknoteInputSlot;
	}
	public CoinSlot getCoinSlot() {
		return coinSlot;
	}
	public ElectronicScale getBaggingAreaScale() {
		return baggingAreaScale;
	}
	public ElectronicScale getScanningAreaScale() {
		return scanningAreaScale;
	}
	public CardReader getCardReader() {
		return cardReader;
	}
	public SelfCheckoutStation getStation() {
		return station;
	}
	public void setExpectedWeightCheckout(double weight) {
		expectedWeightCheckout = weight;
		
	}
	
	public double getExpectedWeightCheckout() {
		return expectedWeightCheckout;
		
	}
	
	public void setExpectedWeightNormalMode(double weight) {
		expectedWeightNormalMode = weight;
		
	}
	
	public double getExpectedWeightNormalMode() {
		return expectedWeightNormalMode;
		
	}
	
	public void setExpectedWeightScanner(double weight) {
		expectedWeightScanner = weight;
		
	}
	
	public double getExpectedWeightScanner() {
		return expectedWeightScanner;
		
	}
	public double getBagWeight() {
		return bagWeight;
	}
	
	public boolean getWeightValidCheckout() {
		return isWeightValidCheckout.get();
	}
	
	public void setWeightValidCheckout(boolean bool) {
		isWeightValidCheckout.set(bool);
	}
	
	public boolean getWeightValidNormalMode() {
		return isWeightValidNormalMode.get();
	}
	
	public void setWeightValidNormalMode(boolean bool) {
		isWeightValidNormalMode.set(bool);
	}
	
	public boolean getWeightValidScanner() {
		return isWeightValidScanner.get();
	}
	
	public void setWeightValidScanner(boolean bool) {
		isWeightValidScanner.set(bool);
	}
	
	//===========================Imported From CheckoutHandler===========================
	
	public void addToTotalCost(BigDecimal scannedItemPrice) {
		totalDue = totalDue.add(scannedItemPrice);

	}

	public void addToTotalPaid(BigDecimal amount) {
		totalMoneyPaid = totalMoneyPaid.add(amount);
		
		//This will be used to track how much money has been paid during one
		//payment run
		totalPaidThisTransaction  = totalPaidThisTransaction.add(amount);

	}
	
	public void resetTotalPaidThisTransaction()
	{
		totalPaidThisTransaction = BigDecimal.ZERO;
	}

	public void setInCheckout(boolean bool) {
		inCheckout.set(bool);

	}

	public boolean isInCheckout() {
		return inCheckout.get();
	}

	public boolean isInCleanup() {
		return inCleanup.get();
	}

	public void setInCleanup(boolean bool) {
		inCleanup.set(bool);
	}

	public void resetCheckoutTotals() {
		totalMoneyPaid = BigDecimal.ZERO;
		totalDue = BigDecimal.ZERO;
	}
	
	private void resetWeightFlags() {
		// Reset weight change flags
		isWeightValidCheckout.set(false);
		isWeightValidScanner.set(false);
		isWeightValidNormalMode.set(false);
	}
	
	public boolean isUsingOwnBags() {
		return isUsingOwnBags.get();
	}

	public void isUsingOwnBags(boolean bool) {
		isUsingOwnBags.set(bool);
	}
	
	public void configureBagWeight() {
		try (Scanner weightInput = new Scanner(System.in)) {
			System.out.println("Enter new weight of bags");
			bagWeight = weightInput.nextDouble();
			// configure new weight of bags
			if (bagWeight < 0) {
				throw new NegativeNumberException();
			}
		} catch (InputMismatchException e) {
			System.out.println("Must enter a valid weight for bags!");
		}	
	}

	public int compareTotals() {
		return totalMoneyPaid.compareTo(getTotalDue());
	}

	public boolean isWaitingForMembership() {
		return isCheckoutWaitingForMembership.get();
	}
	
	public void setWaitingForMembership(boolean bool) {
		isCheckoutWaitingForMembership.set(bool);;
	}
	
	public boolean getCardSwiped() {
		return cardSwipedCheckout.get();
	}
	
	public void setCardSwiped(boolean bool) {
		cardSwipedCheckout.set(bool);
	}
	
//	public void setCreditNumber(String num) {
//		creditNum = num;
//	}

	public boolean isWaitingForCreditCard() {
		return isCheckoutWaitingForCreditCard.get();
	}
	
	public void setWaitingForCreditCard(boolean bool) {
		isCheckoutWaitingForCreditCard.set(bool);
	}
	
	public BigDecimal getTotalPaidThisTransaction() {
		return totalPaidThisTransaction;
		
	}
	
	public void setTotalPaidThisTransaction(BigDecimal val) {
		totalPaidThisTransaction = val;
		
	}
	
	public void disablePaymentDevices() {
		this.station.coinSlot.disable();
		this.station.banknoteInput.disable();
		this.station.cardReader.disable();
	}

	public void enablePaymentDevices() {
		this.station.coinSlot.enable();
		this.station.banknoteInput.enable();
		this.station.cardReader.enable();
	}

	// Disable all devices - NOT FULLY IMPLEMENTED
	public void disableAllDevices() {
		this.station.baggingArea.disable();
		this.station.mainScanner.disable();
		disablePaymentDevices();
	}

	// Enable all devices - NOT FULLY IMPLEMENTED
	public void enableAllDevices() {
		this.station.baggingArea.enable();
		this.station.mainScanner.enable();
		enablePaymentDevices();
	}
	
	public void disableScannerDevices() {
		this.station.mainScanner.disable();
		this.station.handheldScanner.disable();
		
	}

	public void enableScannerDevices() {
		this.station.mainScanner.enable();
		this.station.handheldScanner.enable();
	}
	
	public boolean isFirstCheckout() {
		return isFirstCheckout.get();
	}
	
	public void setIsFirstCheckout(boolean bool) {
		isFirstCheckout.set(bool);
		
	}
	//===========================Imported From CheckoutHandler===========================
	
	
	//===========================For ScaleHandler===========================
	
	public boolean getIsBaggingAreaOverloaded() {
		return isBaggingAreaScaleOverloaded.get();
	}
	public void setIsBaggingAreaOverloaded(boolean bool) {
		isBaggingAreaScaleOverloaded.set(bool);
	}
	
	//===========================For ScaleHandler===========================
	
	//===========================For ScannerHandler===========================
	public boolean getIsScannerWaitingForWeightChange() {
		return isScannerWaitingForWeightChange.get();
	}
	public void setIsScannerWaitingForWeightChange(boolean bool) {
		isScannerWaitingForWeightChange.set(bool);
	}
	public double getBaggingAreaWeightVariablity() {
		return baggingAreaWeightVariability;
	}
	public Map<Barcode, BarcodedProduct> getBarcodedProductDatabase() {
		return Barcoded_Product_Database.getDatabase();
	}
	public BarcodedProductDatabase getBarcodedProductDatabaseObject() {
		return Barcoded_Product_Database;
	}
	
	public void resetScannerWeightFlags()
	{
		// Reset weight change flags
		setIsScannerWaitingForWeightChange(false);
		setWeightValidScanner(false);
	}
	public void compareAndSetWaitingForWeightChangeEvent(boolean expected, boolean update) {
		isScannerWaitingForWeightChange.compareAndSet(expected, update);
		
	}

	//===========================For ScannerHandler===========================
	
	public StationState getCurrentState() {
		return currentState;
	}
	public void setCurrentState(StationState currentState) {
		this.currentState = currentState;
	}
	public StationState getPreBlockedState() {
		return preBlockedState;
	}
	public void setPreBlockedState(StationState preBlockedState) {
		this.preBlockedState = preBlockedState;
	}
	

	public boolean getATTENDANT_BLOCK() {
		return ATTENDANT_BLOCK.get();
	}
	public void setATTENDANT_BLOCK(boolean bool) {
		ATTENDANT_BLOCK.set(bool);
	}
}