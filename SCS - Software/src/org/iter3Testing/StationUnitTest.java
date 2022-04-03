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
import org.controlSoftware.deviceHandlers.membership.MembershipCardScannerHandler;
import org.controlSoftware.deviceHandlers.payment.CashPaymentHandler;
import org.controlSoftware.deviceHandlers.payment.PayWithCreditCard;
import org.controlSoftware.deviceHandlers.payment.PayWithDebitCard;
import org.controlSoftware.general.TouchScreenSoftware;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutSoftware;
import org.driver.SelfCheckoutStationUnit;
import org.driver.databases.TestProducts;
import org.iter2Testing.RemoveItemOnScaleRunnable;
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
public class StationUnitTest {
	
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
	
	private static Coin nickel = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal(0.05));
	private static Coin quarter = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal(0.25));
	private static Coin loonie = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal(1.00));
	private static Coin toonie = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal(2.00));
	private static Coin quarterUSD = new Coin(Currency.getInstance("USD"), new BigDecimal(0.25));
	private static Coin invalidCoin = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal(0.75));
	
	public static int banknoteChangeValue = 0; //Updated by DanglingBanknoteRemover runnable 
	
	private BarcodedProduct milkJug;
	private BarcodedProduct orangeJuice;
	private BarcodedProduct cornFlakes;
	private BarcodedItem milkJugItem;
	private BarcodedItem orangeJuiceItem;
	private BarcodedItem cornFlakesItem;
	
	private ReceiptHandler receiptHandler;
	private MembershipCardScannerHandler customMembershipScannerObserver;
	
	private SelfCheckoutStationUnit stationUnit;
	private SelfCheckoutStation stationHardware;
	private SelfCheckoutData stationData;
	private SelfCheckoutSoftware stationSoftware;
	private TouchScreenSoftware touchScreenSoftware;
	private TestProducts testProducts;
	
	private ScheduledExecutorService scheduler;
	
	

	//Initialize
	@Before
	public void setup() {
		
		
		
		this.stationUnit = new SelfCheckoutStationUnit();
		
		this.stationHardware = stationUnit.getSelfCheckoutStationHardware();
		this.stationData = stationUnit.getSelfCheckoutData();
		this.stationSoftware = stationUnit.getSelfCheckoutSoftware();
		this.touchScreenSoftware = stationUnit.getTouchScreenSoftware();
		
		this.testProducts = new TestProducts();
		
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
    public void verifyExpectedWeightWhenStartingCheckout() throws InterruptedException, OverloadException, EmptyException, DisabledException {
    	//Setup simulated input
    	//User will select 0 bags
    	//Choose to swipe their membership card
    	//They will pay in full ($0)
    	//They will pay with cash
    	String inputString = "0\n" + "skip\n" + "full\n" + "cash\n";
    	
    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
    	TouchScreenSoftware tss = new TouchScreenSoftware(customInputStream, this.stationUnit.getTouchScreen(), stationData);
    	stationSoftware.updateTouchScreenSoftware(tss);
    	this.touchScreenSoftware = tss;
    	
    	// start checkout 
    	this.stationHardware.baggingArea.add(milkJugItem); //Weighs 4000 grams
    	this.scheduler.schedule(new RemoveItemOnScaleRunnable(this.stationHardware.baggingArea, milkJugItem), 2500, TimeUnit.MILLISECONDS);
        stationSoftware.getCheckoutHandler().startCheckout();
        // verify device is disabled or not
        assertTrue(Math.floor(stationData.getExpectedWeightCheckout()) == 4000.0);
    }
    
//    @Test
//    public void verifyWeightIssueDetectedWhenStartingCheckout() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to swipe their membership card
//    	//They will pay in full ($0)
//    	//They will pay with cash
//    	String inputString = "0\n" + "skip\n" + "full\n" + "cash\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreen ts = new TouchScreen(customInputStream);
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	//Put a milk jug on the scale 1.5 seconds after starting checkout
//    	scheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, milkJug), 500, TimeUnit.MILLISECONDS);
//    	//Remove the milk jug 5 seconds after starting checkout
//    	scheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.baggingArea, milkJug), 5000, TimeUnit.MILLISECONDS);
//        checkout.startCheckout();
//        
//        //Touch screen should have been informed of the item being added during checkout
//        assertTrue(touchScreen.invalidWeightInCheckoutDetected.get());
//        //Touch screen should have been informed of the item being removed during checkout
//    	assertTrue(touchScreen.invalidWeightInCheckoutCorrected.get());
//    }
//    
    
//    @Test
//    public void testScanningMembershipCard() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//
//    	//Schedule the membership card to be swiped 2.5 seconds after starting checkout
//    	scheduler.schedule(new ScanTestMembershipCardRunnable(this.Station.cardReader, "Membership"), 10, TimeUnit.MILLISECONDS);
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to swipe their membership card
//    	//They will pay in full ($0)
//    	//They will pay with cash
//    	String inputString = "0\n" + "swipe\n" + "full\n" + "cash\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream);
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	checkout.startCheckout();
//    	
//    	String finalReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + finalReceipt);
//    	
//		assertTrue(checkout.getMembershipNumber().equals("123456789"));
//		assertTrue(ReceiptHandler.getMembershipID().equals("123456789\n"));
//    	
//    }
//
//    @Test
//    public void testScanningWrongCardAsMembershipCard() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//
//    	//Schedule the membership card to be swiped 2.5 seconds after starting checkout
//    	scheduler.schedule(new ScanTestMembershipCardRunnable(this.Station.cardReader, "Credit"), 10, TimeUnit.MILLISECONDS);
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to swipe their membership card
//    	//They will pay in full ($0)
//    	//They will pay with cash
//    	String inputString = "0\n" + "swipe\n" + "full\n" + "cash\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream);
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	checkout.startCheckout();
//    	
//    	String finalReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + finalReceipt);
//    	
//		assertTrue(checkout.getMembershipNumber().equals("null"));
//		assertTrue(ReceiptHandler.getMembershipID().equals("null\n"));
//    }
//
//    
//    @Test
//    public void testManualInputMembershipCard() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to input their membership card manually
//    	//ID = 123456789
//    	//They will pay in full ($0)
//    	//They will pay with cash
//    	String inputString = "0\n" + "manual\n" + "123456789\n" + "full\n" + "cash\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	checkout.startCheckout();
//    	
//    	String finalReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + finalReceipt);
//    	
//		assertTrue(checkout.getMembershipNumber().equals("123456789"));
//		assertTrue(ReceiptHandler.getMembershipID().equals("123456789\n"));
//    }
//    
//    @Test
//    public void testCoinChangeDispensed() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	//Change will be given out
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay in full ($50)
//    	//They will pay with cash ($51.25)
//    	//Should get $1.25 back in Coin tray
//    	String inputString = "0\n" + "skip\n" + "full\n" + "cash\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	BigDecimal total = new BigDecimal("50");
//    	CheckoutHandler.setTotalCost(total); //Add $50 to total cost
//    	//Create a list of banknotes exceeding the total cost of all items
//    	Banknote[] banknotes1 = { twentyDollarBanknote, twentyDollarBanknote, fiveDollarBanknote };
//    	Coin[] coins = { quarter, toonie, toonie, toonie};
//    	
//    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
//    	//There is a 1 second delay between each banknote insertion.
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes1), 100, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins), 3200, TimeUnit.MILLISECONDS);
//    	
//    	checkout.startCheckout();
//
//		String finalReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + finalReceipt);
//		
//		//Get Change from tray
//		List<Coin> change = this.Station.coinTray.collectCoins();
//		BigDecimal changeValue = BigDecimal.ZERO;
//		
//		for (Coin c : change) { if (!(c == null)) { changeValue = changeValue.add(c.getValue()); } }
//    	
//		//Touch screen should have been informed of change being dispensed
//    	assertTrue(touchScreen.changeDispensed.get());
//    	assertTrue(banknoteChangeValue == 0);
//    	assertTrue(changeValue.equals(new BigDecimal("1.25")));
//    }
//
//
//    @Test
//    public void testBanknoteChangeDispensed() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	//Change will be given out
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay in full ($55)
//    	//They will pay with cash ($51.25)
//    	//Should get $1.25 back in Coin tray
//    	String inputString = "0\n" + "skip\n" + "full\n" + "cash\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	BigDecimal total = new BigDecimal("55");
//    	CheckoutHandler.setTotalCost(total); //Add $50 to total cost
//    	//Create a list of banknotes exceeding the total cost of all items
//    	Banknote[] banknotes1 = { fiftyDollarBanknote, twentyDollarBanknote };
//    	
//    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
//    	//There is a 1 second delay between each banknote insertion.
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes1), 1000, TimeUnit.MILLISECONDS);
//    	
//    	
//    	//Will be expecting $15 in change via 1x $10 note, and 1x $5 note
//    	scheduler.schedule(new RemoveDanglingBanknotesRunnable(this.Station.banknoteOutput, 2), 3000, TimeUnit.MILLISECONDS);
//    	
//    	checkout.startCheckout();
//
//		String finalReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + finalReceipt);
//		
//		BigDecimal changeValue = BigDecimal.ZERO;
//		//Get Change from tray
//		List<Coin> change = this.Station.coinTray.collectCoins();
//		
//		for (Coin c : change) { if (!(c == null)) { changeValue = changeValue.add(c.getValue()); } }
//		
//		//Touch screen should have been informed of change being dispensed
//    	assertTrue(touchScreen.changeDispensed.get());
//    	assertTrue(banknoteChangeValue == 15);
//    	assertTrue(changeValue.equals(new BigDecimal("0")));
//    }
//    
//    @Test
//    public void testBanknoteAndCoinChangeDispensed() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	//Change will be given out
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay in full
//    	String inputString = "0\n" + "skip\n" + "full\n" + "cash\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	BigDecimal total = new BigDecimal("55");
//    	CheckoutHandler.setTotalCost(total); //Add $55 to total cost
//    	
//    	Banknote[] banknotes1 = { fiftyDollarBanknote }; 
//    	Coin[] coins = { quarter, quarter, quarter, loonie, toonie};
//    	Banknote[] banknotes2 = { tenDollarBanknote };
//    	//Totals $63.75 - Should return $8.75 in change
//    	//Should be given: 1x $5, 1x $2, 1x $1, 3x $0.25 back 
//    	
//    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
//    	//There is a 1 second delay between each banknote insertion.
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes1), 1000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins), 2000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes2), 7000, TimeUnit.MILLISECONDS);
//    	
//    	//Will be expecting $15 in change via 1x $10 note, and 1x $5 note
//    	scheduler.schedule(new RemoveDanglingBanknotesRunnable(this.Station.banknoteOutput, 1), 10000, TimeUnit.MILLISECONDS);
//    	
//    	checkout.startCheckout();
//
//		String finalReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + finalReceipt);
//		
//		BigDecimal changeValue = BigDecimal.ZERO;
//		//Get Change from tray
//		List<Coin> change = this.Station.coinTray.collectCoins();
//		
//		for (Coin c : change) { if (!(c == null)) { changeValue = changeValue.add(c.getValue()); } }
//		
//		//Touch screen should have been informed of change being dispensed
//    	assertTrue(touchScreen.changeDispensed.get());
//    	assertTrue(banknoteChangeValue == 5);
//    	assertTrue(changeValue.equals(new BigDecimal("3.75")));
//    }
// 
//    @Test
//    public void testChangeDispensedWithEmptyDispensers() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	//Change will be given out
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay in full ($55)
//    	//They will pay with cash ($51.25)
//    	//Should get $1.25 back in Coin tray
//    	String inputString = "0\n" + "skip\n" + "full\n" + "cash\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	BigDecimal total = new BigDecimal("55");
//    	CheckoutHandler.setTotalCost(total); //Add $55 to total cost
//    	
//    	//Remove all $5 bills, and all toonies, shold be given back the coins we put in 
//    	// plus $5 in loonies
//    	this.Station.banknoteDispensers.get(5).unload();
//    	this.Station.coinDispensers.get(new BigDecimal(2.00)).unload();
//    	
//    	Banknote[] banknotes1 = { fiftyDollarBanknote }; 
//    	Coin[] coins = { quarter, quarter, quarter, loonie, toonie};
//    	Banknote[] banknotes2 = { tenDollarBanknote };
//    	//Totals $63.75 - Should return $8.75 in change
//    	//Should be given: 1x $5, 1x $2, 1x $1, 3x $0.25 back 
//    	
//    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
//    	//There is a 1 second delay between each banknote insertion.
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes1), 1000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins), 2000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes2), 7000, TimeUnit.MILLISECONDS);
//    	
//    	//Will be expecting $15 in change via 1x $10 note, and 1x $5 note
//    	scheduler.schedule(new RemoveDanglingBanknotesRunnable(this.Station.banknoteOutput, 1), 8500, TimeUnit.MILLISECONDS);
//    	
//    	checkout.startCheckout();
//
//		String finalReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + finalReceipt);
//		
//		BigDecimal changeValue = BigDecimal.ZERO;
//		//Get Change from tray
//		List<Coin> change = this.Station.coinTray.collectCoins();
//		
//		for (Coin c : change) { if (!(c == null)) { changeValue = changeValue.add(c.getValue()); } }
//		
//		//Touch screen should have been informed of change being dispensed
//    	assertTrue(touchScreen.changeDispensed.get());
//    	assertTrue(banknoteChangeValue == 0);
//    	assertTrue(changeValue.equals(new BigDecimal("8.75")));
//    }
//    
//    @Test
//    public void testStartCheckoutPaymentWithBadDebitCardSwipe() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	
//    	Card testDebitCard = new Card("Debit", "9999999999", "Card Holder", "888", "4321", true, true);
//		BankClientInfo bankClientDebitInfo = new BankClientInfo("Debit", "0987654321", "Test Card Holder", "999", "4321", true, true, new BigDecimal("200"), new BigDecimal("2500"));
//		PayWithDebitCard payWithDebitTest = new PayWithDebitCard(this.Station, testDebitCard, null, "4321", bankClientDebitInfo);
//		
//		this.checkout = new CheckoutHandler(this.touchScreen, 
//									 this.Station.mainScanner, 
//									 this.Station.banknoteInput, //Checkout can disable banknote slot
//									 this.Station.coinSlot,      //Checkout can disable coin slot
//									 this.Station.baggingArea,
//									 this.Station,
//									 this.receiptHandler,
//									 payWithDebitTest,
//									 null,
//									 this.Station.cardReader);
//    	
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "full\n" + "debit\n" + "swipe\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	checkout.startCheckout();
//
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.returnedToAddingItemMode.get());
//    }
//    
//    @Test
//    public void testStartCheckoutPaymentWithBadCreditCardSwipe() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	
//    	Card testCreditCard = new Card("Credit", "9999999999", "Card Holder", "888", "4321", true, true);
//		BankClientInfo bankClientCreditInfo = new BankClientInfo("Credit", "0987654321", "Test Card Holder", "999", "4321", true, true, new BigDecimal("200"), new BigDecimal("2500"));
//		PayWithCreditCard payWithCreditTest = new PayWithCreditCard(this.Station, testCreditCard, null, "4321", bankClientCreditInfo);
//		
//		this.checkout = new CheckoutHandler(this.touchScreen, 
//									 this.Station.mainScanner, 
//									 this.Station.banknoteInput, //Checkout can disable banknote slot
//									 this.Station.coinSlot,      //Checkout can disable coin slot
//									 this.Station.baggingArea,
//									 this.Station,
//									 this.receiptHandler,
//									 null,
//									 payWithCreditTest,
//									 this.Station.cardReader);
//    	
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "full\n" + "credit\n" + "swipe\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	checkout.startCheckout();
//
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.returnedToAddingItemMode.get());
//    }
//    
//    @Test
//    public void testStartCheckoutFullPaymentWithDebtCardSwipe() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "full\n" + "debit\n" + "swipe\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	checkout.startCheckout();
//
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//    
//    @Test
//    public void testStartCheckoutPartialPaymentsWithDebtCardSwipe() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "partial\n" + "50\n" + "debit\n" + "swipe\n" + "full\n" + "debit\n" + "swipe\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	checkout.startCheckout();
//    	
//    	assertTrue(touchScreen.returnedToAddingItemMode.get());
//    	
//    	checkout.startCheckout();
//    	
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//    
//    @Test
//    public void testStartCheckoutFullPaymentWithDebtCardInsert() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "full\n" + "debit\n" + "insert\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	checkout.startCheckout();
//
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//    
//    @Test
//    public void testStartCheckoutPartialPaymentsWithDebtCardInsert() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "partial\n" + "50\n" + "debit\n" + "insert\n" + "full\n" + "debit\n" + "insert\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	checkout.startCheckout();
//    	
//    	assertTrue(touchScreen.returnedToAddingItemMode.get());
//    	
//    	checkout.startCheckout();
//    	
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//    
//    @Test
//    public void testStartCheckoutFullPaymentWithDebtCardTap() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "full\n" + "debit\n" + "tap\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	checkout.startCheckout();
//
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//    
//    @Test
//    public void testStartCheckoutPartialPaymentsWithDebtCardTap() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "partial\n" + "50\n" + "debit\n" + "tap\n" + "full\n" + "debit\n" + "tap\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	checkout.startCheckout();
//    	
//    	assertTrue(touchScreen.returnedToAddingItemMode.get());
//    	
//    	checkout.startCheckout();
//    	
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//    
//    @Test
//    public void testStartCheckoutFullPaymentWithCreditCardSwipe() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "full\n" + "credit\n" + "swipe\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	checkout.startCheckout();
//
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//    
//    @Test
//    public void testStartCheckoutPartialPaymentsWithCreditCardSwipe() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "partial\n" + "50\n" + "credit\n" + "swipe\n" + "full\n" + "credit\n" + "swipe\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	checkout.startCheckout();
//    	
//    	assertTrue(touchScreen.returnedToAddingItemMode.get());
//    	
//    	checkout.startCheckout();
//    	
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//    
//    @Test
//    public void testStartCheckoutFullPaymentWithCreditCardInsert() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "full\n" + "credit\n" + "insert\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	checkout.startCheckout();
//
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//    
//    @Test
//    public void testStartCheckoutPartialPaymentsWithCreditCardInsert() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "partial\n" + "50\n" + "credit\n" + "insert\n" + "full\n" + "credit\n" + "insert\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	checkout.startCheckout();
//    	
//    	assertTrue(touchScreen.returnedToAddingItemMode.get());
//    	
//    	checkout.startCheckout();
//    	
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//    
//    @Test
//    public void testStartCheckoutFullPaymentWithCreditCardTap() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "full\n" + "credit\n" + "tap\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	checkout.startCheckout();
//
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//    
//    @Test
//    public void testStartCheckoutPartialPaymentsWithCreditCardTap() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "partial\n" + "50\n" + "credit\n" + "tap\n" + "full\n" + "credit\n" + "tap\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	checkout.startCheckout();
//    	
//    	assertTrue(touchScreen.returnedToAddingItemMode.get());
//    	
//    	checkout.startCheckout();
//    	
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//    
//    @Test
//    public void testPayingWithCashAddItemNoScan() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay in full ($50)
//    	//They will pay with cash ($51.25)
//    	//Should get $1.25 back in Coin tray
//    	String inputString = "0\n" + "skip\n" + "full\n" + "cash\n";
//
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	//Cleanup will be tested in here also
//    	BigDecimal total = new BigDecimal("50");
//    	CheckoutHandler.setTotalCost(total); //Add $50 to total cost
//    	//Bypass startCheckout method
//    	checkout.setInCheckout(true);
//    	//Create a list of banknotes exceeding the total cost of all items
//    	Banknote[] banknotes1 = { twentyDollarBanknote };
//    	Banknote[] banknotes2 = { twentyDollarBanknote, fiveDollarBanknote };
//    	Coin[] coins = { loonie, toonie, toonie };
//    	
//    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
//    	//There is a 1 second delay between each banknote insertion.
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes1), 1000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, milkJug), 2000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.baggingArea, milkJug), 3500, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes2), 4000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins), 6500, TimeUnit.MILLISECONDS);
//    	
//    	checkout.startCheckout();
//
//		String finalReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + finalReceipt);
//    	
//		//Touch screen should have been informed of the weight issue, and its correction
//    	assertTrue(touchScreen.invalidWeightInCheckoutDetected.get());
//    	assertTrue(touchScreen.invalidWeightInCheckoutCorrected.get());
//		
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	//Touch screen should have reset to the welcome screen
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//    
//    @Test
//    public void testStartCheckoutTwoPartialPaymentsWithCash() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "partial\n" + "50\n" + "cash\n" + "partial\n" + "50\n" + "cash\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	//Cleanup will be tested in here also
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	//Create a list of banknotes exceeding the total cost of all items
//    	Banknote[] banknotes1 = { twentyDollarBanknote };
//    	Banknote[] banknotes2 = { twentyDollarBanknote, fiveDollarBanknote };
//    	Coin[] coins = { quarter, toonie, toonie, toonie};
//    	
//    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
//    	//There is a 1 second delay between each banknote insertion.
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes1), 1000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes2), 3000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins), 6000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes1), 9000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes2), 11000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins), 14000, TimeUnit.MILLISECONDS);
//    	
//    	checkout.startCheckout();
//
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//		
//    	
//    	checkout.startCheckout();
//
//		partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//		
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//    
//    @Test
//    public void testStartCheckoutTwoPartialPaymentsWithCashAddItemInbetween() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) each time
//
//    	String inputString = "0\n" + "skip\n" + "partial\n" + "50\n" + "cash\n" + "full\n" + "cash\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	
//    	//Change will be given out
//    	//Weight does not change during payment
//    	//Cleanup will be tested in here also
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	//Create a list of banknotes exceeding the total cost of all items
//    	Banknote[] banknotes1 = { twentyDollarBanknote };
//    	Banknote[] banknotes2 = { twentyDollarBanknote, fiveDollarBanknote };
//    	Banknote[] banknotes3 = { fiftyDollarBanknote, fiveDollarBanknote };
//    	Coin[] coins = { quarter, toonie, toonie, toonie};
//    	
//    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
//    	//There is a 1 second delay between each banknote insertion.
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes1), 1000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes2), 3000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins), 6000, TimeUnit.MILLISECONDS);
//
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes3), 15000, TimeUnit.MILLISECONDS);
//    	
//    	checkout.startCheckout();
//		
//    	assertTrue(ReceiptHandler.getFinalTotal().equals("$100\n"));
//    	
//		//Scan in a milkJug and then schedule it to be put down
//    	scheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, milkJug), 500, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.baggingArea, milkJug), 1000, TimeUnit.MILLISECONDS);
//		this.Station.handheldScanner.scan(milkJug);
//
//    	checkout.startCheckout();
//    	
//    	assertTrue(ReceiptHandler.getFinalTotal().equals("$104.89\n"));
//    	
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//		
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.resetSuccessful.get());
//    }
//
//    
//    @Test
//    public void testStartCheckoutPartialPaymentWithCash() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	
//    	//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to skip membership card 
//    	//They will pay partial ($50) once
//
//    	String inputString = "0\n" + "skip\n" + "partial\n" + "50\n" + "cash\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//    	
//    	//Create a list of banknotes exceeding the total cost of all items
//    	Banknote[] banknotes1 = { twentyDollarBanknote };
//    	Banknote[] banknotes2 = { twentyDollarBanknote, tenDollarBanknote };
//    	
//    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
//    	//There is a 1 second delay between each banknote insertion.
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes1), 5000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes2), 6750, TimeUnit.MILLISECONDS);    	
//    	
//    	checkout.startCheckout();
//
//		String partialReceipt = this.Station.printer.removeReceipt();
//		System.out.println("Receipt Generated:\n" + partialReceipt);
//		
//    	//Should no longer be in checkout mode
//    	assertFalse(checkout.isInCheckout());
//    	assertTrue(touchScreen.returnedToAddingItemMode.get());
//    }
//    
//    @Test
//    public void testPartialPaymentWithCash() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//
//    	//Change will be given out
//    	//Weight does not change during payment
//    	//Cleanup will be tested in here also
//    	BigDecimal total = new BigDecimal("100");
//    	CheckoutHandler.setTotalCost(total); //Add $100 to total cost
//
//    	//Create a list of banknotes exceeding the total cost of all items
//    	Banknote[] banknotes1 = { twentyDollarBanknote };
//    	Banknote[] banknotes2 = { twentyDollarBanknote, fiveDollarBanknote };
//    	Coin[] coins = { loonie, toonie, toonie};
//    	
//    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
//    	//There is a 1 second delay between each banknote insertion.
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes1), 1000, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes2), 2500, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins), 5500, TimeUnit.MILLISECONDS);
//    	
//    	checkout.payWithCash(total.divide(new BigDecimal("2"))); //Pay $50
//
//		
//    	assertTrue(checkout.getTotalMoneyPaid().equals(new BigDecimal("50")));
//    }
//
//    
//    @Test
//    public void testPayingWithBanknotesNoChangeRemoveScannedItem() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	//Change will not be given out
//    	//Weight DOES change during payment
//    	//Must be corrected before software can continue
//    	//Cleanup will be tested in here also
//    	BigDecimal total = new BigDecimal("50");
//    	//Add a milk jug to the scale
//    	this.Station.baggingArea.add(milkJug);
//    	CheckoutHandler.setTotalCost(total); //Add $50 to total cost
//
//    	//Bypass startCheckout method
//    	checkout.setInCheckout(true);
//    	checkout.setExpectedWeight(4000);
//    	
//    	//Put 2 $20 bills in before removing milk jug
//    	Banknote[] banknotes1 = { twentyDollarBanknote, twentyDollarBanknote };
//    	//After putting the jug back on the scale, pay the rest
//    	Banknote[] banknotes2 = { fiveDollarBanknote, fiveDollarBanknote };
//    	
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes1), 1500, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes2), 6500, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.baggingArea, milkJug), 4500, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, milkJug), 5500, TimeUnit.MILLISECONDS);
//    	
//    	checkout.payWithCash(total);
//
//    	//Touch screen should have been informed of the weight issue, and its correction
//    	assertTrue(touchScreen.invalidWeightInCheckoutDetected.get());
//    	assertTrue(touchScreen.invalidWeightInCheckoutCorrected.get());
//    }
//    
//    @Test
//    public void testPayingWithCoinsInvalidWeight() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	//Change will not be given out
//    	//Weight DOES change during payment
//    	//Must be corrected before software can continue
//    	//Cleanup will be tested in here also
//    	BigDecimal total = new BigDecimal("6.75");
//    	this.Station.baggingArea.add(milkJug);
//    	CheckoutHandler.setTotalCost(total); //Add $50 to total cost
//    	//Bypass startCheckout method
//    	checkout.setInCheckout(true);
//    	checkout.setExpectedWeight(4000);
//    	//Put 2 toonies in before removing milk jug
//    	Coin[] coins1 = { toonie, toonie };
//    	//After putting the jug back on the scale, pay the rest
//    	Coin[] coins2 = { toonie, quarter, quarter, quarter };
//    	
//    	scheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins1), 1500, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins2), 6500, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.baggingArea, milkJug), 4500, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, milkJug), 5500, TimeUnit.MILLISECONDS);
//    	scheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.baggingArea, milkJug), 12500, TimeUnit.MILLISECONDS);
//    	
//    	checkout.payWithCash(total);
//    	
//
//    	//Touch screen should have been informed of the weight issue, and its correction
//    	assertTrue(touchScreen.invalidWeightInCheckoutDetected.get());
//    	assertTrue(touchScreen.invalidWeightInCheckoutCorrected.get());
//    }    
//    
//    @Test
//    public void testPayingWithCashAddItemMidPayment() throws InterruptedException, OverloadException, EmptyException, DisabledException {
//    	//Change will not be given out
//    	//Weight does not change during payment
//    	//Cleanup will be tested in here also
//    	BigDecimal total = new BigDecimal("5");
//    	CheckoutHandler.setTotalCost(total); //Add $5 to total cost
//    	//Bypass startCheckout method
//    	checkout.setInCheckout(true);
//    	//$4 in coins to pay before adding another item
//    	Coin[] coins = { toonie, toonie};
//    	//$5 in coins to pay remaining balance
//    	Coin[] coins2 = { toonie, toonie, loonie }; 
//    
//    	//Schedule the list of coins to be inserted starting 1.5 seconds after starting payment.
//    	//There is a 1 second delay between each coin insertion.
//    	scheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins), 1500, TimeUnit.MILLISECONDS);
//    	
//    	//Scan an item after 4.5 seconds
//    	scheduler.schedule(new ScanItemRunnable(this.Station.mainScanner, cornFlakes), 4500, TimeUnit.MILLISECONDS);
//    	
//    	//Put item on scale after 5.5 seconds    	
//    	scheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, cornFlakes), 5500, TimeUnit.MILLISECONDS);
//    	
//    	//Pay remaining balance after 9.5 seconds
//    	scheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins2), 9500, TimeUnit.MILLISECONDS);
//    	
//    	
////    	scheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.baggingArea, cornFlakes), 12500, TimeUnit.MILLISECONDS);
//    	
//    	checkout.payWithCash(total);
//    	
//    	assertTrue(checkout.getTotalMoneyPaid().equals(new BigDecimal("9")));    	
//    	
//    }
//
//	@Test(expected = NegativeNumberException.class)
//	public void testInvalidBagWeight()
//			throws InterruptedException, OverloadException, EmptyException, DisabledException {
//		
//    	String inputString = "-1\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	System.setIn(customInputStream);
//		checkout.configureBagWeight(); // set to an invalid bag weight
//
//	}
//
//	@Test
//	public void verifyExpectedWeightWithBags()
//			throws InterruptedException, OverloadException, EmptyException, DisabledException {
//		
//		//Setup simulated input
//    	//User will select 0 bags
//    	//Choose to swipe their membership card
//    	//They will pay in full ($0)
//    	//They will pay with cash
//    	String inputString = "1\n" + "skip\n" + "full\n" + "cash\n";
//    	
//    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
//    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream);
//    	checkout.updateTouchScreen(ts);
//    	this.touchScreen = ts;
//    	
//		
//		Barcode bagCode = new Barcode(new Numeral[] { Numeral.four, Numeral.four, Numeral.four });
//		BarcodedItem bagItem = new BarcodedItem(bagCode,this.checkout.getBagWeight());
////		scheduler.schedule(new ScanTestMembershipCardRunnable(this.Station.cardReader, "Membership"), 5000, TimeUnit.MILLISECONDS);
//		scheduler.schedule(
//				new PlaceItemOnScaleRunnable(this.Station.baggingArea, bagItem), 1000,
//				TimeUnit.MILLISECONDS);
//		scheduler.schedule(
//				new RemoveItemOnScaleRunnable(this.Station.baggingArea, bagItem), 2000,
//				TimeUnit.MILLISECONDS);
//		// start checkout
//		checkout.startCheckout();
//		assertTrue(Math.floor(checkout.getExpectedWeight()) == 50.0);
//	}
	
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

