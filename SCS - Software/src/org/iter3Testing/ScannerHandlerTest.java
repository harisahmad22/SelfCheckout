//Brody Long - 30022870 

package org.iter3Testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.controlSoftware.*;
import org.controlSoftware.customer.CheckoutHandler;
import org.controlSoftware.deviceHandlers.BaggingAreaScaleHandler;
import org.controlSoftware.deviceHandlers.ScannerHandler;
import org.controlSoftware.deviceHandlers.membership.ScansMembershipCard;
import org.controlSoftware.deviceHandlers.ReceiptHandler;
import org.controlSoftware.general.TouchScreenSoftware;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutSoftware;
import org.driver.SelfCheckoutStationUnit;
import org.driver.databases.TestBarcodedProducts;
import org.iter2Testing.PlaceItemOnScaleRunnable;
import org.iter2Testing.RemoveItemOnScaleRunnable;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.BarcodeScannerObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;
import org.lsmr.selfcheckout.products.BarcodedProduct;

@RunWith(JUnit4.class)
public class ScannerHandlerTest {
	
	private BarcodedProduct milkJug;
	private BarcodedProduct orangeJuice;
	private BarcodedProduct cornFlakes;
	private BarcodedItem milkJugItem;
	private BarcodedItem orangeJuiceItem;
	private BarcodedItem cornFlakesItem;
	
	private ReceiptHandler receiptHandler;
	private ScansMembershipCard customMembershipScannerObserver;
	
	private SelfCheckoutStationUnit stationUnit;
	private SelfCheckoutStation stationHardware;
	private SelfCheckoutData stationData;
	private SelfCheckoutSoftware stationSoftware;
	private TouchScreenSoftware touchScreenSoftware;
	private TestBarcodedProducts testProducts;
	
	private ScheduledExecutorService addItemsToScaleScheduler;
	

	//Initialize
	@Before
	public void setup() {
//===============================================================================		
		this.stationUnit = new SelfCheckoutStationUnit(1);
		
		this.stationHardware = stationUnit.getSelfCheckoutStationHardware();
		this.stationData = stationUnit.getSelfCheckoutData();
		this.stationSoftware = stationUnit.getSelfCheckoutSoftware();
		this.touchScreenSoftware = stationUnit.getTouchScreenSoftware();
		
		
		//Create some test products/items
		this.testProducts = new TestBarcodedProducts();
		
		milkJug = stationData.getBarcodedProductDatabase()
					.get(testProducts.getBarcodeList().get(0));
		milkJugItem = testProducts.getItem(milkJug);
		
		orangeJuice = stationData.getBarcodedProductDatabase()
				.get(testProducts.getBarcodeList().get(1));
		orangeJuiceItem = testProducts.getItem(orangeJuice);
		
		cornFlakes = stationData.getBarcodedProductDatabase()
				.get(testProducts.getBarcodeList().get(2));
		cornFlakesItem = testProducts.getItem(cornFlakes);
		
		
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
	public void testScannerDisabled() {
		this.stationHardware.mainScanner.disable();
		this.stationHardware.mainScanner.scan(milkJugItem); 
		assertTrue(stationData.getTotalDue().compareTo(BigDecimal.ZERO) == 0);
	}

    @Test
    public void testScannerEnabledAfterDisable() {
    	
    	this.stationHardware.mainScanner.disable();
    	this.stationHardware.mainScanner.enable();
    	
    	//Put a 4L milk jug on the scale 1 second after scanning  
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.stationHardware.baggingArea, milkJugItem), 1000, TimeUnit.MILLISECONDS);
		
    	this.stationHardware.mainScanner.scan(milkJugItem); 
		
    	//Total cost should equal the cost of a milk jug 
		assertTrue(stationData.getTotalDue().compareTo(milkJug.getPrice()) == 0);
		
		//Re-initialize the station, touchscreen, and checkout totals.
		////resetState();
    }
    
    

    @Test
    public void testScanValidItemPutValidWeight() {
    	
    	//Put a 4L milk jug on the scale 1 second after scanning
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.stationHardware.baggingArea, milkJugItem), 1000, TimeUnit.MILLISECONDS);
    	
    	//Scan in a 4L jug of milk (Barcode = 1)
    	this.stationHardware.mainScanner.scan(milkJugItem); 
    	
    	//Touch screen should have been informed of the invalid barcode
    	assertFalse(stationSoftware.getTouchScreenSoftware().invalidBarcodeDetected.get());
    	//Total cost should equal the cost of a milk jug 
    	assertTrue(stationData.getTotalDue().compareTo(milkJug.getPrice()) == 0);
		//Re-initialize the station, touchscreen, and checkout totals.
    	//resetState();
    	
    }
    
	@Test
    public void testScanValidItemPutInvalidWeight() {
    	//System will detect invalid weight, inform touch screen and wait until weight is valid.
    	
    	//Schedule the wrong item to be put down after 0.5 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.stationHardware.baggingArea, cornFlakesItem), 1000, TimeUnit.MILLISECONDS);
    	
    	//Schedule the wrong item to be removed after 3.5 seconds 
    	addItemsToScaleScheduler.schedule(new RemoveItemOnScaleRunnable(this.stationHardware.baggingArea, cornFlakesItem), 3500, TimeUnit.MILLISECONDS);
    	
    	//Schedule the correct item to be put down after 4 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.stationHardware.baggingArea, milkJugItem), 4000, TimeUnit.MILLISECONDS);
    	
    	
    	//Scan in a 4L jug of milk (Barcode = 1)
    	this.stationHardware.mainScanner.scan(milkJugItem); 
    	
    	//After 3 seconds observer will check that item has been put down
    	//It will detect that the wrong Item was put down
    	assertTrue(stationSoftware.getTouchScreenSoftware().scanWeightIssueDetected.get());
    	
    	//After 4 seconds observer will detect the weight has been corrected
    	assertTrue(stationSoftware.getTouchScreenSoftware().scanWeightIssueCorrected.get());
    	//Total cost should equal the cost of a milk jug 
    	assertTrue(stationData.getTotalDue().compareTo(milkJug.getPrice()) == 0);
		//Re-initialize the station, touchscreen, and checkout totals.
    	//resetState();
    }
	
	@Test
    public void testScanValidItemRemoveItemInBaggingArea() {
		//First Scan in some corn flakes, then just after scanning in a milk jug we take the corn flakes off the scale
		//System will detect invalid weight, inform touch screen and wait until weight is valid. (Corn flakes + milk put on scale)
    	
		//Schedule the item to be put down after 1 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.stationHardware.baggingArea, cornFlakesItem), 1000, TimeUnit.MILLISECONDS);
    	
		this.stationHardware.handheldScanner.scan(cornFlakesItem);
		
		//Schedule this item to be removed after 1.0 seconds 
    	addItemsToScaleScheduler.schedule(new RemoveItemOnScaleRunnable(this.stationHardware.baggingArea, cornFlakesItem), 1000, TimeUnit.MILLISECONDS);		
    	
    	//Schedule the item to be put back down after 2.5 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.stationHardware.baggingArea, cornFlakesItem), 3500, TimeUnit.MILLISECONDS);
    	
    	//Schedule the correct item to be put down after 4 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.stationHardware.baggingArea, milkJugItem), 5000, TimeUnit.MILLISECONDS);
    	
    	
    	//Scan in a 4L jug of milk (Barcode = 1)
    	this.stationHardware.mainScanner.scan(milkJugItem); 
    	
    	//After 3 seconds observer will check that item has been put down
    	//It will detect that the wrong Item was put down
    	assertTrue(stationSoftware.getTouchScreenSoftware().scanWeightIssueDetected.get());
    	
    	//After 4 seconds observer will detect the weight has been corrected
    	assertTrue(stationSoftware.getTouchScreenSoftware().scanWeightIssueCorrected.get());
    	//Total cost should equal the cost of a milk jug 
    	assertTrue(stationData.getTotalDue().compareTo(cornFlakes.getPrice().add(milkJug.getPrice())) == 0);
		//Re-initialize the station, touchscreen, and checkout totals.
    	//resetState();
    }
    
    @Test
    public void testScanValidItemWaitToPutValidWeight() {
    	//System will detect valid weight after notifying user to put item in bagging area
    	//will inform touch screen and wait until item is put down
    	
    	//Schedule the wrong item to be put down after 3.5 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.stationHardware.baggingArea, cornFlakesItem), 3500, TimeUnit.MILLISECONDS);    	
    	
    	//Scan in a 4L jug of milk (Barcode = 1)
    	this.stationHardware.mainScanner.scan(cornFlakesItem); 
    	
    	//After 3 seconds observer will check that item has been put down
    	//It will detect that no Item was put down, and inform touch screen 
    	assertTrue(stationSoftware.getTouchScreenSoftware().waitingForItemAfterScanDetected.get());
    	
    	//After 4 seconds observer will detect that the item has been put down 
    	assertTrue(stationSoftware.getTouchScreenSoftware().waitingForItemAfterScanCorrected.get());
    	//Total cost should equal the cost of a milk jug 
    	assertTrue(stationData.getTotalDue().compareTo(cornFlakes.getPrice()) == 0);
		//Re-initialize the station, touchscreen, and checkout totals.
    	//resetState();
    }
    
    @Test
    public void testScanValidItemWaitToPutInvalidWeight() {
    	//System will detect invalid weight after notifying user to put item in bagging area,
    	//inform touch screen and wait until weight is valid.
    	
    	//Schedule the wrong item to be put down after 3.5 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.stationHardware.baggingArea, cornFlakesItem), 3500, TimeUnit.MILLISECONDS);
    	
    	//Schedule the wrong item to be removed after 4 seconds 
    	addItemsToScaleScheduler.schedule(new RemoveItemOnScaleRunnable(this.stationHardware.baggingArea, cornFlakesItem), 4000, TimeUnit.MILLISECONDS);
    	
    	//Schedule the correct item to be put down after 4.5 seconds 
    	addItemsToScaleScheduler.schedule(new PlaceItemOnScaleRunnable(this.stationHardware.baggingArea, milkJugItem), 4500, TimeUnit.MILLISECONDS);
    	
    	
    	//Scan in a 4L jug of milk (Barcode = 1)
    	this.stationHardware.mainScanner.scan(milkJugItem); 
    	
       	//After 3 seconds scale will check that item has been put down
    	//It will detect that no Item was put down, and inform touch screen 
    	assertTrue(stationSoftware.getTouchScreenSoftware().waitingForItemAfterScanDetected.get());
    	
    	//After 3.5 seconds observer will check that item has been put down
    	//It will detect that the wrong Item was put down and continue to block
    	//After 4 seconds the wrong item is removed
    	//After 4.5 seconds observer will detect that the item has been put down 
    	assertTrue(stationSoftware.getTouchScreenSoftware().waitingForItemAfterScanCorrected.get());
    	//Total cost should equal the cost of a milk jug 
    	assertTrue(stationData.getTotalDue().compareTo(milkJug.getPrice()) == 0);
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
    	this.stationHardware.mainScanner.scan(invalidItem);
    	
    	//Invalid item should be detected
    	assertTrue(stationSoftware.getTouchScreenSoftware().invalidBarcodeDetected.get());
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
    	this.stationHardware.mainScanner.scan(invalidItem);
    	
    	//total cost in checkout remains the same
    	assertTrue(stationData.getTotalDue().compareTo(invalidProduct.getPrice()) < 0);
		//Re-initialize the station, touchscreen, and checkout totals.
    	//resetState();
    }
 
    @After
    public void resetState() {
    	this.touchScreenSoftware = new TouchScreenSoftware(System.in, this.stationUnit.getTouchScreen(), stationData);
    	stationData.resetCheckoutTotals();
	}
}
