//Gurleen Arora - 30123071

package org.iter2Testing;

import static org.junit.Assert.assertEquals;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.controlSoftware.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.CoinDispenserObserver;

@RunWith(JUnit4.class)
public class PayWithCoinTest {
	
	private SelfCheckoutStation Station;
	private TouchScreen touchScreen;
	private Checkout checkout;
	private Object customObserver;
	private ReceiptHandler receiptHandler;
    private static Coin fiveCents = new Coin(DummySelfCheckoutStation.getCurrency(),new BigDecimal(0.05));
   
	//Initialize
	@Before
	public void setup() {

		this.Station = new DummySelfCheckoutStation();
		this.touchScreen = new TouchScreen();
		this.receiptHandler = new ReceiptHandler(this.Station.printer);
		this.checkout = new Checkout(this.touchScreen, 
									 this.Station.mainScanner, 
									 this.Station.banknoteInput, //Checkout can disable banknote slot
									 this.Station.coinSlot,      //Checkout can disable coin slot
									 this.Station.baggingArea,
									 this.receiptHandler);
		//Initialize a new custom coin dispenser observer
		this.customObserver = new PayWithCoin();
		
		//For each dispenser in the system, attach the custom observer to it
		for (BigDecimal dispenser : this.Station.coinDispensers.keySet()) 
		{
			this.Station.coinDispensers.get(dispenser).attach((CoinDispenserObserver) customObserver);
		}

	}

    @Test(expected = DisabledException.class)
    public void disabledCoinSlot() throws DisabledException{
		Station.coinSlot.disable();
	    Station.coinSlot.accept(fiveCents);
    }
    
    @Test
    public void enabledCoinSlot() throws DisabledException {
    	Station.coinSlot.enable();
	Station.coinSlot.accept(fiveCents);
    	checkout.resetCheckoutTotals();

    }
    
    @Test
    public void validCoin() throws DisabledException {
    	Station.coinSlot.enable();
    	checkout.addToTotalCost(new BigDecimal(0.05));				//dont really need this
    	this.Station.coinSlot.accept(fiveCents);
    	assertEquals(checkout.getTotalCost(),checkout.getTotalMoneyPaid());	//dont really need this
    	checkout.resetCheckoutTotals();

    }
    
    
   @Test
    public void InvalidCoin() throws DisabledException, OverloadException {
	Coin invalidCoin = new Coin(DummySelfCheckoutStation.getCurrency(),new BigDecimal(0.06));
       this.Station.coinSlot.accept(invalidCoin);
       assertEquals(new BigDecimal(0.00),checkout.getTotalMoneyPaid());

    }
   
}
