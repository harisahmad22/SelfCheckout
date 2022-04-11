//Brody Long - 30022870 

package org.controlSoftware.deviceHandlers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.controlSoftware.customer.CheckoutHandler;
import org.controlSoftware.general.TouchScreenSoftware;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutSoftware;
import org.driver.SelfCheckoutData.StationState;
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
	/*	
		Assuming there is a HashMap that acts as a dictionary for all the items that can be scanned via barcode.
		
		1) We are given a barcode from the barcode scanner
		
		2) Lookup Barcode in BarcodedProduct dictionary to get PRICE
			- Hashmap with its key = BARCODE and value = ItemProduct
				- ItemProduct is a wrapper class that combines BarcodedItem and BarcodedProduct objects into one
			- If barcode cannot be found in dictionary then notify the user via the touch screen and stop
		
		3) Get the price from BarcodedProduct
		
		4) Add product to stationData's hashmap
		
		5) Done
	 */
	private ReceiptHandler receiptHandler;
	private SelfCheckoutData stationData;
	private SelfCheckoutSoftware stationSoftware;

	public ScannerHandler(SelfCheckoutData stationData, SelfCheckoutSoftware stationSoftware) {
		this.stationData = stationData;
		this.stationSoftware = stationSoftware;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}
	@Override
	public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
		stationData.changeState(StationState.PROCESSING_SCAN);
		
		// Lookup Barcode in out lookup
		BarcodedProduct scannedProduct = stationData.getBarcodedProductDatabase().get(barcode);
		if (scannedProduct != null)
		{ //Item found in lookup, proceed
			System.out.println(scannedProduct.getDescription() + " has just been scanned in!");
			BigDecimal scannedItemPrice = scannedProduct.getPrice();
			double scannedItemWeight = scannedProduct.getExpectedWeight();
			double scannedItemWeightInKG = scannedItemWeight/1000; // Convert grams to KG
			
			
			if (scannedProduct.isPerUnit()) 
			{
				//Item is priced per unit, since only one item can be scanned at once, just 
				//add the price of this item to the customer's total
				
				stationData.addToTotalCost(scannedItemPrice);
			}

			//Update expected weight 
			stationData.setExpectedWeight(stationData.getExpectedWeight() + scannedItemWeight);
			
			if (stationData.getMidPaymentFlag())
			{ //Mid payment, we need to update transaction payment amount to account for the cost of the new item
			 //Even if the user chose partial payment, make them pay for the just added item
				stationData.addToTransactionPaymentAmount(scannedItemPrice);
			}
			
			//Add product info to the receipt handler list, will change system state to WAITING_FOR_ITEM
			stationData.addProductToCheckout(scannedProduct);
		}
		else
		{
			//Item not found in lookup
			//Report Error to touchscreen
			System.out.println("Informing Touch Screen of invalid barcode.");
			stationSoftware.getTouchScreenSoftware().invalidBarcodeScanned();
			stationData.changeState(StationState.NORMAL); 
			return;
		}
	}
}
