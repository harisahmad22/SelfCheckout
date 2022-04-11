package org.driver;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import org.controlSoftware.data.NegativeNumberException;
import org.controlSoftware.GUI.SelfCheckoutGUIMaster;
import org.driver.SelfCheckoutData.StationState;
import org.driver.SelfCheckoutSoftware;
import org.driver.databases.BarcodedProductDatabase;
import org.driver.databases.GiftCardDatabase;
import org.driver.databases.MembershipDatabase;
import org.driver.databases.TestBarcodedProducts;
import org.driver.databases.BarcodedProductDatabase;
import org.driver.databases.ProductInfo;
import org.driver.databases.BarcodedProductDatabase;
import org.driver.databases.PLUProductDatabase;
import org.driver.databases.PLUTestProducts;
import org.driver.databases.StoreInventory;
import org.driver.databases.TestBarcodedProducts;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.CoinSlot;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.CardIssuer;
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
 *  
 */

public class SelfCheckoutData {
	private AttendantUnit attendantUnit;
	private SelfCheckoutStationUnit thisUnit;
	
	//Test Coins/Banknotes
	public Currency CAD = Currency.getInstance("CAD");
	public Banknote fiveDollarBanknote = new Banknote(CAD, 5);
	public Banknote tenDollarBanknote = new Banknote(CAD, 10);
	public Banknote twentyDollarBanknote = new Banknote(CAD, 20);
	public Banknote fiveDollarBanknoteUSD = new Banknote(Currency.getInstance("USD"), 5);
	public Banknote twelveDollarBanknote = new Banknote(CAD, 12);
	public Coin nickel = new Coin(CAD, new BigDecimal("0.05"));
	public Coin dime = new Coin(CAD, new BigDecimal("0.10"));
	public Coin quarter = new Coin(CAD, new BigDecimal("0.25"));
	public Coin loonie = new Coin(CAD, new BigDecimal("1.00"));
	public Coin toonie = new Coin(CAD, new BigDecimal("2.00"));

	//============================Hardware Devices============================ 
	private SelfCheckoutStation stationHardware;

	private BarcodeScanner handScanner;
	private BarcodeScanner mainScanner;
	private BanknoteSlot banknoteInputSlot;
	private CoinSlot coinSlot;
	private ElectronicScale baggingAreaScale;
	private ElectronicScale scanningAreaScale;
	private CardReader cardReader;
	// ============================Hardware Devices============================

	private BigDecimal totalDue = BigDecimal.ZERO;
	private BigDecimal totalMoneyPaid = BigDecimal.ZERO;
	private BigDecimal totalPaidThisTransaction = BigDecimal.ZERO;
	private BigDecimal transactionPaymentAmount = BigDecimal.ZERO;
	private int transactionPaymentMethod = -1;
	
	private double expectedWeight = 0;

	private String guiBuffer = null;
	
	//The amount a product's weight can differ +/- from the listed weight in the lookup
	private double baggingAreaWeightVariability = 15;

	private static double bagWeight = 40;
	private String membershipID = "null"; // Default to null, change when membership card is scanned in
	private String giftCardNo = "null";
	// No implementation yet
	private String membershipPoints = "0\n";

	// List of scanned products for receipt generation and GUI
	// The ProductInfo class is a wrapper for a product that allows you to get its
	// associated weight
	// Which may come from a database or the scanning area scale
	// There are two methods to add to this map, either using a BarcodedProduct or a
	// PLUCodedProduct
	private HashMap<String, ProductInfo> productsAddedToCheckout = new HashMap<String, ProductInfo>(); // Key is product
																										// description,
																										// could be
																										// changed

	// Product Lookup
	private PLUCodedProduct lookedUpProduct;

	// ============================Software Flags============================
	private AtomicBoolean isWeightValidNormalMode = new AtomicBoolean(true);
	private AtomicBoolean isWeightValidCheckout = new AtomicBoolean(true);
	private AtomicBoolean isWeightValidScanner = new AtomicBoolean(false);

	private AtomicBoolean inCheckout = new AtomicBoolean(false);
	private AtomicBoolean inCleanup = new AtomicBoolean(false);
	private AtomicBoolean isUsingOwnBags = new AtomicBoolean(false);
	private AtomicBoolean cardSwipedCheckout = new AtomicBoolean(false);
	private AtomicBoolean isMidPayment = new AtomicBoolean(false);

	//Hannah Ku
	private AtomicBoolean isWeightOverride = new AtomicBoolean(false);
	
	private AtomicBoolean isFirstCheckout = new AtomicBoolean(true);
	private AtomicBoolean isBaggingAreaScaleOverloaded = new AtomicBoolean(false);
	private AtomicBoolean isScanningAreaScaleOverloaded = new AtomicBoolean(false);
	private AtomicBoolean isScannerWaitingForWeightChange = new AtomicBoolean(false);

	private AtomicBoolean isCheckoutWaitingForCreditCard = new AtomicBoolean(false);
	private AtomicBoolean isCheckoutWaitingForMembership = new AtomicBoolean(false);

	private AtomicBoolean ATTENDANT_BLOCK = new AtomicBoolean(false);
	//============================Software Flags============================
	
	private SelfCheckoutSoftware stationSoftware;
	private SelfCheckoutGUIMaster gui;
	
	private PLUProductDatabase PLU_Product_Database;
	private BarcodedProductDatabase Barcoded_Product_Database;
	private GiftCardDatabase giftCardDB;
	
	private StoreInventory Store_Inventory;
	
	private CardIssuer creditCardIssuer;
	private CardIssuer debitCardIssuer;
	
	private PLUTestProducts PLUTestProducts;
	private TestBarcodedProducts testProducts;
	private MembershipDatabase memberDB;
	

	public SelfCheckoutData(SelfCheckoutStation station) {
		//This class will give the software access to 
		//the hardware devices
		this.stationHardware = station;
		this.mainScanner = this.stationHardware.mainScanner;
		this.handScanner = this.stationHardware.handheldScanner;
		this.banknoteInputSlot = this.stationHardware.banknoteInput;
		this.coinSlot = this.stationHardware.coinSlot;
		this.baggingAreaScale = this.stationHardware.baggingArea;
		this.scanningAreaScale = this.stationHardware.scanningArea;
		this.cardReader = this.stationHardware.cardReader;
		
		//Initialize Card Issuers
		creditCardIssuer = new CardIssuer("Credit"); 
		debitCardIssuer = new CardIssuer("Debit"); 
		
		populateCardIssuers(); // Give each card issuer a test card
		
		//Initialize Databases
		
		//Initialize giftcard database (Contains 3 cards with different values)
		giftCardDB = new GiftCardDatabase();
		
		//Initialize Member datatbase
		memberDB = new MembershipDatabase();
		
		// Initialize some test Products
		// 3 Products, rice, pear, and banana
		PLUTestProducts = new PLUTestProducts();
		PLU_Product_Database = new PLUProductDatabase(PLUTestProducts.getPLUProductList());

		// Initialize some test Products
		// 3 Products, milk, orange juice, and corn flakes
		testProducts = new TestBarcodedProducts();
		Barcoded_Product_Database = new BarcodedProductDatabase(testProducts.getBarcodedProductList());

		// Initialize some test quantities for test Products within inventory
		// Quantity of products set to same arbitrary amount for all PLU or barcoded
		// products. NEED FIX
		Store_Inventory = new StoreInventory(PLUTestProducts, 3, testProducts, 4);
	}
	
	private void populateCardIssuers() {
		Card creditCard1 = new Card("Credit", "1", "Test Holder", "000", "1234", true, true);
		Calendar creditCard1Expiry = Calendar.getInstance();
		creditCard1Expiry.set(2025, 5, 10);
		creditCardIssuer.addCardData("1", "Test Holder", creditCard1Expiry, "000", new BigDecimal("5000"));
		
		Card debitCard1 = new Card("Debit", "1", "Test Holder", "000", "1234", true, true);
		Calendar debitCard1Expiry = Calendar.getInstance();
		debitCard1Expiry.set(2025, 5, 10);
		debitCardIssuer.addCardData("1", "Test Holder", debitCard1Expiry, "000", new BigDecimal("1200"));
	}

	public void registerGUI(SelfCheckoutGUIMaster newGui) {
		gui = newGui;
	}
	
	/* 
	 * High level states the self checkout station can be in
	 * Boolean state checks can be done in logic.
	 * 
	 * **I have no idea how this is going to mesh with multithreading. Consultation
	 * needed.
	 */
	public enum StationState {
		// Welcome screen
		WELCOME,
		
		//Default state when waiting for items to be added
		NORMAL,
		
		// Item has been scanned, enter processing state to block new scans/additions until item has been put in bagging area. 
		PROCESSING_SCAN,
		
		// Membership related states. Need only enable disable card reader for SWIPE state.
		ASK_MEMBERSHIP, SWIPE_MEMBERSHIP, TYPE_MEMBERSHIP, TEST_MEMBERSHIP,
		
		// Customer uses own bags related states.
		ASK_BAGS, ADDING_BAGS, ADDED_BAGS,
		
		// Ready for item to be scanned. Could proceed to checkout from here
		MAIN_SCAN, LETTER_SEARCH, PLU_SEARCH, CHECKOUT_CHECK,
		
		// Scanned item need be bagged. Should return to scanning once bagged.
		BAGGING,
		
		// State for prompting user if they would like to add their own bags
		ADD_BAGS_PROMPT,
		
		// State for prompting user how much they would like to pay
		PAYMENT_AMOUNT_PROMPT,
		
		// State for prompting user to choose how they would like to pay 
		PAYMENT_MODE_PROMPT,

		// Scanning.
		SCANNING,
		
		// Interim checkout menu, can go back and scan more items or proceed to some payment option
		CHECKOUT,
		
		//State for when user has paid total due, and system is waiting for them to take their items
		//Once Scale observer detects weight = 0 when in CLEANUP state, state will change to WELCOME
		CLEANUP,
		
		// Make full or partial cash payment. Return to CHECKOUT when completed.
		PAY_CASH, // **Only change with cash payment?

		// Make full or partial credit payment. Return to CHECKOUT when completed.
		PAY_CREDIT,

		// Make full or partial debit payment. Return to CHECKOUT when completed.
		PAY_DEBIT,		// **Combine credit/debit into PAY_CARD? Any difference?
		
		PAY_GIFTCARD,
		
		// State to handle checking if change is needed, dispensing and updating the receipt data
		// then displaying GUI window asking user if they would like a receipt, button listeners for this window
		// will call the hardware's printer print() method and cut_paper() method if the user chooses to get their receipt
		// After they have made a decision, check if there is still money to be paid. If so, move to NORMAL state, otherwise 
		// go to WELCOME state
		PRINT_RECEIPT_PROMPT,
		
		// General post-checkout state, check if more payment is needed, if so move to NORMAL state.
		// Otherwise move to CLEANUP state
		FINISHED,
		
		//Blocking State, triggered by the attendant
		BLOCKED,
		
		// Maybe error sub-states are required? Maintenance state?
		ERROR, 
		
		//State that Checkout station will default to on initialization
		//Represents the 'off' state, No GUI.
		//Any other state would represent an 'on' state.
		//This State can only be entered if system is in WELCOME state. (Not being used by a customer)
		//Later could have all system's methods except startupStation() not work if the state == INACTIVE
		INACTIVE, 
		
		//State that is entered after an item is scanned/looked up item put on scanning area scale
		//Will only be exited once Bagging area scale handler detects weight on scale == expected weight 
		//At which point system returns to NORMAL mode
		WAITING_FOR_ITEM,
		
		//State that is entered after user chooses an item from lookup and it needs to be put on scanner scale to learn its weight
		WAITING_FOR_LOOKUP_ITEM,
		
		//State that is entered once a weight issue is detected, can also be entered if weight is invalid after system begins waiting for item to be put down
		WEIGHT_ISSUE, 
		
		// State that allows system to exit from WEIGHT_ISSUE state, will update expected weights to current scale weight
		ATTENDANT_OVERRIDE, 
		
		// State to inform GUI to display keypad for user to enter in their payment amount
		PARTIAL_PAYMENT_KEYPAD,
		
		// State to inform user that their gift card does not have enough funds to complete transaction
		INSUFFICIENT_FUNDS,
		
		// State to inform user that given PLU code is not in database
		BAD_PLU, 
		
		// State to inform user that their membership is invalid 
		BAD_MEMBERSHIP, 
		
		// State to inform user that their card is invalid
		BAD_CARD
	}


	private StationState currentState = StationState.INACTIVE;
	private StationState preBlockedState = getCurrentState();

	
	/*
	 * State changing methods
	 */

	// Changes to new state while properly exiting old one (enabling/disabling relevant hardware)
	public void changeState(StationState targetState) {
		// Disable hardware for old state
		exitState(getCurrentState());
		// Enable hardware for new state

		switch(targetState) {
		
		case INACTIVE:
			disableAllDevices();
			wipeSessionData();
			//SIGNAL GUI TO CLOSE ALL WINDOWS
			break;

		case WELCOME:
			disableAllDevices();
			wipeSessionData(); 
			
			//SIGNAL GUI TO DISPLAY WELCOME SCREEN WINDOW
			
			break;
		
		case NORMAL:
			stationHardware.mainScanner.enable();
			stationHardware.handheldScanner.enable();
			stationHardware.scanningArea.enable();
			try { setExpectedWeight(stationHardware.baggingArea.getCurrentWeight()); } 
			catch (OverloadException e) 
			{ System.out.println("Error! Scale overloaded during transition to NORMAL state!"); }
			break;
		
		case PROCESSING_SCAN:
			stationHardware.mainScanner.disable();
			stationHardware.handheldScanner.disable();
			stationHardware.scanningArea.disable();
			break;
			
		case CHECKOUT:
			disableScannerDevices();
			stationHardware.scanningArea.disable();
			disablePaymentDevices();
			break;
			
		case CHECKOUT_CHECK:
			break;
			
		case BAD_PLU:
			break;
			
		case BAD_MEMBERSHIP:
			break;
			
		case BAD_CARD:
			break;
			
		case PARTIAL_PAYMENT_KEYPAD:
			break;

		case BAGGING: 
			break;
			
		case WAITING_FOR_ITEM:
			break;
			
		case WAITING_FOR_LOOKUP_ITEM:
			stationHardware.scanningArea.enable();
			break;
			
		case WEIGHT_ISSUE:
			System.out.println("WEIGHT ISSUE DETECTED!!!");
			setPreBlockedState(this.getCurrentState());
			break;
			
		case ADD_BAGS_PROMPT:
			System.out.println("Do you have any bags?");
			break;
		
		case PAYMENT_AMOUNT_PROMPT:
			//Ask user if they would like to pay partial or full
			break;
		
			
		case PAYMENT_MODE_PROMPT:
			//Ask user how they would like to pay (Cash, Credit, Debt, giftcard)
			break;

		case ADDING_BAGS:
			//System will remain in this state until a weight event occurs, if valid
			//will change to Add membership state
			break;
			
		case LETTER_SEARCH:
			break;
		
		case PLU_SEARCH:
			break;
			
		case PRINT_RECEIPT_PROMPT:
			// User has paid the current transaction amount, check for change
			// dispense, and update receipt. Then inform GUI to display receipt prompt window 
			// GUI listeners will handle when the user makes a choice. If they choose to print
			// then listener will call hardware methods to print. After, they will check if money still
			// needs to be paid, if so move to NORMAL state otherwise move to CLEANUP state
			disablePaymentDevices();
			stationHardware.printer.enable();
			setMidPaymentFlag(false);
			stationSoftware.getCheckoutHandler().handleChange();
			break;

		case PAY_CASH:
			setMidPaymentFlag(true);
			stationHardware.banknoteInput.enable();
			stationHardware.coinSlot.enable();
			enableScannerDevices();
			setExpectedWeight(getExpectedWeight());
			//Every-time a coin/banknote is put in, the CASH OBSERVER will update
			//total paid/total paid this transaction, then test if that payment 
			//event has increased the total paid this transaction to equal/exceed
			//the transaction payment amount (to support partial payment). If true
			//state will change to the PRINT_RECEIPT_PROMPT state, which will determine if system 
			//needs to give change, ask to print receipt, then go to the CLEANUP or NORMAL state
			//depending on if there is still a total due to be paid off
			break;

		case PAY_CREDIT:
			stationHardware.cardReader.enable();
			break;

		case PAY_DEBIT:
			stationHardware.cardReader.enable();
			break;
			
		case PAY_GIFTCARD:
			stationHardware.cardReader.enable();
			break;
			
		case INSUFFICIENT_FUNDS:
			break;
			
		case ASK_MEMBERSHIP:
			try { setExpectedWeight(stationHardware.baggingArea.getCurrentWeight()); } 
			catch (OverloadException e) 
			{ System.out.println("Bagging area scale overload during ask membership state change!"); }
			stationHardware.cardReader.enable();
			setWaitingForMembership(true);
		    //Method will change state to Checkout if user manually entered ID
			//Otherwise system will change to checkout state after card is swiped
			break;
	
		case SWIPE_MEMBERSHIP:
			this.stationHardware.cardReader.enable();
			break;
			
		case TYPE_MEMBERSHIP:
			break;
		
		case ASK_BAGS:
			break;

		case FINISHED:
			break;
		
		case BLOCKED:
			ATTENDANT_BLOCK.set(true);
			setPreBlockedState(this.getCurrentState());
			disableAllDevices();
			break;

		case ERROR:
			break;

		default:
			return;
		} 
		//Made it here, assume target state is valid
		System.out.println("State Change: " + targetState);
		setCurrentState(targetState);
		notifyStateChanged();
	}
	
	private void exitState(StationState state) {
		switch(state) {
		
		case INACTIVE:
			break;

		case WELCOME:
			stationHardware.mainScanner.enable();
			stationHardware.handheldScanner.enable();
			stationHardware.scanningArea.enable();
			break;
		
		case NORMAL:
			break;
		
		case PROCESSING_SCAN:
			stationHardware.mainScanner.enable();
			stationHardware.handheldScanner.enable();
			stationHardware.scanningArea.enable();
			break;
		
		case CHECKOUT:
			break;
			
		case BAGGING:
			break;
			
		case ADD_BAGS_PROMPT:
			break;
			
		case PAYMENT_AMOUNT_PROMPT:
			break;

		case ADDING_BAGS:
			break;

		case PAY_CASH:
			stationHardware.banknoteInput.disable();
			stationHardware.coinSlot.disable();
			break;

		case PAY_CREDIT:
			stationHardware.cardReader.disable();
			break;

		case PAY_DEBIT:
			stationHardware.cardReader.disable();
			break;
			
		case PAY_GIFTCARD:
			stationHardware.cardReader.disable();
			break;

		case FINISHED:
			break;
			
		case BLOCKED:
			ATTENDANT_BLOCK.set(false);
			break;

		case ERROR:
			break;

		default:
			return;
		}
	}

	// Getters/setters
	
	public CardIssuer getDebitCardIssuer() {
		return debitCardIssuer;
	}

	public CardIssuer getCreditCardIssuer() {
		return creditCardIssuer;
	}

	public PLUTestProducts getPLUTestProducts() {
		return PLUTestProducts;
	}

	public TestBarcodedProducts getTestProducts() {
		return testProducts;
	}

	
	public SelfCheckoutSoftware getStationSoftware()
	{
		return this.stationSoftware;
	}

	public void setMidPaymentFlag(boolean b) {
		isMidPayment.set(b);		
	}
	
	public boolean getMidPaymentFlag() {
		return isMidPayment.get();		
	}

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

	public GiftCardDatabase getGiftCardDatabase() {
		return giftCardDB;
	}

	public void debugAddProductToCheckout(BarcodedProduct product) {
		ProductInfo PI = new ProductInfo(product);
		if (productsAddedToCheckout.containsKey(product.getDescription()))
		{
			productsAddedToCheckout.get(product.getDescription()).increaseQuantity();
		}
		else {
			productsAddedToCheckout.put(product.getDescription(), PI);
		}
	}
	
	public void addProductToCheckout(BarcodedProduct product) {
		ProductInfo PI = new ProductInfo(product);
		if (productsAddedToCheckout.containsKey(product.getDescription()))
		{
			productsAddedToCheckout.get(product.getDescription()).increaseQuantity();
		}
		else {
			productsAddedToCheckout.put(product.getDescription(), PI);
		}
		changeState(StationState.WAITING_FOR_ITEM);
	}

	public void addProductToCheckout(PLUCodedProduct product, double weightInGrams) {
		ProductInfo PI = new ProductInfo(product, weightInGrams);
		
		BigDecimal weightInKG = new BigDecimal(weightInGrams/1000);
		//Update price based on the given weight
		addToTotalCost(product.getPrice().multiply(weightInKG));
		
		if (productsAddedToCheckout.containsKey(product.getDescription()))
		{
			productsAddedToCheckout.get(product.getDescription()).increaseWeight(weightInGrams);
		}
		else {
			productsAddedToCheckout.put(product.getDescription(), PI);
		}
		changeState(StationState.WAITING_FOR_ITEM);
	}

	public HashMap<String, ProductInfo> getProductsAddedToCheckoutHashMap() {
		return productsAddedToCheckout;
	}
	
	public ProductInfo getProductAddedToCheckout(String productDescription) {
		return productsAddedToCheckout.get(productDescription);
	}

	public void removeProductFromCheckoutHashMap(String description) {
		ProductInfo product = productsAddedToCheckout.remove(description);		
		if (product == null) {
			System.out.println("Error! Could not remove product with description: " + description);
		}
		else
		{
			setTotalDue(getTotalDue().subtract(product.getProduct().getPrice()));
			setExpectedWeight(getExpectedWeight() - product.getWeight());
		}
	}

	public void setGuiBuffer(String text) {
		guiBuffer = text;
		System.out.println("GUI buffer in self checkout data set to " + guiBuffer);
	}
	public String getGuiBuffer() {
		return guiBuffer;
	}
	
	
	public void attachStationSoftware(SelfCheckoutSoftware stationSoftware) {
		this.stationSoftware = stationSoftware;
	}
	
	private void notifyStateChanged() {
		gui.stateChanged();
		
	}
		
	private void wipeSessionData() {
		totalDue = BigDecimal.ZERO;
		totalMoneyPaid = BigDecimal.ZERO;
		transactionPaymentAmount = BigDecimal.ZERO;
		totalPaidThisTransaction = BigDecimal.ZERO;
		isFirstCheckout.set(true);
		setExpectedWeight(0.0);
		membershipID = "null\n"; //Default to null, change when membership card is scanned in
		productsAddedToCheckout = new HashMap<String, ProductInfo>();
	}

	public BarcodeScanner getScanner(String type) {
		if (type.equals("main")) {
			return mainScanner;
		} else if (type.equals("hand")) {
			return handScanner;
		} else {
			System.out.println("Error! Unexpected Type!");
			return mainScanner;
		}
	}

	public BarcodeScanner getScanner() {
		// If no arg, default to main scanner
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

	public SelfCheckoutStation getStationHardware() {
		return stationHardware;
	}

	public void setExpectedWeight(double weight) {
		expectedWeight = weight;
		
	}
	
	public double getExpectedWeight() {
		return expectedWeight;
		
	}
		
	//Hannah Ku
	public boolean isWeightOverride() {
		return isWeightOverride.get();
	}
	//Hannah Ku
	public void setIsWeightOverride(boolean bool) {
		isWeightOverride.set(bool);
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

	public void addToTransactionPaymentAmount(BigDecimal amount) {
		transactionPaymentAmount = transactionPaymentAmount.add(amount);

	}
	
	public void setTransactionPaymentAmount(BigDecimal paymentAmount) {
		transactionPaymentAmount = paymentAmount;
	}
	
	public BigDecimal getTransactionPaymentAmount() {
		return transactionPaymentAmount;
	}
	
	public int getTransactionPaymentMethod(int i) {
		return transactionPaymentMethod;
		
	}
	
	public void setTransactionPaymentMethod(int method) {
		//0 = Cash, 1 = credit, 2 = debt
		transactionPaymentMethod = method;
		
	}
	
	//===========================Imported From CheckoutHandler===========================
	public void addToTotalCost(BigDecimal scannedItemPrice) {
		totalDue = totalDue.add(scannedItemPrice);

	}

	public void addToTotalPaid(BigDecimal amount) {
		totalMoneyPaid = totalMoneyPaid.add(amount);

		// This will be used to track how much money has been paid during one
		// payment run
		totalPaidThisTransaction = totalPaidThisTransaction.add(amount);

	}

	public void resetTotalPaidThisTransaction() {
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
		isCheckoutWaitingForMembership.set(bool);
		;
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
	
	public void addToTotalPaidThisTransaction(BigDecimal scannedItemPrice) {
		totalPaidThisTransaction = totalPaidThisTransaction.add(scannedItemPrice);

	}

public void disablePaymentDevices() {
		this.stationHardware.coinSlot.disable();
		this.stationHardware.banknoteInput.disable();
		this.stationHardware.cardReader.disable();
	}

	public void enablePaymentDevices() {
		this.stationHardware.coinSlot.enable();
		this.stationHardware.banknoteInput.enable();
		this.stationHardware.cardReader.enable();
	}

	// Disable all devices
	public void disableAllDevices() {
		this.stationHardware.scanningArea.disable();
		disableScannerDevices();
		disablePaymentDevices();
	}

	// Enable all devices
	public void enableAllDevices() {
		this.stationHardware.scanningArea.enable();
		disableScannerDevices();
		enablePaymentDevices();
	}

	public void disableScannerDevices() {
		this.stationHardware.mainScanner.disable();
		this.stationHardware.handheldScanner.disable();
	}

	public void enableScannerDevices() {
		this.stationHardware.mainScanner.enable();
		this.stationHardware.handheldScanner.enable();
	}

	public boolean isFirstCheckout() {
		return isFirstCheckout.get();
	}

	public void setIsFirstCheckout(boolean bool) {
		isFirstCheckout.set(bool);

	}
	// ===========================Imported From CheckoutHandler===========================

	// ===========================For ScaleHandler===========================

	public boolean getIsBaggingAreaOverloaded() {
		return isBaggingAreaScaleOverloaded.get();
	}

	public void setIsBaggingAreaOverloaded(boolean bool) {
		isBaggingAreaScaleOverloaded.set(bool);
	}

	// ===========================For ScaleHandler===========================

	// ===========================For ScannerHandler===========================
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
	
	public PLUProductDatabase getPLUDatabaseObject() {
		return PLU_Product_Database;
	}

	public BarcodedProductDatabase getBarcodedProductDatabaseObject() {
		return Barcoded_Product_Database;
	}

	public void resetScannerWeightFlags() {
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

	public String getGiftCardNo() {
		return giftCardNo;
	}	
	
	public void setGiftCardNo(String number) {
		giftCardNo = number;
	}

	public void setLookedUpProduct(PLUCodedProduct PLUProduct) {
		lookedUpProduct = PLUProduct;
	}	
	public PLUCodedProduct getLookedUpProduct() {
		return lookedUpProduct;
	}
	
	public void setAttendantUnit (AttendantUnit newUnit) {
		attendantUnit = newUnit;
	}
	
	public AttendantUnit getAttendantUnit () {
		return attendantUnit;
	}
	
	public void setThisUnit (SelfCheckoutStationUnit newUnit) {
		thisUnit = newUnit;
	}
	
	public SelfCheckoutStationUnit getThisUnit () {
		return thisUnit;
	}
}