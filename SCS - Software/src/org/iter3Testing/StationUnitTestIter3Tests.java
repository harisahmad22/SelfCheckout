//Brody Long - 30022870 
//Kamrul Ahsan Noor- 30078754

package org.iter3Testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.controlSoftware.*;
import org.controlSoftware.customer.CheckoutHandler;
import org.controlSoftware.data.BankClientInfo;
import org.controlSoftware.data.NegativeNumberException;
import org.controlSoftware.deviceHandlers.BaggingAreaScaleHandler;
import org.controlSoftware.deviceHandlers.ScannerHandler;
import org.controlSoftware.deviceHandlers.ReceiptHandler;
import org.controlSoftware.deviceHandlers.membership.ScansMembershipCard;
import org.controlSoftware.deviceHandlers.payment.CashPaymentHandler;
import org.controlSoftware.deviceHandlers.payment.PayWithCreditCard;
import org.controlSoftware.deviceHandlers.payment.PayWithDebitCard;
import org.controlSoftware.general.TouchScreenSoftware;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutSoftware;
import org.driver.SelfCheckoutStationUnit;
import org.driver.SelfCheckoutData.StationState;
import org.driver.databases.TestBarcodedProducts;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.BanknoteValidatorObserver;
import org.lsmr.selfcheckout.devices.observers.BarcodeScannerObserver;
import org.lsmr.selfcheckout.devices.observers.CoinDispenserObserver;
import org.lsmr.selfcheckout.devices.observers.CoinStorageUnitObserver;
import org.lsmr.selfcheckout.devices.observers.CoinValidatorObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;
import org.lsmr.selfcheckout.products.BarcodedProduct;

@RunWith(JUnit4.class)
public class StationUnitTestIter3Tests {
	
	//We will be overriding the regular System.in input stream with a
	//Byte array input stream to simulate user input 
	//This idea comes from the stack overflow post: https://stackoverflow.com/a/6416179
	private final InputStream backupInputStream = System.in; //Save a backup of System.in
	private InputStream customInputStream = backupInputStream;
	
	
	private static Banknote fiveDollarBanknote = new Banknote(SelfCheckoutStationUnit.CAD, 5);
	private static Banknote tenDollarBanknote = new Banknote(SelfCheckoutStationUnit.CAD, 10);
	private static Banknote twentyDollarBanknote = new Banknote(SelfCheckoutStationUnit.CAD, 20);
	private static Banknote fiftyDollarBanknote = new Banknote(SelfCheckoutStationUnit.CAD, 50);
	private static Banknote fiveDollarBanknoteUSD = new Banknote(Currency.getInstance("USD"), 5);
	private static Banknote twelveDollarBanknote = new Banknote(SelfCheckoutStationUnit.CAD, 12);
	
	private static Coin nickel = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal("0.05"));
	private static Coin quarter = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal("0.25"));
	private static Coin loonie = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal("1.00"));
	private static Coin toonie = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal("2.00"));
	private static Coin quarterUSD = new Coin(Currency.getInstance("USD"), new BigDecimal("0.25"));
	private static Coin invalidCoin = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal("0.75"));
	
	public static int banknoteChangeValue = 0; //Updated by DanglingBanknoteRemover runnable 
	
	private BarcodedProduct milkJug;
	private BarcodedProduct orangeJuice;
	private BarcodedProduct cornFlakes;
	private BarcodedItem milkJugItem;
	private BarcodedItem orangeJuiceItem;
	private BarcodedItem cornFlakesItem;
	
	private Barcode testBagBarcode = new Barcode(new Numeral[] { Numeral.nine, Numeral.nine, Numeral.nine});
	private BarcodedItem testBag = new BarcodedItem(testBagBarcode, 115.5); //115.5g bag 	
	private BarcodedItem testBag2 = new BarcodedItem(testBagBarcode, 115.5); //115.5g bag
	
	private ReceiptHandler receiptHandler;
	private ScansMembershipCard customMembershipScannerObserver;
	
	private SelfCheckoutStationUnit stationUnit;
	private SelfCheckoutStation stationHardware;
	private SelfCheckoutData stationData;
	private SelfCheckoutSoftware stationSoftware;
	private TouchScreenSoftware touchScreenSoftware;
	private TestBarcodedProducts testProducts;
	
	private ScheduledExecutorService scheduler;
	
	

	//Initialize
	@Before
	public void setup() {
		
		this.stationUnit = new SelfCheckoutStationUnit(1);
		
		this.stationHardware = stationUnit.getSelfCheckoutStationHardware();
		this.stationData = stationUnit.getSelfCheckoutData();
		this.stationSoftware = stationUnit.getSelfCheckoutSoftware();
		this.touchScreenSoftware = stationUnit.getTouchScreenSoftware();
		
		
		//Create some test products/items
		this.testProducts = new TestBarcodedProducts();
		
		milkJug = stationData.getBarcodedProductDatabase()
					.get(testProducts.getBarcodeList().get(0));
		milkJugItem = testProducts.getItem(milkJug);
		
		orangeJuice = stationData.getBarcodedProductDatabase()
				.get(testProducts.getBarcodeList().get(1));
		orangeJuiceItem = testProducts.getItem(orangeJuice);
		
		cornFlakes = stationData.getBarcodedProductDatabase()
				.get(testProducts.getBarcodeList().get(2));
		cornFlakesItem = testProducts.getItem(cornFlakes);
		
		
		//Setup receipt printer
		try {
			this.stationHardware.printer.addInk(2500);
			this.stationHardware.printer.addPaper(512);
		} catch (OverloadException e) {
			System.out.println("Overfilled!");
			e.printStackTrace();
		}
		
		//Setup receipt printer
					
		this.scheduler = Executors.newScheduledThreadPool(5);

	}
	 
          
    @Test
    public void addBagsTest() throws InterruptedException, OverloadException, EmptyException, DisabledException {
    	
    	stationData.setCurrentState(StationState.ADDING_BAGS);
    	
    	this.stationHardware.baggingArea.add(testBag);
    	
        assertTrue(stationData.getCurrentState() == StationState.ASK_MEMBERSHIP);
    }
    
    
    @Test
    public void testInvalidWeightDuringNormalState() throws InterruptedException, OverloadException, EmptyException, DisabledException {
  
    	stationData.changeState(StationState.NORMAL);
    	
    	this.stationHardware.baggingArea.add(milkJugItem);
    	
        // verify device is disabled or not
    	 assertTrue(stationData.getCurrentState() == StationState.WEIGHT_ISSUE);
    }
    
    @Test
    public void checkPreBlockedStateAfterForcedBlock() throws InterruptedException, OverloadException, EmptyException, DisabledException {
  
    	stationData.changeState(StationState.NORMAL);
    	stationData.changeState(StationState.BLOCKED);
        	
    	assertTrue(stationData.getPreBlockedState() == StationState.NORMAL);
    }
   
    @Test
    public void testProductAddedToStationDataHashMap() throws InterruptedException, OverloadException, EmptyException, DisabledException {
  
    	stationData.changeState(StationState.NORMAL);
    	
    	scheduler.schedule(new PlaceItemOnScaleRunnable(stationHardware.baggingArea, cornFlakesItem), 1000, TimeUnit.MILLISECONDS);
            	
    	stationHardware.mainScanner.scan(cornFlakesItem);

    	TimeUnit.SECONDS.sleep(2);
    	
    	assertTrue(stationData.getProductsAddedToCheckoutHashMap().containsKey(cornFlakes.getDescription()));
    }
    
    @Test
    public void testDataResetOnReturnToWelcomeScreen() throws InterruptedException, OverloadException, EmptyException, DisabledException {
  
    	stationData.changeState(StationState.NORMAL);
    	scheduler.schedule(new PlaceItemOnScaleRunnable(stationHardware.baggingArea, cornFlakesItem), 1000, TimeUnit.MILLISECONDS);    	
    	stationHardware.mainScanner.scan(cornFlakesItem);
    	
    	assertTrue(stationData.getProductsAddedToCheckoutHashMap().containsKey(cornFlakes.getDescription()));
    	
    	TimeUnit.SECONDS.sleep(2);
    	
    	stationData.changeState(StationState.WELCOME);
    	
    	assertTrue(stationData.getProductsAddedToCheckoutHashMap().isEmpty());
    }
    
    @Test
    public void testCorrectInitialState() throws InterruptedException, OverloadException, EmptyException, DisabledException {
    	stationData.changeState(StationState.SWIPE_MEMBERSHIP);
    	
    	    	
    	stationHardware.mainScanner.scan(cornFlakesItem);
    	assertTrue(stationData.getProductsAddedToCheckoutHashMap().containsKey(cornFlakes.getDescription()));
    	
    	stationData.changeState(StationState.WELCOME);
    	
    	assertTrue(stationData.getProductsAddedToCheckoutHashMap().isEmpty());
    }    
    
    
    
    ////////////////////////////////////////////////////////////////////////////////////////
    
    @Test
    public void testGiveValidMembershipCard() throws InterruptedException, OverloadException, EmptyException, DisabledException {

    	stationData.changeState(StationState.SWIPE_MEMBERSHIP);
    	scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "swipe", "Member", "1", "Test", null, null, false, false), 100, TimeUnit.MILLISECONDS);   	
    	
    	TimeUnit.SECONDS.sleep(1);
    	
    	assertTrue(stationData.getCurrentState() == StationState.PAYMENT_AMOUNT_PROMPT);
    	assertTrue(stationData.getMembershipID().equals("1"));
    }
    
    @Test
    public void testGiveInValidMembershipCard() throws InterruptedException, OverloadException, EmptyException, DisabledException {

    	stationData.changeState(StationState.SWIPE_MEMBERSHIP);
    	scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "swipe", "Member", "9999", "Test", null, null, false, false), 100, TimeUnit.MILLISECONDS);   	
    	
    	TimeUnit.SECONDS.sleep(1);
    	
    	assertTrue(stationData.getCurrentState() == StationState.BAD_MEMBERSHIP);
    	assertTrue(stationData.getMembershipID().equals("null"));
    }


    @Test
    public void testGiveWrongCardAsMembershipCard() throws InterruptedException, OverloadException, EmptyException, DisabledException {
    	stationData.changeState(StationState.SWIPE_MEMBERSHIP);
    	scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "swipe", "Credit", "1", "Test", null, null, false, false), 100, TimeUnit.MILLISECONDS);   	
    	
    	TimeUnit.SECONDS.sleep(1);
    	
    	assertTrue(stationData.getCurrentState() == StationState.BAD_MEMBERSHIP);
    	assertTrue(stationData.getMembershipID().equals("null"));
    }


    @Test
    public void testCoinChangeDispensed() throws InterruptedException, OverloadException, EmptyException, DisabledException {
    	
    	BigDecimal total = new BigDecimal("50");
    	stationData.setTotalDue(total); //Add $50 to total cost
    	stationData.setTransactionPaymentAmount(total);
    	//Create a list of banknotes exceeding the total cost of all items
    	Banknote[] banknotes1 = { twentyDollarBanknote, twentyDollarBanknote, fiveDollarBanknote };
    	Coin[] coins = { quarter, toonie, toonie, toonie};
    	
    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
    	//There is a 1 second delay between each banknote insertion.
    	scheduler.schedule(new PayWithBanknotesRunnable(this.stationHardware.banknoteInput, banknotes1), 100, TimeUnit.MILLISECONDS);
    	scheduler.schedule(new PayWithCoinsRunnable(this.stationHardware.coinSlot, coins), 3200, TimeUnit.MILLISECONDS);
    	
    	stationData.changeState(StationState.PAY_CASH);
		
    	TimeUnit.SECONDS.sleep(10);
    	
    	String finalReceipt = this.stationHardware.printer.removeReceipt();
		System.out.println("Receipt Generated:\n" + finalReceipt);
    	
		//Get Change from tray
		List<Coin> change = this.stationHardware.coinTray.collectCoins();
		BigDecimal changeValue = BigDecimal.ZERO;
		
		for (Coin c : change) { if (!(c == null)) { changeValue = changeValue.add(c.getValue()); } }
    	
		//Touch screen should have been informed of change being dispensed
    	assertTrue(banknoteChangeValue == 0);
    	assertTrue(changeValue.equals(new BigDecimal("1.25")));
    }


    @Test
    public void testBanknoteChangeDispensed() throws InterruptedException, OverloadException, EmptyException, DisabledException {
    	
    	BigDecimal total = new BigDecimal("55");
    	stationData.setTotalDue(total); //Add $50 to total cost
    	stationData.setTransactionPaymentAmount(total);
    	//Create a list of banknotes exceeding the total cost of all items
    	Banknote[] banknotes1 = { fiftyDollarBanknote, twentyDollarBanknote };
    	
    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
    	//There is a 1 second delay between each banknote insertion.
    	scheduler.schedule(new PayWithBanknotesRunnable(this.stationHardware.banknoteInput, banknotes1), 1000, TimeUnit.MILLISECONDS);
    	
    	
    	//Will be expecting $15 in change via 1x $10 note, and 1x $5 note
    	scheduler.schedule(new RemoveDanglingBanknotesRunnable(this.stationHardware.banknoteOutput, 2), 3000, TimeUnit.MILLISECONDS);
    	
    	stationData.changeState(StationState.PAY_CASH);
		
    	TimeUnit.SECONDS.sleep(15);
    	
		String finalReceipt = this.stationHardware.printer.removeReceipt();
		System.out.println("Receipt Generated:\n" + finalReceipt);
		
		BigDecimal changeValue = BigDecimal.ZERO;
		//Get Change from tray
		List<Coin> change = this.stationHardware.coinTray.collectCoins();
		
		for (Coin c : change) { if (!(c == null)) { changeValue = changeValue.add(c.getValue()); } }
		
    	assertTrue(banknoteChangeValue == 15);
    	assertTrue(changeValue.equals(new BigDecimal("0")));
    }
    
    @Test
    public void testBanknoteAndCoinChangeDispensed() throws InterruptedException, OverloadException, EmptyException, DisabledException {
    	BigDecimal total = new BigDecimal("55");
    	stationData.setTotalDue(total); //Add $50 to total cost
    	stationData.setTransactionPaymentAmount(total);

    	Banknote[] banknotes1 = { fiftyDollarBanknote }; 
    	Coin[] coins = { quarter, quarter, quarter, loonie, toonie};
    	Banknote[] banknotes2 = { tenDollarBanknote };
    	//Totals $63.75 - Should return $8.75 in change
    	//Should be given: 1x $5, 1x $2, 1x $1, 3x $0.25 back 
    	
    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
    	//There is a 1 second delay between each banknote insertion.
    	scheduler.schedule(new PayWithBanknotesRunnable(this.stationHardware.banknoteInput, banknotes1), 1000, TimeUnit.MILLISECONDS);
    	scheduler.schedule(new PayWithCoinsRunnable(this.stationHardware.coinSlot, coins), 2000, TimeUnit.MILLISECONDS);
    	scheduler.schedule(new PayWithBanknotesRunnable(this.stationHardware.banknoteInput, banknotes2), 7000, TimeUnit.MILLISECONDS);
    	
    	//Will be expecting $15 in change via 1x $10 note, and 1x $5 note
    	scheduler.schedule(new RemoveDanglingBanknotesRunnable(this.stationHardware.banknoteOutput, 1), 10000, TimeUnit.MILLISECONDS);    	
    	
    	stationData.changeState(StationState.PAY_CASH);
		
    	TimeUnit.SECONDS.sleep(15);
    	
		String finalReceipt = this.stationHardware.printer.removeReceipt();
		System.out.println("Receipt Generated:\n" + finalReceipt);
		
		BigDecimal changeValue = BigDecimal.ZERO;
		//Get Change from tray
		List<Coin> change = this.stationHardware.coinTray.collectCoins();
		
		for (Coin c : change) { if (!(c == null)) { changeValue = changeValue.add(c.getValue()); } }
		
		//Touch screen should have been informed of change being dispensed
    	assertTrue(banknoteChangeValue == 5);
    	assertTrue(changeValue.equals(new BigDecimal("3.75")));
    }
 
    @Test
    public void testChangeDispensedWithEmptyDispensers() throws InterruptedException, OverloadException, EmptyException, DisabledException {
    	
    	BigDecimal total = new BigDecimal("55");
    	stationData.setTotalDue(total); //Add $55 to total cost
    	stationData.setTransactionPaymentAmount(total);
    	
    	//Remove all $5 bills, and all toonies, shold be given back the coins we put in 
    	// plus $5 in loonies
    	this.stationHardware.banknoteDispensers.get(5).unload();
    	this.stationHardware.coinDispensers.get(new BigDecimal("2.00")).unload();
    	
    	Banknote[] banknotes1 = { fiftyDollarBanknote }; 
    	Coin[] coins = { quarter, quarter, quarter, loonie, toonie};
    	Banknote[] banknotes2 = { tenDollarBanknote };
    	//Totals $63.75 - Should return $8.75 in change
    	//Should be given: 1x $5, 1x $2, 1x $1, 3x $0.25 back 
    	
    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
    	//There is a 1 second delay between each banknote insertion.
    	scheduler.schedule(new PayWithBanknotesRunnable(this.stationHardware.banknoteInput, banknotes1), 1000, TimeUnit.MILLISECONDS);
    	scheduler.schedule(new PayWithCoinsRunnable(this.stationHardware.coinSlot, coins), 2000, TimeUnit.MILLISECONDS);
    	scheduler.schedule(new PayWithBanknotesRunnable(this.stationHardware.banknoteInput, banknotes2), 7000, TimeUnit.MILLISECONDS);
    	
    	//Will be expecting $15 in change via 1x $10 note, and 1x $5 note
    	scheduler.schedule(new RemoveDanglingBanknotesRunnable(this.stationHardware.banknoteOutput, 1), 8500, TimeUnit.MILLISECONDS);
    	
    	stationData.changeState(StationState.PAY_CASH);
		
    	TimeUnit.SECONDS.sleep(15);
    	

		String finalReceipt = this.stationHardware.printer.removeReceipt();
		System.out.println("Receipt Generated:\n" + finalReceipt);
		
		BigDecimal changeValue = BigDecimal.ZERO;
		//Get Change from tray
		List<Coin> change = this.stationHardware.coinTray.collectCoins();
		
		for (Coin c : change) { if (!(c == null)) { changeValue = changeValue.add(c.getValue()); } }
		

    	assertTrue(banknoteChangeValue == 0);
    	assertTrue(changeValue.equals(new BigDecimal("8.75")));
    }
    

    
    @Test
    public void testPayingWithCashAddItemNoScan() throws InterruptedException, OverloadException, EmptyException, DisabledException {
    	//Change will be given out
    	//Weight does not change during payment
    	//Cleanup will be tested in here also
    	BigDecimal total = new BigDecimal("50");
    	stationData.setTotalDue(total); //Add $50 to total cost
    	stationData.setTransactionPaymentAmount(total);
    	stationData.setMidPaymentFlag(true);
    	//Create a list of banknotes exceeding the total cost of all items
    	Banknote[] banknotes1 = { twentyDollarBanknote };
    	Banknote[] banknotes2 = { twentyDollarBanknote, fiveDollarBanknote };
    	Coin[] coins = { loonie, toonie, toonie };
    	
    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
    	//There is a 1 second delay between each banknote insertion.
    	scheduler.schedule(new PayWithBanknotesRunnable(this.stationHardware.banknoteInput, banknotes1), 1000, TimeUnit.MILLISECONDS);
    	scheduler.schedule(new PlaceItemOnScaleRunnable(this.stationHardware.baggingArea, milkJugItem), 2000, TimeUnit.MILLISECONDS);
    	scheduler.schedule(new RemoveItemOnScaleRunnable(this.stationHardware.baggingArea, milkJugItem), 3500, TimeUnit.MILLISECONDS);
    	scheduler.schedule(new PayWithBanknotesRunnable(this.stationHardware.banknoteInput, banknotes2), 4000, TimeUnit.MILLISECONDS);
    	scheduler.schedule(new PayWithCoinsRunnable(this.stationHardware.coinSlot, coins), 6500, TimeUnit.MILLISECONDS);
    	
    	stationData.changeState(StationState.PAY_CASH);
    	TimeUnit.SECONDS.sleep(2);
    	assertTrue(stationData.getCurrentState() == StationState.WEIGHT_ISSUE);
    	
    	TimeUnit.SECONDS.sleep(8);
    	
		String finalReceipt = this.stationHardware.printer.removeReceipt();
		System.out.println("Receipt Generated:\n" + finalReceipt);
		
    	assertTrue(stationData.getCurrentState() == StationState.PRINT_RECEIPT_PROMPT);
    	
    }
        
    @Test
    public void testPartialPaymentWithCash() throws InterruptedException, OverloadException, EmptyException, DisabledException {
    	    	
    	BigDecimal total = new BigDecimal("100");
    	stationData.setTotalDue(total); //Add $100 to total cost
    	stationData.setTransactionPaymentAmount(new BigDecimal("50"));
    	
    	//Create a list of banknotes exceeding the total cost of all items
    	Banknote[] banknotes1 = { twentyDollarBanknote };
    	Banknote[] banknotes2 = { twentyDollarBanknote, tenDollarBanknote };
    	
    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
    	//There is a 1 second delay between each banknote insertion.
    	scheduler.schedule(new PayWithBanknotesRunnable(this.stationHardware.banknoteInput, banknotes1), 1000, TimeUnit.MILLISECONDS);
    	scheduler.schedule(new PayWithBanknotesRunnable(this.stationHardware.banknoteInput, banknotes2), 2500, TimeUnit.MILLISECONDS);    	
    	
    	stationData.changeState(StationState.PAY_CASH);
    	TimeUnit.SECONDS.sleep(5);
    	
		assertTrue(stationData.getCurrentState() == StationState.NORMAL);		

    }
    
    @Test
    public void testPayingWithBanknotesNoChangeRemoveScannedItem() throws InterruptedException, OverloadException, EmptyException, DisabledException {
    	//Change will not be given out
    	//Weight DOES change during payment
    	//Must be corrected before software can continue
    	//Cleanup will be tested in here also
    	BigDecimal total = new BigDecimal("50");
    	//Add a milk jug to the scale
    	this.stationHardware.baggingArea.add(milkJugItem);
    	stationData.setTotalDue(total); //Add $50 to total cost
    	stationData.setTransactionPaymentAmount(total);

    	stationData.setExpectedWeight(4000);
    	
    	//Put 2 $20 bills in before removing milk jug
    	Banknote[] banknotes1 = { twentyDollarBanknote, twentyDollarBanknote };
    	//After putting the jug back on the scale, pay the rest
    	Banknote[] banknotes2 = { fiveDollarBanknote, fiveDollarBanknote };
    	
    	scheduler.schedule(new PayWithBanknotesRunnable(this.stationHardware.banknoteInput, banknotes1), 1500, TimeUnit.MILLISECONDS);
    	scheduler.schedule(new PayWithBanknotesRunnable(this.stationHardware.banknoteInput, banknotes2), 6500, TimeUnit.MILLISECONDS);
    	scheduler.schedule(new RemoveItemOnScaleRunnable(this.stationHardware.baggingArea, milkJugItem), 4500, TimeUnit.MILLISECONDS);
    	scheduler.schedule(new PlaceItemOnScaleRunnable(this.stationHardware.baggingArea, milkJugItem), 5500, TimeUnit.MILLISECONDS);
    	
    	stationData.changeState(StationState.PAY_CASH);
    	TimeUnit.SECONDS.sleep(5);
    	assertTrue(stationData.getCurrentState() == StationState.WEIGHT_ISSUE);
    	
    	TimeUnit.SECONDS.sleep(5);
    	
    	String finalReceipt = this.stationHardware.printer.removeReceipt();
		System.out.println("Receipt Generated:\n" + finalReceipt);
		
    	assertTrue(stationData.getCurrentState() == StationState.PRINT_RECEIPT_PROMPT);
    }
        
    @Test
    public void testPayingWithCashAddItemMidPayment() throws InterruptedException, OverloadException, EmptyException, DisabledException {
    	//Change will not be given out
    	//Weight does not change during payment
    	//Cleanup will be tested in here also
    	BigDecimal total = new BigDecimal("5");
    	stationData.setTotalDue(total); //Add $5 to total cost
    	stationData.setTransactionPaymentAmount(total);
    	//$4 in coins to pay before adding another item
    	Coin[] coins = { toonie, toonie};
    	//$5 in coins to pay remaining balance
    	Coin[] coins2 = { toonie, toonie, loonie }; 
    
    	//Schedule the list of coins to be inserted starting 1.5 seconds after starting payment.
    	//There is a 1 second delay between each coin insertion.
    	scheduler.schedule(new PayWithCoinsRunnable(this.stationHardware.coinSlot, coins), 1500, TimeUnit.MILLISECONDS);
    	
    	//Scan an item after 4.5 seconds
    	scheduler.schedule(new ScanItemRunnable(this.stationHardware.mainScanner, cornFlakesItem), 4500, TimeUnit.MILLISECONDS);
    	
    	//Put item on scale after 5.5 seconds    	
    	scheduler.schedule(new PlaceItemOnScaleRunnable(this.stationHardware.baggingArea, cornFlakesItem), 5500, TimeUnit.MILLISECONDS);
    	
    	//Pay remaining balance after 9.5 seconds
    	scheduler.schedule(new PayWithCoinsRunnable(this.stationHardware.coinSlot, coins2), 9500, TimeUnit.MILLISECONDS);
    	
    	
    	scheduler.schedule(new RemoveItemOnScaleRunnable(this.stationHardware.baggingArea, cornFlakesItem), 12500, TimeUnit.MILLISECONDS);
    	
    	stationData.changeState(StationState.PAY_CASH);
    	
    	TimeUnit.SECONDS.sleep(15);
    	
    	String finalReceipt = this.stationHardware.printer.removeReceipt();
		System.out.println("Receipt Generated:\n" + finalReceipt);
		
    	
    	assertTrue(stationData.getCurrentState() == StationState.WELCOME);	
    	
    }

  @Test
  public void testCheckoutPaymentWithBadDebitCardSwipe() throws InterruptedException, OverloadException, EmptyException, DisabledException {
  	
	BigDecimal total = new BigDecimal("50");
  	stationData.setTotalDue(total); //Add $50 to total cost
  	stationData.setTransactionPaymentAmount(total);
  	//Create a list of banknotes exceeding the total cost of all items
  	Banknote[] banknotes1 = { twentyDollarBanknote, twentyDollarBanknote, fiveDollarBanknote };
  	Coin[] coins = { quarter, toonie, toonie, toonie};
  	
  	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
  	//There is a 1 second delay between each banknote insertion.
  	scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "swipe", "Debit", "999", "Test Holder", "000", "1234", true, true), 100, TimeUnit.MILLISECONDS);
  	
  	stationData.changeState(StationState.PAY_DEBIT);
		
  	TimeUnit.SECONDS.sleep(1);
  	
	assertTrue(stationData.getCurrentState() == StationState.BAD_CARD);

  }
  
  @Test
  public void testCheckoutPaymentWithBadCreditCardSwipe() throws InterruptedException, OverloadException, EmptyException, DisabledException {
		BigDecimal total = new BigDecimal("50");
	  	stationData.setTotalDue(total); //Add $50 to total cost
	  	stationData.setTransactionPaymentAmount(total);
	  	//Create a list of banknotes exceeding the total cost of all items
	  	Banknote[] banknotes1 = { twentyDollarBanknote, twentyDollarBanknote, fiveDollarBanknote };
	  	Coin[] coins = { quarter, toonie, toonie, toonie};
	  	
	  	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
	  	//There is a 1 second delay between each banknote insertion.
	  	scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "swipe", "Credit", "999", "Test", "999", "999", true, true), 100, TimeUnit.MILLISECONDS);
	  	
	  	stationData.changeState(StationState.PAY_CREDIT);
			
	  	TimeUnit.SECONDS.sleep(1);
	  	
		assertTrue(stationData.getCurrentState() == StationState.BAD_CARD);
  }
  
  @Test
  public void testStartCheckoutFullPaymentWithDebtCardSwipe() throws InterruptedException, OverloadException, EmptyException, DisabledException {
		BigDecimal total = new BigDecimal("50");
	  	stationData.setTotalDue(total); //Add $50 to total cost
	  	stationData.setTransactionPaymentAmount(total);
	  	//Create a list of banknotes exceeding the total cost of all items
	  	Banknote[] banknotes1 = { twentyDollarBanknote, twentyDollarBanknote, fiveDollarBanknote };
	  	Coin[] coins = { quarter, toonie, toonie, toonie};
	  	
	  	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
	  	//There is a 1 second delay between each banknote insertion.
	  	scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "swipe", "Debit", "1", "Test Holder", "000", "1234", true, true), 100, TimeUnit.MILLISECONDS);
	  	
	  	stationData.changeState(StationState.PAY_DEBIT);
			
	  	TimeUnit.SECONDS.sleep(1);
	  	
		assertTrue(stationData.getCurrentState() == StationState.PRINT_RECEIPT_PROMPT);
  }
  
  @Test
  public void testStartCheckoutPartialPaymentsWithDebtCardSwipe() throws InterruptedException, OverloadException, EmptyException, DisabledException {
		BigDecimal total = new BigDecimal("50");
	  	stationData.setTotalDue(total); //Add $50 to total cost
	  	stationData.setTransactionPaymentAmount(new BigDecimal("25"));
	  	//Create a list of banknotes exceeding the total cost of all items
	  	Banknote[] banknotes1 = { twentyDollarBanknote, fiveDollarBanknote };
	  	
	  	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
	  	//There is a 1 second delay between each banknote insertion.
	  	scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "swipe", "Debit", "1", "Test Holder", "000", "1234", true, true), 100, TimeUnit.MILLISECONDS);
	  	
	  	stationData.changeState(StationState.PAY_DEBIT);
			
	  	TimeUnit.SECONDS.sleep(1);
	  	
		assertTrue(stationData.getCurrentState() == StationState.NORMAL);
  }
  
  @Test
  public void testStartCheckoutFullPaymentWithDebtCardInsert() throws InterruptedException, OverloadException, EmptyException, DisabledException {
		BigDecimal total = new BigDecimal("50");
	  	stationData.setTotalDue(total); //Add $50 to total cost
	  	stationData.setTransactionPaymentAmount(total);
	  	//Create a list of banknotes exceeding the total cost of all items
	  	Banknote[] banknotes1 = { twentyDollarBanknote, twentyDollarBanknote, fiveDollarBanknote };
	  	Coin[] coins = { quarter, toonie, toonie, toonie};
	  	
	  	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
	  	//There is a 1 second delay between each banknote insertion.
	  	scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "insert", "Debit", "1", "Test Holder", "000", "1234", true, true), 100, TimeUnit.MILLISECONDS);
	  	
	  	stationData.changeState(StationState.PAY_DEBIT);
			
	  	TimeUnit.SECONDS.sleep(1);
	  	
		assertTrue(stationData.getCurrentState() == StationState.PRINT_RECEIPT_PROMPT);
  }
  
  @Test
  public void testStartCheckoutPartialPaymentsWithDebtCardInsert() throws InterruptedException, OverloadException, EmptyException, DisabledException {
	  BigDecimal total = new BigDecimal("50");
	  	stationData.setTotalDue(total); //Add $50 to total cost
	  	stationData.setTransactionPaymentAmount(new BigDecimal("25"));
	  	//Create a list of banknotes exceeding the total cost of all items
	  	Banknote[] banknotes1 = { twentyDollarBanknote, fiveDollarBanknote };
	  	
	  	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
	  	//There is a 1 second delay between each banknote insertion.
	  	scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "insert", "Debit", "1", "Test Holder", "000", "1234", true, true), 100, TimeUnit.MILLISECONDS);
	  	
	  	stationData.changeState(StationState.PAY_DEBIT);
			
	  	TimeUnit.SECONDS.sleep(1);
	  	
		assertTrue(stationData.getCurrentState() == StationState.NORMAL);
	}
  
  @Test
  public void testStartCheckoutFullPaymentWithDebtCardTap() throws InterruptedException, OverloadException, EmptyException, DisabledException {
	  BigDecimal total = new BigDecimal("50");
	  	stationData.setTotalDue(total); //Add $50 to total cost
	  	stationData.setTransactionPaymentAmount(total);
	  	//Create a list of banknotes exceeding the total cost of all items
	  	Banknote[] banknotes1 = { twentyDollarBanknote, fiveDollarBanknote };
	  	
	  	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
	  	//There is a 1 second delay between each banknote insertion.
	  	scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "tap", "Debit", "1", "Test Holder", "000", "1234", true, true), 100, TimeUnit.MILLISECONDS);
	  	
	  	stationData.changeState(StationState.PAY_DEBIT);
			
	  	TimeUnit.SECONDS.sleep(1);
	  	
		assertTrue(stationData.getCurrentState() == StationState.PRINT_RECEIPT_PROMPT);  
	}
  
  @Test
  public void testStartCheckoutPartialPaymentsWithDebtCardTap() throws InterruptedException, OverloadException, EmptyException, DisabledException {
  	
	  BigDecimal total = new BigDecimal("50");
	  	stationData.setTotalDue(total); //Add $50 to total cost
	  	stationData.setTransactionPaymentAmount(new BigDecimal("25"));
	  	//Create a list of banknotes exceeding the total cost of all items
	  	Banknote[] banknotes1 = { twentyDollarBanknote, fiveDollarBanknote };
	  	
	  	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
	  	//There is a 1 second delay between each banknote insertion.
	  	scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "tap", "Debit", "1", "Test Holder", "000", "1234", true, true), 100, TimeUnit.MILLISECONDS);
	  	
	  	stationData.changeState(StationState.PAY_DEBIT);
			
	  	TimeUnit.SECONDS.sleep(1);
	  	
		assertTrue(stationData.getCurrentState() == StationState.NORMAL);
	}
  
  @Test
  public void testStartCheckoutFullPaymentWithCreditCardSwipe() throws InterruptedException, OverloadException, EmptyException, DisabledException {	
	BigDecimal total = new BigDecimal("50");
	stationData.setTotalDue(total); //Add $50 to total cost
	stationData.setTransactionPaymentAmount(total);
	//Create a list of banknotes exceeding the total cost of all items
	Banknote[] banknotes1 = { twentyDollarBanknote, fiveDollarBanknote };
	
	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
	//There is a 1 second delay between each banknote insertion.
	scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "swipe", "Credit", "1", "Test Holder", "000", "1234", true, true), 100, TimeUnit.MILLISECONDS);
	
	stationData.changeState(StationState.PAY_CREDIT);
		
	TimeUnit.SECONDS.sleep(1);
	
	assertTrue(stationData.getCurrentState() == StationState.PRINT_RECEIPT_PROMPT);
  }
  
  @Test
  public void testStartCheckoutPartialPaymentsWithCreditCardSwipe() throws InterruptedException, OverloadException, EmptyException, DisabledException {
	  	BigDecimal total = new BigDecimal("50");
		stationData.setTotalDue(total); //Add $50 to total cost
		stationData.setTransactionPaymentAmount(new BigDecimal("25"));
		//Create a list of banknotes exceeding the total cost of all items
		Banknote[] banknotes1 = { twentyDollarBanknote, fiveDollarBanknote };
		
		//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
		//There is a 1 second delay between each banknote insertion.
		scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "swipe", "Credit", "1", "Test Holder", "000", "1234", true, true), 100, TimeUnit.MILLISECONDS);
		
		stationData.changeState(StationState.PAY_CREDIT);
			
		TimeUnit.SECONDS.sleep(1);
		
		assertTrue(stationData.getCurrentState() == StationState.NORMAL);
  	
  }
  
  @Test
  public void testStartCheckoutFullPaymentWithCreditCardInsert() throws InterruptedException, OverloadException, EmptyException, DisabledException {
	  	BigDecimal total = new BigDecimal("50");
		stationData.setTotalDue(total); //Add $50 to total cost
		stationData.setTransactionPaymentAmount(total);
		//Create a list of banknotes exceeding the total cost of all items
		Banknote[] banknotes1 = { twentyDollarBanknote, fiveDollarBanknote };
		
		//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
		//There is a 1 second delay between each banknote insertion.
		scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "insert", "Credit", "1", "Test Holder", "000", "1234", true, true), 100, TimeUnit.MILLISECONDS);
		
		stationData.changeState(StationState.PAY_CREDIT);
			
		TimeUnit.SECONDS.sleep(1);
		
		assertTrue(stationData.getCurrentState() == StationState.PRINT_RECEIPT_PROMPT);
  }
  
  @Test
  public void testStartCheckoutPartialPaymentsWithCreditCardInsert() throws InterruptedException, OverloadException, EmptyException, DisabledException {
	  	BigDecimal total = new BigDecimal("50");
		stationData.setTotalDue(total); //Add $50 to total cost
		stationData.setTransactionPaymentAmount(new BigDecimal("25"));
		//Create a list of banknotes exceeding the total cost of all items
		Banknote[] banknotes1 = { twentyDollarBanknote, fiveDollarBanknote };
		
		//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
		//There is a 1 second delay between each banknote insertion.
		scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "insert", "Credit", "1", "Test Holder", "000", "1234", true, true), 100, TimeUnit.MILLISECONDS);
		
		stationData.changeState(StationState.PAY_CREDIT);
			
		TimeUnit.SECONDS.sleep(1);
		
		assertTrue(stationData.getCurrentState() == StationState.NORMAL);
  }
  
  @Test
  public void testStartCheckoutFullPaymentWithCreditCardTap() throws InterruptedException, OverloadException, EmptyException, DisabledException {
	  	BigDecimal total = new BigDecimal("50");
		stationData.setTotalDue(total); //Add $50 to total cost
		stationData.setTransactionPaymentAmount(total);
		//Create a list of banknotes exceeding the total cost of all items
		Banknote[] banknotes1 = { twentyDollarBanknote, fiveDollarBanknote };
		
		//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
		//There is a 1 second delay between each banknote insertion.
		scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "tap", "Credit", "1", "Test Holder", "000", "1234", true, true), 100, TimeUnit.MILLISECONDS);
		
		stationData.changeState(StationState.PAY_CREDIT);
			
		TimeUnit.SECONDS.sleep(1);
		
		assertTrue(stationData.getCurrentState() == StationState.PRINT_RECEIPT_PROMPT);
  }
  
  @Test
  public void testStartCheckoutPartialPaymentsWithCreditCardTap() throws InterruptedException, OverloadException, EmptyException, DisabledException {
	  	BigDecimal total = new BigDecimal("50");
		stationData.setTotalDue(total); //Add $50 to total cost
		stationData.setTransactionPaymentAmount(new BigDecimal("25"));
		//Create a list of banknotes exceeding the total cost of all items
		Banknote[] banknotes1 = { twentyDollarBanknote, fiveDollarBanknote };
		
		//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
		//There is a 1 second delay between each banknote insertion.
		scheduler.schedule(new TestCardRunnable(this.stationHardware.cardReader, "tap", "Credit", "1", "Test Holder", "000", "1234", true, true), 100, TimeUnit.MILLISECONDS);
		
		stationData.changeState(StationState.PAY_CREDIT);
			
		TimeUnit.SECONDS.sleep(1);
		
		assertTrue(stationData.getCurrentState() == StationState.NORMAL);
  }
    
  @Test
  public void testResetAfterTakingItems() throws InterruptedException, OverloadException, EmptyException, DisabledException {
  	
	  	this.stationHardware.baggingArea.add(milkJugItem);
	  	this.stationHardware.baggingArea.add(cornFlakesItem);
    	
	  
	  	scheduler.schedule(new RemoveItemOnScaleRunnable(this.stationHardware.baggingArea, milkJugItem), 1500, TimeUnit.MILLISECONDS);
	  	scheduler.schedule(new RemoveItemOnScaleRunnable(this.stationHardware.baggingArea, cornFlakesItem), 2500, TimeUnit.MILLISECONDS);
	
		stationData.changeState(StationState.PRINT_RECEIPT_PROMPT);
		TimeUnit.SECONDS.sleep(5);
	
	
		String finalReceipt = this.stationHardware.printer.removeReceipt();
		System.out.println("Receipt Generated:\n" + finalReceipt);
			
	  	assertTrue(stationData.getCurrentState() == StationState.WELCOME);
	  	
	  }
  
  
	@After
    public void reset()
    {
    	System.setIn(backupInputStream);    	
    	banknoteChangeValue = 0;
    	stationData.resetCheckoutTotals();
    	stationData.resetTotalPaidThisTransaction();
    	this.touchScreenSoftware = new TouchScreenSoftware(customInputStream, this.stationUnit.getTouchScreen(), stationData);
    	scheduler.shutdownNow();

    }
}

