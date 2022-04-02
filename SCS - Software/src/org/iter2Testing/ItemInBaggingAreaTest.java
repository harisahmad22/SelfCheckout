//Shufan Zhai - 30117333

package org.iter2Testing;

import static org.junit.Assert.assertTrue;

import org.controlSoftware.*;
import org.controlSoftware.customer.CheckoutSoftware;
import org.controlSoftware.deviceHandlers.ItemInBaggingArea;
import org.controlSoftware.deviceHandlers.ProcessScannedItem;
import org.controlSoftware.deviceHandlers.ReceiptHandler;
import org.controlSoftware.general.TouchScreenSoftware;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

@RunWith(JUnit4.class)
public class ItemInBaggingAreaTest {
	
	private SelfCheckoutStation Station;
	private TouchScreenSoftware touchScreen;
	private CheckoutSoftware checkout;
	private ItemInBaggingArea customObserver; // changed from Object to ItemInBaggingArea
	private ProcessScannedItem customScannerObserver;
	private DummyBarcodeLookup lookup;
	private DummyItemProducts itemProducts;
	private ReceiptHandler receiptHandler;

	//Initialize
	@Before
	public void setup() {
		this.Station = new DummySelfCheckoutStation();
		itemProducts = new DummyItemProducts();
		this.lookup = new DummyBarcodeLookup(itemProducts.IPList);
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
		//Initialize a new custom Barcode scanner observer
		this.customScannerObserver = new ProcessScannedItem(this.Station.mainScanner,
															this.lookup, 
															this.Station.baggingArea, 
															touchScreen,
															checkout,
															this.receiptHandler);
		//Attach the scanner observer to the scanner
		this.Station.mainScanner.attach(customScannerObserver);
		
		//Initialize a new custom scale observer
		this.customObserver = new ItemInBaggingArea(this.Station.baggingArea, 
													this.customScannerObserver, 
													touchScreen, 
													checkout);
		
		//Attach the custom observer to the relevant device
		this.Station.baggingArea.attach((ElectronicScaleObserver) customObserver);

	}

    	@Test
	public void testScaleOverloaded() {
		ItemStub dummyOverloadItem = new ItemStub(50001);
		Station.baggingArea.add(dummyOverloadItem);
		assertTrue(customObserver.isOverloaded());
	}

	@Test
	public void testScaleOutOfOverload() {
		ItemStub dummyOverloadItem = new ItemStub(50001);
		Station.baggingArea.add(dummyOverloadItem);
		Station.baggingArea.remove(dummyOverloadItem);
		assertTrue(!customObserver.isOverloaded());
	}
}
