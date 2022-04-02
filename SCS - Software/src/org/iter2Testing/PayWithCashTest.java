//Mikail Munir - 30086727

package org.iter2Testing;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.controlSoftware.customer.CheckoutSoftware;
import org.controlSoftware.deviceHandlers.ReceiptHandler;
import org.controlSoftware.deviceHandlers.payment.PayWithCash;
import org.controlSoftware.general.TouchScreenSoftware;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.BanknoteValidatorObserver;
import org.lsmr.selfcheckout.devices.observers.CoinDispenserObserver;
import org.lsmr.selfcheckout.devices.observers.CoinStorageUnitObserver;
import org.lsmr.selfcheckout.devices.observers.CoinValidatorObserver;

@RunWith(JUnit4.class)
public class PayWithCashTest {
	
	private SelfCheckoutStation Station;
	private TouchScreenSoftware touchScreen;
	private CheckoutSoftware checkout;
	private PayWithCash customCashPaymentObserver;
	private ReceiptHandler receiptHandler;
	private static Banknote fiveDollarBanknote = new Banknote(DummySelfCheckoutStation.getCurrency(), 5);

	//Initialize
	@Before
	public void setup() {
		this.Station = new DummySelfCheckoutStation();
		this.touchScreen = new TouchScreenSoftware(System.in);
		this.receiptHandler = new ReceiptHandler(this.Station.printer);
		this.checkout = new CheckoutSoftware(this.touchScreen, 
									 this.Station.mainScanner, 
									 this.Station.banknoteInput, //Checkout can disable banknote slot
									 this.Station.coinSlot,      //Checkout can disable coin slot
									 this.Station.baggingArea,
									 this.Station,
									 this.receiptHandler,
									 null,
									 null,
									 this.Station.cardReader);
		//Initialize a new custom banknote validator observer
		this.customCashPaymentObserver = new PayWithCash(this.Station);
		
		//Attach the custom cash payment observer to the relevant devices
		this.Station.banknoteValidator.attach((BanknoteValidatorObserver) customCashPaymentObserver);
		this.Station.coinValidator.attach((CoinValidatorObserver) customCashPaymentObserver);
		this.Station.coinStorage.attach((CoinStorageUnitObserver) customCashPaymentObserver);
		
		for (BigDecimal dispenser : this.Station.coinDispensers.keySet()) 
		{
			this.Station.coinDispensers.get(dispenser).attach((CoinDispenserObserver) customCashPaymentObserver);
		}
		
	}

    @Test (expected = DisabledException.class)
    public void testDisabledBanknoteSlot() throws OverloadException, DisabledException {
    	Station.banknoteInput.disable();
    	Station.banknoteInput.accept(fiveDollarBanknote); //Try inserting $5 bill
    }
    
    @Test
    public void testEnabledBanknoteSlot() throws OverloadException, DisabledException {
    	Station.banknoteInput.disable();
    	Station.banknoteInput.enable();
    	Station.banknoteInput.accept(fiveDollarBanknote); //Try inserting $5 bill
    }
    
    @Test
    public void testValidBanknoteDetected() throws DisabledException, OverloadException {
    	
    	checkout.addToTotalCost(new BigDecimal(fiveDollarBanknote.getValue())); //Add $5 to bill
    	Station.banknoteInput.accept(fiveDollarBanknote); 			//Try inserting $5 bill 
    	Assert.assertTrue(checkout.compareTotals() == 1);	//Check if total cost = total paid
	    
    }
    
    public void testInvalidBanknoteDetected() throws DisabledException, OverloadException {
    	Banknote invalidNote = new Banknote(DummySelfCheckoutStation.getCurrency(), 30);
    	Station.banknoteInput.accept(invalidNote);
//    	Assert.assertTrue(!customCashPaymentObserver.getValid());
    }
}
