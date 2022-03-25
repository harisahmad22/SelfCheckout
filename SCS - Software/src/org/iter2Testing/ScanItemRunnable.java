//Brody Long - 30022870

package org.iter2Testing;

import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.BarcodeScanner;

public class ScanItemRunnable implements Runnable {

	private BarcodeScanner scanner;
	private BarcodedItem item;
	
	public ScanItemRunnable(BarcodeScanner scanner, BarcodedItem item)
	{
		this.scanner = scanner;
		this.item = item;
	}
	
	@Override
	public void run() {
		scanner.scan(item);
	}

}
