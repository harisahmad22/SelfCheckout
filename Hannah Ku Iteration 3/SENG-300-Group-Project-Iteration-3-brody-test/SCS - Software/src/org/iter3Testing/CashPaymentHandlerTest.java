//Mikail Munir - 30086727

package org.iter3Testing;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.controlSoftware.customer.CheckoutHandler;
import org.controlSoftware.deviceHandlers.ReceiptHandler;
import org.controlSoftware.deviceHandlers.payment.CashPaymentHandler;
import org.controlSoftware.general.TouchScreenSoftware;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutSoftware;
import org.driver.SelfCheckoutStationUnit;
import org.driver.databases.TestBarcodedProducts;
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
public class CashPaymentHandlerTest {
	
	private SelfCheckoutStationUnit stationUnit;
	private SelfCheckoutStation stationHardware;
	private SelfCheckoutData stationData;
	private SelfCheckoutSoftware stationSoftware;
	private TouchScreenSoftware touchScreenSoftware;
	private CashPaymentHandler cashPaymentHandler;
	private ReceiptHandler receiptHandler;
	private static Banknote fiveDollarBanknote = new Banknote(SelfCheckoutStationUnit.getCurrency(), 5);

	//Initialize
	@Before
	public void setup() {
	//===============================================================================		
		this.stationUnit = new SelfCheckoutStationUnit(1);
		
		this.stationHardware = stationUnit.getSelfCheckoutStationHardware();
		this.stationData = stationUnit.getSelfCheckoutData();
		this.stationSoftware = stationUnit.getSelfCheckoutSoftware();
		this.touchScreenSoftware = stationUnit.getTouchScreenSoftware();
		
		this.cashPaymentHandler = new CashPaymentHandler(stationData);
	}

    @Test (expected = DisabledException.class)
    public void testDisabledBanknoteSlot() throws OverloadException, DisabledException {
    	stationHardware.banknoteInput.disable();
    	stationHardware.banknoteInput.accept(fiveDollarBanknote); //Try inserting $5 bill
    }
    
    @Test
    public void testEnabledBanknoteSlot() throws OverloadException, DisabledException {
    	stationHardware.banknoteInput.disable();
    	stationHardware.banknoteInput.enable();
    	stationHardware.banknoteInput.accept(fiveDollarBanknote); //Try inserting $5 bill
    }
    
    @Test
    public void testValidBanknoteDetected() throws DisabledException, OverloadException {
    	
    	stationData.addToTotalCost(new BigDecimal(fiveDollarBanknote.getValue())); //Add $5 to bill
    	stationHardware.banknoteInput.accept(fiveDollarBanknote); 			//Try inserting $5 bill 
    	Assert.assertTrue(stationData.compareTotals() == 1);	//Check if total cost = total paid
	    
    }
    
    @Test
    public void testInvalidBanknoteDetected() throws DisabledException, OverloadException {
    	stationData.addToTotalCost(new BigDecimal(fiveDollarBanknote.getValue())); //Add $5 to bill
    	Banknote invalidNote = new Banknote(SelfCheckoutStationUnit.getCurrency(), 30);
    	stationHardware.banknoteInput.accept(invalidNote);
    	assertTrue(cashPaymentHandler.isInValidDetected() == true);
    }
}
