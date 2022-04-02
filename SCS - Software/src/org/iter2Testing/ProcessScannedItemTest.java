//Brody Long - 30022870 

package org.iter2Testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.controlSoftware.*;
import org.controlSoftware.customer.CheckoutSoftware;
import org.controlSoftware.deviceHandlers.ItemInBaggingArea;
import org.controlSoftware.deviceHandlers.ProcessScannedItem;
import org.controlSoftware.deviceHandlers.ReceiptHandler;
import org.controlSoftware.general.TouchScreenSoftware;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.BarcodeScannerObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;
import org.lsmr.selfcheckout.products.BarcodedProduct;

@RunWith(JUnit4.class)
public class ProcessScannedItemTest {
	
	private SelfCheckoutStation Station;
	private TouchScreenSoftware touchScreen;
	private CheckoutSoftware checkout;
	private ProcessScannedItem customObserver;
	private ItemInBaggingArea customScaleObserver;
	private DummyBarcodeLookup lookup;
	private DummyItemProducts itemProducts;
	
	private BarcodedItem milkJug;
	private BigDecimal milkJugCost;
	private BarcodedItem orangeJuice;
	private BigDecimal orangeJuiceCost;
	private BarcodedItem cornFlakes;
	private BigDecimal cornFlakesCost;
	private ScheduledExecutorService addItemsToScaleScheduler;
	private ReceiptHandler receiptHandler;
	

	//Initialize
	@Before
	public void setup() {
//===============================================================================		
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
		
		//Get some barcoded items with their prices
		milkJug = lookup.get(itemProducts.BarcodeList.get(0)).getItem();
    	milkJugCost = lookup.get(itemProducts.BarcodeList.get(0)).getPrice();
    	orangeJuice = lookup.get(itemProducts.BarcodeList.get(1)).getItem();
    	orangeJuiceCost = lookup.get(itemProducts.BarcodeList.get(1)).getPrice();
    	cornFlakes = lookup.get(itemProducts.BarcodeList.get(2)).getItem();
    	cornFlakesCost = lookup.get(itemProducts.BarcodeList.get(2)).getPrice();
		
		//Initialize a new custom Barcode scanner observer
		this.customObserver = new ProcessScannedItem(this.Station.mainScanner,
													 this.lookup, 
													 this.Station.baggingArea, 
													 touchScreen,
													 checkout,
													 this.receiptHandler); 
		//Attach the custom observer to the relevant device
		this.Station.mainScanner.attach((BarcodeScannerObserver) customObserver);
		this.Station.handheldScanner.attach((BarcodeScannerObserver) customObserver);
		
		//Initialize a new custom scale observer
		this.customScaleObserver = new ItemInBaggingArea(this.Station.baggingArea, 
				   										 this.customObserver, 
				   										 touchScreen, 
				   										 checkout);
		//Attach the scanner observer to the scanner
		this.Station.baggingArea.attach((ElectronicScaleObserver) customScaleObserver);
		
		addItemsToScaleScheduler =  Executors.newScheduledThreadPool(1);
	}

	@Test
	public void testScannerDisabled() {
		this.Station.mainScanner.disable();
		this.Station.mainScanner.scan(milkJug); 
		assertTrue(checkout.getTotalCost().compareTo(BigDecimal.ZERO) == 0);
	}

    @Test
    public void testScannerEnabledAfterDisable() {
    	
    	this.Station.mainScanner.disable();
    	this.Station.mainScanner.enable();
    	
    	//Put a 4L milk jug on the scale 1 second after scanning  
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, milkJug), 1000, TimeUnit.MILLISECONDS);
		
    	this.Station.mainScanner.scan(milkJug); 
		
    	//Total cost should equal the cost of a milk jug 
		assertTrue(checkout.getTotalCost().compareTo(milkJugCost) == 0);
		
		//Re-initialize the station, touchscreen, and checkout totals.
		////resetState();
    }
    
    

    @Test
    public void testScanValidItemPutValidWeight() {
    	
    	//Put a 4L milk jug on the scale 1 second after scanning
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, milkJug), 1000, TimeUnit.MILLISECONDS);
    	
    	//Scan in a 4L jug of milk (Barcode = 1)
    	this.Station.mainScanner.scan(milkJug); 
    	
    	//Touch screen should have been informed of the invalid barcode
    	assertFalse(touchScreen.invalidBarcodeDetected.get());
    	//Total cost should equal the cost of a milk jug 
    	assertTrue(checkout.getTotalCost().compareTo(milkJugCost) == 0);
		//Re-initialize the station, touchscreen, and checkout totals.
    	//resetState();
    	
    }
    
	@Test
    public void testScanValidItemPutInvalidWeight() {
    	//System will detect invalid weight, inform touch screen and wait until weight is valid.
    	
    	//Schedule the wrong item to be put down after 0.5 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, cornFlakes), 1000, TimeUnit.MILLISECONDS);
    	
    	//Schedule the wrong item to be removed after 3.5 seconds 
    	addItemsToScaleScheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.baggingArea, cornFlakes), 3500, TimeUnit.MILLISECONDS);
    	
    	//Schedule the correct item to be put down after 4 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, milkJug), 4000, TimeUnit.MILLISECONDS);
    	
    	
    	//Scan in a 4L jug of milk (Barcode = 1)
    	this.Station.mainScanner.scan(milkJug); 
    	
    	//After 3 seconds observer will check that item has been put down
    	//It will detect that the wrong Item was put down
    	assertTrue(touchScreen.scanWeightIssueDetected.get());
    	
    	//After 4 seconds observer will detect the weight has been corrected
    	assertTrue(touchScreen.scanWeightIssueCorrected.get());
    	//Total cost should equal the cost of a milk jug 
    	assertTrue(checkout.getTotalCost().compareTo(milkJugCost) == 0);
		//Re-initialize the station, touchscreen, and checkout totals.
    	//resetState();
    }
	
	@Test
    public void testScanValidItemRemoveItemInBaggingArea() {
		//First Scan in some corn flakes, then just after scanning in a milk jug we take the corn flakes off the scale
		//System will detect invalid weight, inform touch screen and wait until weight is valid. (Corn flakes + milk put on scale)
    	
		//Schedule the item to be put down after 1 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, cornFlakes), 1000, TimeUnit.MILLISECONDS);
    	
		this.Station.handheldScanner.scan(cornFlakes);
		
		//Schedule this item to be removed after 1.0 seconds 
    	addItemsToScaleScheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.baggingArea, cornFlakes), 1000, TimeUnit.MILLISECONDS);		
    	
    	//Schedule the item to be put back down after 2.5 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, cornFlakes), 3500, TimeUnit.MILLISECONDS);
    	
    	//Schedule the correct item to be put down after 4 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, milkJug), 5000, TimeUnit.MILLISECONDS);
    	
    	
    	//Scan in a 4L jug of milk (Barcode = 1)
    	this.Station.mainScanner.scan(milkJug); 
    	
    	//After 3 seconds observer will check that item has been put down
    	//It will detect that the wrong Item was put down
    	assertTrue(touchScreen.scanWeightIssueDetected.get());
    	
    	//After 4 seconds observer will detect the weight has been corrected
    	assertTrue(touchScreen.scanWeightIssueCorrected.get());
    	//Total cost should equal the cost of a milk jug 
    	assertTrue(checkout.getTotalCost().compareTo(cornFlakesCost.add(milkJugCost)) == 0);
		//Re-initialize the station, touchscreen, and checkout totals.
    	//resetState();
    }
    
    @Test
    public void testScanValidItemWaitToPutValidWeight() {
    	//System will detect valid weight after notifying user to put item in bagging area
    	//will inform touch screen and wait until item is put down
    	
    	//Schedule the wrong item to be put down after 3.5 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, cornFlakes), 3500, TimeUnit.MILLISECONDS);    	
    	
    	//Scan in a 4L jug of milk (Barcode = 1)
    	this.Station.mainScanner.scan(cornFlakes); 
    	
    	//After 3 seconds observer will check that item has been put down
    	//It will detect that no Item was put down, and inform touch screen 
    	assertTrue(touchScreen.waitingForItemAfterScanDetected.get());
    	
    	//After 4 seconds observer will detect that the item has been put down 
    	assertTrue(touchScreen.waitingForItemAfterScanCorrected.get());
    	//Total cost should equal the cost of a milk jug 
    	assertTrue(checkout.getTotalCost().compareTo(cornFlakesCost) == 0);
		//Re-initialize the station, touchscreen, and checkout totals.
    	//resetState();
    }
    
    @Test
    public void testScanValidItemWaitToPutInvalidWeight() {
    	//System will detect invalid weight after notifying user to put item in bagging area,
    	//inform touch screen and wait until weight is valid.
    	
    	//Schedule the wrong item to be put down after 3.5 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, cornFlakes), 3500, TimeUnit.MILLISECONDS);
    	
    	//Schedule the wrong item to be removed after 4 seconds 
    	addItemsToScaleScheduler.schedule(new RemoveItemOnScaleRunnable(this.Station.baggingArea, cornFlakes), 4000, TimeUnit.MILLISECONDS);
    	
    	//Schedule the correct item to be put down after 4.5 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.Station.baggingArea, milkJug), 4500, TimeUnit.MILLISECONDS);
    	
    	
    	//Scan in a 4L jug of milk (Barcode = 1)
    	this.Station.mainScanner.scan(milkJug); 
    	
       	//After 3 seconds scale will check that item has been put down
    	//It will detect that no Item was put down, and inform touch screen 
    	assertTrue(touchScreen.waitingForItemAfterScanDetected.get());
    	
    	//After 3.5 seconds observer will check that item has been put down
    	//It will detect that the wrong Item was put down and continue to block
    	//After 4 seconds the wrong item is removed
    	//After 4.5 seconds observer will detect that the item has been put down 
    	assertTrue(touchScreen.waitingForItemAfterScanCorrected.get());
    	//Total cost should equal the cost of a milk jug 
    	assertTrue(checkout.getTotalCost().compareTo(milkJugCost) == 0);
		//Re-initialize the station, touchscreen, and checkout totals.
    	//resetState();
    }
    
    @Test
    public void testScanInvalidItem() {
    	//Create a barcode 123 for the invalid item
    	Barcode invalidBarcode = new Barcode(new Numeral[] {Numeral.one, Numeral.two, Numeral.three});
    	//Weight of 500g
    	BarcodedItem invalidItem = new BarcodedItem(invalidBarcode, 500);
    	//Worth $10
    	BarcodedProduct invalidProduct = new BarcodedProduct(invalidBarcode, "Invalid Item", new BigDecimal(10), 500);
    	
    	//Try scanning the invalid item
    	this.Station.mainScanner.scan(invalidItem);
    	
    	//Invalid item should be detected
    	assertTrue(touchScreen.invalidBarcodeDetected.get());
		//Re-initialize the station, touchscreen, and checkout totals.
    	//resetState();
    	
    }
    

    @Test
    public void testTotalCostNotIncreasedAfterInvalidScan() {	
    	//Create a barcode 123 for the invalid item
    	Barcode invalidBarcode = new Barcode(new Numeral[] {Numeral.one, Numeral.two, Numeral.three});
    	//Weight of 500g
    	BarcodedItem invalidItem = new BarcodedItem(invalidBarcode, 500);
    	//Worth $10
    	BarcodedProduct invalidProduct = new BarcodedProduct(invalidBarcode, "Invalid Item", new BigDecimal(10), 500);
    	
    	//Try scanning the invalid item
    	this.Station.mainScanner.scan(invalidItem);
    	
    	//total cost in checkout remains the same
    	assertTrue(checkout.getTotalCost().compareTo(invalidProduct.getPrice()) < 0);
		//Re-initialize the station, touchscreen, and checkout totals.
    	//resetState();
    }
 
    @After
    public void resetState() {
    	this.Station = new DummySelfCheckoutStation();
    	this.touchScreen = new TouchScreenSoftware(System.in);
    	CheckoutSoftware.resetCheckoutTotals();
	}
}
