//Shufan Zhai - 30117333

package org.iter3Testing;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.controlSoftware.*;
import org.controlSoftware.customer.CheckoutHandler;
import org.controlSoftware.deviceHandlers.BaggingAreaScaleHandler;
import org.controlSoftware.deviceHandlers.ScannerHandler;
import org.controlSoftware.deviceHandlers.ReceiptHandler;
import org.controlSoftware.general.TouchScreenSoftware;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutSoftware;
import org.driver.SelfCheckoutStationUnit;
import org.driver.databases.TestProducts;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

@RunWith(JUnit4.class)
public class BaggingAreaHandlerTest {
	
	private SelfCheckoutStationUnit stationUnit;
	private SelfCheckoutStation stationHardware;
	private SelfCheckoutData stationData;
	private SelfCheckoutSoftware stationSoftware;
	private TouchScreenSoftware touchScreenSoftware;
	private TestProducts testProducts;
	
	private ScheduledExecutorService addItemsToScaleScheduler;
	private ReceiptHandler receiptHandler;
	private BaggingAreaScaleHandler baggingAreaScaleHandler;

	//Initialize
	@Before
	public void setup() {
	//===============================================================================		
		this.stationUnit = new SelfCheckoutStationUnit();
		
		this.stationHardware = stationUnit.getSelfCheckoutStationHardware();
		this.stationData = stationUnit.getSelfCheckoutData();
		this.stationSoftware = stationUnit.getSelfCheckoutSoftware();
		this.touchScreenSoftware = stationUnit.getTouchScreenSoftware();
		
		this.baggingAreaScaleHandler = new BaggingAreaScaleHandler(stationData, stationSoftware); 
		
		//Setup receipt printer
		try {
			this.stationHardware.printer.addInk(2500);
			this.stationHardware.printer.addPaper(512);
		} catch (OverloadException e) {
			System.out.println("Overfilled!");
			e.printStackTrace();
		}
		
		//Setup receipt printer
					
		this.addItemsToScaleScheduler = Executors.newScheduledThreadPool(5);
	}

    	@Test
	public void testScaleOverloaded() {
		ItemStub dummyOverloadItem = new ItemStub(50001);
		stationHardware.baggingArea.add(dummyOverloadItem);
		assertTrue(baggingAreaScaleHandler.isOverloaded());
	}

	@Test
	public void testScaleOutOfOverload() {
		ItemStub dummyOverloadItem = new ItemStub(50001);
		stationHardware.baggingArea.add(dummyOverloadItem);
		stationHardware.baggingArea.remove(dummyOverloadItem);
		assertTrue(!baggingAreaScaleHandler.isOverloaded());
	}
}
