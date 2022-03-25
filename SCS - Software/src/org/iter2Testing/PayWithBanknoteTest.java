//Mikail Munir - 30086727

package org.iter2Testing;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.controlSoftware.Checkout;
import org.controlSoftware.PayWithBanknote;
import org.controlSoftware.TouchScreen;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.BanknoteValidatorObserver;

@RunWith(JUnit4.class)
public class PayWithBanknoteTest {
	
	private SelfCheckoutStation Station;
	private TouchScreen touchScreen;
	private Checkout checkout;
	private PayWithBanknote customObserver;
	private static Banknote fiveDollarBanknote = new Banknote(DummySelfCheckoutStation.getCurrency(), 5);

	//Initialize
	@Before
	public void setup() {
		this.Station = new DummySelfCheckoutStation();
		this.touchScreen = new TouchScreen();
		this.checkout = new Checkout(this.touchScreen, 
									 this.Station.mainScanner, 
									 this.Station.banknoteInput, //Checkout can disable banknote slot
									 this.Station.coinSlot,      //Checkout can disable coin slot
									 this.Station.baggingArea);
		//Initialize a new custom banknote validator observer
		this.customObserver = new PayWithBanknote();
		
		//Attach the custom observer to the relevant device
		this.Station.banknoteValidator.attach((BanknoteValidatorObserver) customObserver);
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
    	Assert.assertTrue(checkout.compareTotals() == 0);	//Check if total cost = total paid
	    
    }
    
    public void testInvalidBanknoteDetected() throws DisabledException, OverloadException {
    	Banknote invalidNote = new Banknote(DummySelfCheckoutStation.getCurrency(), 30);
    	Station.banknoteInput.accept(invalidNote);
    	Assert.assertTrue(!customObserver.getValid());
    }
}
