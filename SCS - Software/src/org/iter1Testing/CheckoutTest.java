//Brody Long - 30022870 
//Kamrul Ahsan Noor- 30078754

package org.iter1Testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.controlSoftware.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.BanknoteValidatorObserver;
import org.lsmr.selfcheckout.devices.observers.BarcodeScannerObserver;
import org.lsmr.selfcheckout.devices.observers.CoinDispenserObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

@RunWith(JUnit4.class)
public class CheckoutTest {
	
	private SelfCheckoutStation Station;
	private TouchScreen touchScreen;
	private Checkout checkout;
	private ScheduledExecutorService addItemsToScaleScheduler;
	private PayWithBanknote customBanknoteValidatorObserver;
	private PayWithCoin customCoinDispenserObserver;
	private ItemInBaggingArea customScaleObserver;
	private ProcessScannedItem customScannerObserver;
	private DummyItemProducts itemProducts;
	private DummyBarcodeLookup lookup;
	
	private static Banknote fiveDollarBanknote = new Banknote(DummySelfCheckoutStation.getCurrency(), 5);
	private static Banknote tenDollarBanknote = new Banknote(DummySelfCheckoutStation.getCurrency(), 10);
	private static Banknote twentyDollarBanknote = new Banknote(DummySelfCheckoutStation.getCurrency(), 20);
	private static Banknote fiveDollarBanknoteUSD = new Banknote(Currency.getInstance("USD"), 5);
	private static Banknote twelveDollarBanknote = new Banknote(DummySelfCheckoutStation.getCurrency(), 12);
	
	private static Coin nickel = new Coin(DummySelfCheckoutStation.getCurrency(), new BigDecimal(0.05));
	private static Coin quarter = new Coin(DummySelfCheckoutStation.getCurrency(), new BigDecimal(0.25));
	private static Coin loonie = new Coin(DummySelfCheckoutStation.getCurrency(), new BigDecimal(1.00));
	private static Coin toonie = new Coin(DummySelfCheckoutStation.getCurrency(), new BigDecimal(2.00));
	private static Coin quarterUSD = new Coin(Currency.getInstance("USD"), new BigDecimal(.25));
	private static Coin invalidCoin = new Coin(DummySelfCheckoutStation.getCurrency(), new BigDecimal(0.75));
	
	private BarcodedItem milkJug;

	//Initialize
	@Before
	public void setup() {
		this.Station = new DummySelfCheckoutStation();
		itemProducts = new DummyItemProducts();
		this.lookup = new DummyBarcodeLookup(itemProducts.IPList);
		this.touchScreen = new TouchScreen();
		this.checkout = new Checkout(this.touchScreen, 
									 this.Station.scanner, 
									 this.Station.banknoteInput, //Checkout can disable banknote slot
									 this.Station.coinSlot,      //Checkout can disable coin slot
									 this.Station.scale);
		
		milkJug = lookup.get(itemProducts.BarcodeList.get(0)).getItem();
		
		//Initialize a new custom Barcode scanner observer
		this.customScannerObserver = new ProcessScannedItem(this.Station.scanner,
													 this.lookup, 
													 this.Station.scale, 
													 touchScreen); 
		this.Station.scanner.attach((BarcodeScannerObserver) customScannerObserver);
		
		//Initialize a new custom scale observer
		this.customScaleObserver = new ItemInBaggingArea(this.Station.scale, 
				   										 this.customScannerObserver, 
				   										 touchScreen, 
				   										 checkout);
		this.Station.scale.attach((ElectronicScaleObserver) customScaleObserver);
		
		//Initialize a new custom banknote validator observer
		this.customBanknoteValidatorObserver = new PayWithBanknote();
		//Attach the custom observer to the relevant device
		this.Station.banknoteValidator.attach((BanknoteValidatorObserver) customBanknoteValidatorObserver);

		//Initialize a new custom coin dispenser observer
		this.customCoinDispenserObserver = new PayWithCoin();
		//For each dispenser in the system, attach the custom observer to it
		for (BigDecimal dispenser : this.Station.coinDispensers.keySet()) 
		{
			this.Station.coinDispensers.get(dispenser).attach((CoinDispenserObserver) customCoinDispenserObserver);
		}
		
		addItemsToScaleScheduler =  Executors.newScheduledThreadPool(4);
	}

    @Test
    public void verifyInCheckoutAfterStartingCheckout() throws InterruptedException, OverloadException {
        // start checkout
        checkout.startCheckout();
        // verify device is disabled or not
        assertTrue(checkout.isInCheckout());
    }
	
    @Test
    public void verifyScannerIsDisabledAfterStartingCheckout() throws InterruptedException, OverloadException {
        // start checkout
        checkout.startCheckout();
        // verify device is disabled or not
        assertTrue(Station.scanner.isDisabled());
    }
    
    @Test
    public void verifyExpectedWeightWhenStartingCheckout() throws InterruptedException, OverloadException {
        // start checkout 
    	this.Station.scale.add(milkJug); //Weighs 4000 grams
        checkout.startCheckout();
        // verify device is disabled or not
        assertTrue(checkout.getExpectedWeight() == 4000);
    }
    
    @Test
    public void verifyAskedForPaymentOptionsWhenStartingCheckout() throws InterruptedException, OverloadException {
        // start checkout
        checkout.startCheckout();
        // verify device is disabled or not
        assertTrue(touchScreen.paymentOptionsDisplayed.get());
    }
    
    @Test
    public void verifyWeightIssueDetectedWhenStartingCheckout() throws InterruptedException, OverloadException {
    	//Put a milk jug on the scale 1.5 seconds after starting checkout
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.scale, milkJug), 1500, TimeUnit.MILLISECONDS);
    	//Remove the milk jug 5 seconds after starting checkout
    	addItemsToScaleScheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.scale, milkJug), 5000, TimeUnit.MILLISECONDS);
        checkout.startCheckout();
        
        //Touch screen should have been informed of the item being added during checkout
        assertTrue(touchScreen.invalidWeightInCheckoutDetected.get());
        //Touch screen should have been informed of the item being removed during checkout
    	assertTrue(touchScreen.invalidWeightInCheckoutCorrected.get());
    }
    
    
    @Test
    public void testPayingWithBanknotesNoChangeNoWeight() throws InterruptedException, OverloadException {
    	//Change will not be given out
    	//Weight does not change during payment
    	//Cleanup will be tested in here also
    	Checkout.addToTotalCost(new BigDecimal(50)); //Add $50 to total cost
    	//Bypass startCheckout method
    	checkout.setInCheckout(true);
    	//Create a list of banknotes matching the total cost of all items
    	Banknote[] banknotes = { twentyDollarBanknote, tenDollarBanknote, tenDollarBanknote, fiveDollarBanknote, fiveDollarBanknote };
    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
    	//There is a 1 second delay between each banknote insertion.
    	addItemsToScaleScheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes), 1500, TimeUnit.MILLISECONDS);
    	
    	checkout.payWithBanknotes();
    	
    	//Touch screen should not have been informed of change being dispensed
    	assertFalse(touchScreen.changeDispensed.get());
    	//Should no longer be in checkout mode
    	assertFalse(checkout.isInCheckout());
    	//Touch screen should have reset to the welcome screen
    	assertTrue(touchScreen.resetSuccessful.get());
    }
    
    @Test
    public void testPayingWithBanknotesWithChangeNoWeight() throws InterruptedException, OverloadException {
    	//Change will be given out
    	//Weight does not change during payment
    	//Cleanup will be tested in here also
    	Checkout.addToTotalCost(new BigDecimal(50)); //Add $50 to total cost
    	//Bypass startCheckout method
    	checkout.setInCheckout(true);
    	//Create a list of banknotes exceeding the total cost of all items
    	Banknote[] banknotes = { twentyDollarBanknote, twentyDollarBanknote, twentyDollarBanknote };
    	//Schedule the list of banknotes to be inserted starting 1.5 seconds after starting payment.
    	//There is a 1 second delay between each banknote insertion.
    	addItemsToScaleScheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes), 1500, TimeUnit.MILLISECONDS);
    	
    	checkout.payWithBanknotes();
    	
    	//Touch screen should have been informed of change being dispensed
    	assertTrue(touchScreen.changeDispensed.get());
    	//Should no longer be in checkout mode
    	assertFalse(checkout.isInCheckout());
    	//Touch screen should have reset to the welcome screen
    	assertTrue(touchScreen.resetSuccessful.get());
    }
    
    @Test
    public void testPayingWithBanknotesWeightChange() throws InterruptedException, OverloadException {
    	//Change will not be given out
    	//Weight DOES change during payment
    	//Must be corrected before software can continue
    	//Cleanup will be tested in here also
    	
    	//Add a milk jug to the scale
    	this.Station.scale.add(milkJug);
    	Checkout.addToTotalCost(new BigDecimal(50)); //Add $50 to total cost
    	//Bypass startCheckout method
    	checkout.setInCheckout(true);
    	checkout.setExpectedWeight(4000);
    	
    	//Put 2 $20 bills in before removing milk jug
    	Banknote[] banknotes1 = { twentyDollarBanknote, twentyDollarBanknote };
    	//After putting the jug back on the scale, pay the rest
    	Banknote[] banknotes2 = { fiveDollarBanknote, fiveDollarBanknote };
    	
    	addItemsToScaleScheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes1), 1500, TimeUnit.MILLISECONDS);
    	addItemsToScaleScheduler.schedule(new PayWithBanknotesRunnable(this.Station.banknoteInput, banknotes2), 6500, TimeUnit.MILLISECONDS);
    	addItemsToScaleScheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.scale, milkJug), 4500, TimeUnit.MILLISECONDS);
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.scale, milkJug), 5500, TimeUnit.MILLISECONDS);
    	
    	//Take the milk off the scale during cleanup
    	addItemsToScaleScheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.scale, milkJug), 11500, TimeUnit.MILLISECONDS);
    	
    	checkout.payWithBanknotes();
    	
    	//Touch screen should have been informed of the weight issue, and its correction
    	assertTrue(touchScreen.invalidWeightInCheckoutDetected.get());
    	assertTrue(touchScreen.invalidWeightInCheckoutCorrected.get());
    	//Successful cleanup and reset
    	assertTrue(touchScreen.resetSuccessful.get());
    }
    
    @Test
    public void testPayingWithCoinsNoChangeNoWeight() throws InterruptedException, OverloadException {
    	//Change will not be given out
    	//Weight does not change during payment
    	//Cleanup will be tested in here also
    	Checkout.addToTotalCost(new BigDecimal(6.75)); //Add $12.75 to total cost
    	//Bypass startCheckout method
    	checkout.setInCheckout(true);
    	//Create a list of coins equal to the total cost of all items
    	Coin[] coins = { toonie, toonie, loonie, loonie, quarter, quarter, quarter};
    	//Schedule the list of coins to be inserted starting 1.5 seconds after starting payment.
    	//There is a 1 second delay between each coin insertion.
    	addItemsToScaleScheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins), 1500, TimeUnit.MILLISECONDS);
    	
    	checkout.payWithCoins();
    	
    	//Touch screen should not have been informed of change being dispensed
    	assertFalse(touchScreen.changeDispensed.get());
    	//Should no longer be in checkout mode
    	assertFalse(checkout.isInCheckout());
    	//Touch screen should have reset to the welcome screen
    	assertTrue(touchScreen.resetSuccessful.get());
    }
    
    @Test
    public void testPayingWithCoinsWithChangeNoWeight() throws InterruptedException, OverloadException {
    	//Change will be given out
    	//Weight does not change during payment
    	//Cleanup will be tested in here also
    	Checkout.addToTotalCost(new BigDecimal(6.75)); //Add $12.75 to total cost
    	//Bypass startCheckout method
    	checkout.setInCheckout(true);
        //Create a list of coins exceeding the total cost of all items
    	Coin[] coins = { toonie, toonie, toonie, loonie };
    	
    	//Schedule the list of coins to be inserted starting 1.5 seconds after starting payment.
    	//There is a 1 second delay between each coin insertion.
    	addItemsToScaleScheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins), 1500, TimeUnit.MILLISECONDS);
    	
    	checkout.payWithCoins();
    	
    	//Touch screen should have been informed of change being dispensed
    	assertTrue(touchScreen.changeDispensed.get());
    	//Should no longer be in checkout mode
    	assertFalse(checkout.isInCheckout());
    	//Touch screen should have reset to the welcome screen
    	assertTrue(touchScreen.resetSuccessful.get());
    }
    
    @Test
    public void testPayingWithCoinsInvalidWeight() throws InterruptedException, OverloadException {
    	//Change will not be given out
    	//Weight DOES change during payment
    	//Must be corrected before software can continue
    	//Cleanup will be tested in here also
    	
    	this.Station.scale.add(milkJug);
    	Checkout.addToTotalCost(new BigDecimal(6.75)); //Add $50 to total cost
    	//Bypass startCheckout method
    	checkout.setInCheckout(true);
    	checkout.setExpectedWeight(4000);
    	//Put 2 toonies in before removing milk jug
    	Coin[] coins1 = { toonie, toonie };
    	//After putting the jug back on the scale, pay the rest
    	Coin[] coins2 = { toonie, quarter, quarter, quarter };
    	
    	addItemsToScaleScheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins1), 1500, TimeUnit.MILLISECONDS);
    	addItemsToScaleScheduler.schedule(new PayWithCoinsRunnable(this.Station.coinSlot, coins2), 6500, TimeUnit.MILLISECONDS);
    	addItemsToScaleScheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.scale, milkJug), 4500, TimeUnit.MILLISECONDS);
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.scale, milkJug), 5500, TimeUnit.MILLISECONDS);
    	addItemsToScaleScheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.scale, milkJug), 12500, TimeUnit.MILLISECONDS);
    	
    	checkout.payWithCoins();
    	
    	//Touch screen should have been informed of the weight issue, and its correction
    	assertTrue(touchScreen.invalidWeightInCheckoutDetected.get());
    	assertTrue(touchScreen.invalidWeightInCheckoutCorrected.get());
    	//Successful cleanup and reset
    	assertTrue(touchScreen.resetSuccessful.get());
    }    
}
