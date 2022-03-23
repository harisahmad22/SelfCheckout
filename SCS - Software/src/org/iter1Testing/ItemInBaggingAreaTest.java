//Shufan Zhai - 30117333

package org.iter1Testing;

import static org.junit.Assert.assertTrue;

import org.controlSoftware.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

@RunWith(JUnit4.class)
public class ItemInBaggingAreaTest {
	
	private SelfCheckoutStation Station;
	private TouchScreen touchScreen;
	private Checkout checkout;
	private ItemInBaggingArea customObserver; // changed from Object to ItemInBaggingArea
	private ProcessScannedItem customScannerObserver;
	private DummyBarcodeLookup lookup;
	private DummyItemProducts itemProducts;

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
		//Initialize a new custom Barcode scanner observer
		this.customScannerObserver = new ProcessScannedItem(this.Station.scanner,
															this.lookup, 
															this.Station.scale, 
															touchScreen);
		//Attach the scanner observer to the scanner
		this.Station.scanner.attach(customScannerObserver);
		
		//Initialize a new custom scale observer
		this.customObserver = new ItemInBaggingArea(this.Station.scale, 
													this.customScannerObserver, 
													touchScreen, 
													checkout);
		
		//Attach the custom observer to the relevant device
		this.Station.scale.attach((ElectronicScaleObserver) customObserver);

	}

    	@Test
	public void testScaleOverloaded() {
		ItemStub dummyOverloadItem = new ItemStub(50001);
		Station.scale.add(dummyOverloadItem);
		assertTrue(customObserver.isOverloaded());
	}

	@Test
	public void testScaleOutOfOverload() {
		ItemStub dummyOverloadItem = new ItemStub(50001);
		Station.scale.add(dummyOverloadItem);
		Station.scale.remove(dummyOverloadItem);
		assertTrue(!customObserver.isOverloaded());
	}
}
