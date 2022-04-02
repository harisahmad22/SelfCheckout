//Jacky Liang 
//Yianni Hontzias

package org.iter2Testing;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.controlSoftware.customer.CheckoutHandler;
import org.controlSoftware.data.BankClientInfo;
import org.controlSoftware.deviceHandlers.ScaleHandler;
import org.controlSoftware.deviceHandlers.ScannerHandler;
import org.controlSoftware.deviceHandlers.ReceiptHandler;
import org.controlSoftware.deviceHandlers.membership.ScansMembershipCard;
import org.controlSoftware.deviceHandlers.payment.PayWithCash;
import org.controlSoftware.deviceHandlers.payment.PayWithCreditCard;
import org.controlSoftware.deviceHandlers.payment.PayWithDebitCard;
import org.controlSoftware.general.TouchScreenSoftware;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.ChipFailureException;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.BarcodeScannerObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;


public class PayWithCreditTest {

	
	private SelfCheckoutStation Station;
	private TouchScreenSoftware touchScreen;
	private CheckoutHandler checkout;
	private ScheduledExecutorService scheduler;
	private PayWithCash customCashPaymentObserver;
	private ScaleHandler customScaleObserver;
	private ScannerHandler customScannerObserver;
	private DummyItemProducts itemProducts;
	private DummyBarcodeLookup lookup;
	private CardData cd;
	
	//We will be overriding the regular System.in input stream with a
	//Byte array input stream to simulate user input 
	//This idea comes from the stack overflow post: https://stackoverflow.com/a/6416179
	private final InputStream backupInputStream = System.in; //Save a backup of System.in
	private InputStream customInputStream = backupInputStream;
	
	
	
	
	public static int banknoteChangeValue = 0; //Updated by DanglingBanknoteRemover runnable 
	
	private ReceiptHandler receiptHandler;
	
	
	@Before 
	public void setup()
	{

		
		this.Station = new DummySelfCheckoutStation();
		itemProducts = new DummyItemProducts();
		this.lookup = new DummyBarcodeLookup(itemProducts.IPList);
		this.touchScreen = new TouchScreenSoftware(System.in);
		this.receiptHandler = new ReceiptHandler(this.Station.printer);
		
		//Setup Card
		Card testDebitCard = new Card("Debit", "1234567890", "Test Card Holder", "000", "1234", true, true);
		Card testCreditCard = new Card("Credit", "1234567890", "Test Card Holder", "000", "1234", true, true);
		BankClientInfo bankClientInfo = new BankClientInfo("Credit", "1234567890", "Test Card Holder", "000", "1234", true, true, new BigDecimal("10"), new BigDecimal("2500"));
		PayWithCreditCard payWithCreditTest = new PayWithCreditCard(this.Station, testCreditCard, null, "1234", bankClientInfo);
		PayWithDebitCard payWithDebtTest = new PayWithDebitCard(this.Station, testCreditCard, null, "1234", bankClientInfo);
		
		
		
		
		
		
		this.checkout = new CheckoutHandler(this.touchScreen, 
									 this.Station.mainScanner, 
									 this.Station.banknoteInput, //Checkout can disable banknote slot
									 this.Station.coinSlot,      //Checkout can disable coin slot
									 this.Station.baggingArea,
									 this.Station,
									 this.receiptHandler,
									 null,
									 payWithCreditTest,
									 this.Station.cardReader);
		//Setup receipt printer
		try {
			this.Station.printer.addInk(2500);
			this.Station.printer.addPaper(512);
		} catch (OverloadException e) {
			System.out.println("Printer Overfilled!");
			e.printStackTrace();
		}
		
		//Setup receipt printer
		//Initialize a new custom Barcode scanner observer
		this.customScannerObserver = new ScannerHandler(this.Station.mainScanner,
													 this.lookup, 
													 this.Station.baggingArea, 
													 touchScreen, 
													 checkout, 
													 this.receiptHandler); 
		this.Station.mainScanner.attach((BarcodeScannerObserver) customScannerObserver);
		//Initialize a new custom scale observer
		this.customScaleObserver = new ScaleHandler(this.Station.baggingArea, 
				   										 this.customScannerObserver, 
				   										 touchScreen, 
				   										 checkout);
		this.Station.baggingArea.attach((ElectronicScaleObserver) customScaleObserver);
	}
	
	
	/*
	
	Test a successful checkout with tapping credit card
	
	*/
	@Test
	public void testSuccPayingWithTap() throws InterruptedException, OverloadException, EmptyException, DisabledException {
		String inputString = "0\n" + "skip\n" + "full\n" + "credit\n" + "tap\n";
    	
    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
    	checkout.updateTouchScreen(ts);
    	this.touchScreen = ts;
    	
    	
    	BigDecimal total = new BigDecimal("100");
    	CheckoutHandler.addToTotalCost(total); //Add $100 to total cost
    	
    	checkout.startCheckout();

		String partialReceipt = this.Station.printer.removeReceipt();
		System.out.println("Receipt Generated:\n" + partialReceipt);

    	assertFalse(checkout.isInCheckout());
    	assertTrue(touchScreen.resetSuccessful.get());	
	}
	
	/*
	 * Test a successful checkout with inserting credit card
	 * 
	 */
	@Test
	public void testSuccPayingWithInsert() throws InterruptedException, OverloadException, EmptyException, DisabledException {
		String inputString = "0\n" + "skip\n" + "full\n" + "credit\n" + "insert\n";
    	
    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); //Update the checkout's touch screen with the custom IS
    	checkout.updateTouchScreen(ts);
    	this.touchScreen = ts;
    	
    	BigDecimal total = new BigDecimal("100");
    	CheckoutHandler.addToTotalCost(total); //Add $100 to total cost
    	
    	checkout.startCheckout();

		String partialReceipt = this.Station.printer.removeReceipt();
		System.out.println("Receipt Generated:\n" + partialReceipt);

    	assertFalse(checkout.isInCheckout());
    	assertTrue(touchScreen.resetSuccessful.get());	
	}
	

	/*
	 * Test a successful checkout with swiping credit card
	 * 
	 */
	@Test
	public void testSuccPayingWithSwipe() throws InterruptedException, OverloadException, EmptyException, DisabledException {
		String inputString = "0\n" + "skip\n" + "full\n" + "credit\n" + "swipe\n";
    	
    	customInputStream = new ByteArrayInputStream(inputString.getBytes());
    	TouchScreenSoftware ts = new TouchScreenSoftware(customInputStream); 
    	checkout.updateTouchScreen(ts);
    	this.touchScreen = ts;

    	
    	BigDecimal total = new BigDecimal("100");
    	CheckoutHandler.addToTotalCost(total); 
    	
    	checkout.startCheckout();
    	
    	
		String partialReceipt = this.Station.printer.removeReceipt();
		System.out.println("Receipt Generated:\n" + partialReceipt);

    	assertFalse(checkout.isInCheckout());
    	assertTrue(touchScreen.resetSuccessful.get());	
	}
	
	
	/*
	 * Test disabled and enabled
	 * 
	 */
	@Test
	public void testEnabledDisabled()
	{
		Card testCreditCard = new Card("Credit", "1234567890", "Test Card Holder", "000", "1234", true, true);
		BankClientInfo bankClientInfo = new BankClientInfo("Credit", "1234567890", "Test Card Holder", "000", "1234", true, true, new BigDecimal("10"), new BigDecimal("2500"));
		PayWithCreditCard payWithCreditTest = new PayWithCreditCard(this.Station, testCreditCard, null, "1234", bankClientInfo);
		payWithCreditTest.disabled(this.Station.cardReader);
		payWithCreditTest.enabled(this.Station.cardReader);
	}
	
	/*
	 * Test a failed insert
	 * 
	 */
	@Test 
	public void failInsertTest()
	{
		Card failCard = new Card("Credit", "1234567890", "Test Card Holder", "000", "1234", true, false);
		BankClientInfo bankClientInfo = new BankClientInfo("Credit", "1234567890", "Test Card Holder", "000", "1234", true, true, new BigDecimal("10"), new BigDecimal("2500"));
		CardData cd = null;
		PayWithCreditCard failed = new PayWithCreditCard(this.Station, failCard, cd, "1234", bankClientInfo);
		failed.cardInserted(this.Station.cardReader);
	}
	
	/*
	 * Test a failed tap
	 * 
	 */
	@Test 
	public void failTapTest()
	{
		Card c = new Card("Credit", "1234567890", "Test Card Holder", "000", "1234", true, true);
		BankClientInfo bankClientInfo = new BankClientInfo("Credit", "1234567890", "Test Card Holder", "000", "1234", true, true, new BigDecimal("10"), new BigDecimal("2500"));
		CardData cd = null;
		PayWithCreditCard p = new PayWithCreditCard(this.Station, c, cd, "1234", bankClientInfo);
		// repeat until an error will occur on the tap
		for(int i = 0; i < 100; i++)
		{
			p.cardTapped(this.Station.cardReader);
		}
	}
	
	/*
	 * Test a failed swipe
	 * 
	 */
	@Test 
	public void failSwipeTest()
	{
		Card c = new Card("Credit", "1234567890", "Test Card Holder", "000", "1234", true, true);
		BankClientInfo bankClientInfo = new BankClientInfo("Credit", "1234567890", "Test Card Holder", "000", "1234", true, true, new BigDecimal("10"), new BigDecimal("2500"));
		CardData cd = null;
		PayWithCreditCard p = new PayWithCreditCard(this.Station, c, cd, "1234", bankClientInfo);
		for(int i = 0; i < 100; i++) // repeat until a error will occur
		{
			p.cardSwiped(this.Station.cardReader);
		}
	}
	
	/*
	 * Test when the verification is false as the user has gone over their limit
	 * 
	 */
	@Test
	public void failCreditPayment()
	{
		Card c = new Card("Credit", "1234567890", "Test Card Holder", "000", "1234", true, true);
		BankClientInfo bankClientInfo = new BankClientInfo("Credit", "1234567890", "Test Card Holder", "000", "1234", true, true, new BigDecimal("10"), new BigDecimal("2500"));
		CardData cd = null;
		PayWithCreditCard p = new PayWithCreditCard(this.Station, c, cd, "1234", bankClientInfo);
		assertFalse(p.checkBankClientInfo(this.Station.cardReader, new BigDecimal("2600")));
	}
	
	/*
	 * Test data read
	 * 
	 */
	@Test
	public void cardDataRead()
	{
		Card c = new Card("Credit", "1234567890", "Test Card Holder", "000", "1234", true, true);
		BankClientInfo bankClientInfo = new BankClientInfo("Credit", "1234567890", "Test Card Holder", "100", "1234", true, true, new BigDecimal("10"), new BigDecimal("2500"));
		PayWithCreditCard p = new PayWithCreditCard(this.Station, c, null, "1234", bankClientInfo);
		p.cardDataRead(null, bankClientInfo);
	}

	/*
	 * Test when a CVV is failed 
	 * 
	 */
	@Test
	public void failedCVV()
	{
		Card testDebitCard = new Card("Debit", "1234567890", "Test Card Holder", "000", "1234", true, true);
		BankClientInfo bankClientInfo = new BankClientInfo("Credit", "1234567890", "Test Card Holder", "100", "1234", true, true, new BigDecimal("10"), new BigDecimal("2500"));
		PayWithDebitCard payWithDebitTest = new PayWithDebitCard(this.Station, testDebitCard, null, "1234", bankClientInfo);
		Card c = new Card("Credit", "1234567890", "Test Card Holder", "000", "1234", true, true);
		payWithDebitTest.cardTapped(this.Station.cardReader);
		cd = payWithDebitTest.getCardData();
		PayWithCreditCard p = new PayWithCreditCard(this.Station, c, cd, "1234", bankClientInfo);
		assertFalse(p.checkBankClientInfo(this.Station.cardReader, new BigDecimal("1")));
	}
	
	
}
