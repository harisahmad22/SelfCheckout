//Brody Long - 30022870 

package org.controlSoftware.deviceHandlers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.controlSoftware.customer.CheckoutHandler;
import org.controlSoftware.data.BarcodeLookup;
import org.controlSoftware.data.ItemProduct;
import org.controlSoftware.general.TouchScreenSoftware;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutSoftware;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BarcodeScannerObserver;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class ScannerHandler implements BarcodeScannerObserver
{
//	private BarcodeScanner scanner;
//	private BarcodeLookup lookup;
//	private ElectronicScale scale;
//	private TouchScreenSoftware touchScreen;
//	private CheckoutHandler checkout;
//	private double targetWeight;
//	private AtomicBoolean waitingForWeightChangeEvent = new AtomicBoolean(false);
//	private AtomicBoolean weightValid = new AtomicBoolean(false);
//	private double weightTolerance = 15; // The maximum weight in grams that an Item's weight can differ from its weight stored in the ItemProduct dictionary  
	/*	
		Assuming there is a HashMap that acts as a dictionary for all the items that can be scanned via barcode.
		
		1) We are given a barcode from the barcode scanner
		
		2) Lookup Barcode in BarcodedProduct dictionary to get PRICE
			- Hashmap with its key = BARCODE and value = ItemProduct
				- ItemProduct is a wrapper class that combines BarcodedItem and BarcodedProduct objects into one
			- If barcode cannot be found in dictionary then notify the user via the touch screen and stop
		
		3) Get the price from BarcodedProduct
		
		4) Check if the product is priced per unit or by per KG
			- If by unit, just add the price to the customer's current total
			- Otherwise get the weight (in KG) of the item and multiply that by the product's price and add this to the customers total 
			
		5) Wait X number of seconds for some signal from the scale that the customer has placed the item in the bagging area
			- If X number of seconds pass without being signaled by the scale, block scanning and touch screen input until signal is received or employee override
		
		6) Done
	 */
	private ReceiptHandler receiptHandler;
	private SelfCheckoutData stationData;
	private SelfCheckoutSoftware stationSoftware;

	public ScannerHandler(SelfCheckoutData stationData, SelfCheckoutSoftware stationSoftware) {
//			BarcodeScanner scanner, 
//			BarcodeLookup lookup, 
//			ElectronicScale scale, 
//			TouchScreenSoftware touchScreen, 
//			CheckoutHandler checkout, 
//			ReceiptHandler receiptHandler) 
//	{
		this.stationData = stationData;
		this.stationSoftware = stationSoftware;
		
//		this.scanner = scanner;
//		this.lookup = lookup;
//		this.scale = scale;
//		this.touchScreen = touchScreen;
//		this.checkout = checkout;
//		this.receiptHandler = receiptHandler;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}
	@Override
	public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
		stationData.getScanner("main").disable(); //Disable scanning while we process this item
		stationData.getScanner("hand").disable(); //Disable handheld scanning while we process this item
		// Lookup Barcode in out lookup
		BarcodedProduct scannedItem = stationData.getBarcodedProductDatabase().get(barcode);
		if (scannedItem != null)
		{ //Item found in lookup, proceed
			System.out.println(scannedItem.getDescription() + " has just been scanned in!");
			BigDecimal scannedItemPrice = scannedItem.getPrice();
			double scannedItemWeight = scannedItem.getExpectedWeight();
			double scannedItemWeightInKG = scannedItemWeight/1000; // Convert grams to KG
			if (scannedItem.isPerUnit()) 
			{
				//Item is priced per unit, since only one item can be scanned at once, just 
				//add the price of this item to the customer's total
				
				stationData.addToTotalCost(scannedItemPrice);
			}
			else
			{
				//Item is priced per KG, get the weight of item in KG and multiply by price,
				//add this to customers total
				//!!! THIS WILL HAVE TO CHANGE WITH THE ADDITION OF THE SCALE !!!
				stationData.addToTotalCost(scannedItemPrice.multiply(new BigDecimal(scannedItemWeightInKG))); 
			}
			
			//Add product info to the receipt handler list
			stationSoftware.getReceiptHandler().addProductToReceipt(scannedItem.getDescription(), scannedItemPrice.toString());
			
			//Customer's total has been updated, now wait for the scanned item to be placed in the bagging area
			// Not sure if this is the best way to handle it VVV
			try {
				waitForWeightChange(scannedItemWeight);
				stationData.getScanner("main").enable();
				stationData.getScanner("hand").enable(); 
			} catch (OverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Not sure if this is the best way to handle it ^^^
			// Will have to look into possibly having the Scale Observer signal this class to inform it of a weight change 
			// Instead of just sleeping for 5 seconds then re-checking
		}
		else
		{
			//Item not found in lookup
			//Report Error to touchscreen
			System.out.println("Informing Touch Screen of invalid barcode.");
			stationSoftware.getTouchScreenSoftware().invalidBarcodeScanned();
			barcodeScanner.enable(); //Re-enable scanning 
			return;
		}
	}
	
	private void waitForWeightChange(double scannedItemWeight) throws OverloadException, InterruptedException {
		double weightBefore = stationData.getBaggingAreaScale().getCurrentWeight(); // In grams
		stationData.setExpectedWeightCheckout(weightBefore + scannedItemWeight); // What we expect the scale to read after placing the item on it
		
		stationData.setIsScannerWaitingForWeightChange(true); //Signal Scale observer that we are waiting for an weight change after scanning
		
		if (stationData.isInCheckout())
		{
			stationData.setExpectedWeightCheckout(stationData.getExpectedWeightScanner());
		}
		
		//Wait for 3 seconds
		TimeUnit.SECONDS.sleep(3);

		// Check if the weight has increased by approximately the weight of the scanned item since we last checked
		if (stationData.getIsScannerWaitingForWeightChange())
		{	// We are still waiting for a weight change event, signal screen that Item must be put in bagging area
			handleItemNotPlacedInBaggingArea();
			return;
		}
		else
		{	//A weight change event has occurred, check if it is valid (matches approx target weight)
			
			if (stationData.getWeightValidScanner())
			{
				//Weight change is valid with the scanned item, we are done
				
				// Reset weight change flags
				stationData.resetScannerWeightFlags();
				return;
			}
			else
			{
				//Weight change is not valid, need to inform relevant observers and 
				//block input/scanning from user until issue is corrected
				handleInvalidWeight();
				return;
			}
		}
		
	}

	
	private void handleItemNotPlacedInBaggingArea() throws InterruptedException {
		
		stationData.disableScannerDevices();
		stationSoftware.getTouchScreenSoftware().waitingForScannedItem();
		System.out.println("Informing Touch Screen that item has not been placed in bagging area.");
		// Loop until scale observer reports a valid weight
		while (!stationData.getWeightValidScanner())
		{
			stationData.compareAndSetWaitingForWeightChangeEvent(false, true);
//			if (!stationData.getIsScannerWaitingForWeightChange()) 
//			{ stationData.setIsScannerWaitingForWeightChange(true); }
		}
		
		// Weight is now valid, unblock and remove touchscreen message
		stationData.enableScannerDevices();
		stationSoftware.getTouchScreenSoftware().doneWaitingForScannedItem();	
		
		stationData.resetScannerWeightFlags();
	}
	
	private void handleInvalidWeight() throws InterruptedException {
		
		stationData.compareAndSetWaitingForWeightChangeEvent(false, true);
		
		stationData.disableScannerDevices();
		stationSoftware.getTouchScreenSoftware().invalidWeightAfterScan();
		System.out.println("Informing Touch Screen of Invalid weight.");
		// Loop until scale observer reports a valid weight
		while (!stationData.getWeightValidScanner())
		{
			stationData.compareAndSetWaitingForWeightChangeEvent(false, true);
		}
		
		// Weight is now valid, unblock and remove touchscreen message
		stationData.enableScannerDevices();
		stationSoftware.getTouchScreenSoftware().validWeightAfterScan();		
		
		stationData.resetScannerWeightFlags();
	}



//	public double getTargetWeight() {
//		return targetWeight;
//	}
//	public boolean isWaitingForWeightChange() {
//		return waitingForWeightChangeEvent.get();
//	}
//	public void setWaitingForWeightChange(boolean bool) {
//		this.waitingForWeightChangeEvent.set(bool);
//	}
//	public boolean isWeightValid() {
//		return weightValid.get();
//	}
//	public void setWeightValid(boolean bool) {
//		this.weightValid.set(bool);
//	}
	
}
