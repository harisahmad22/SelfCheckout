//Brody Long - 30022870 

package org.testing;

import java.util.ArrayList;

import org.controlSoftware.ItemProduct;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Numeral;

public class DummyItemProducts {
	
	public ArrayList<Barcode> BarcodeList = new ArrayList<Barcode>();
	
	public ArrayList<ItemProduct> IPList = new ArrayList<ItemProduct>();
	
	public DummyItemProducts()
	{
		// ItemProduct 1
		Barcode barcode1 = new Barcode(new Numeral[] {Numeral.one});
		ItemProduct IP1 = DummyBarcodeLookup.createItemProduct(barcode1,
				4000,
				"4L Milk Jug.",
				4.89);
		BarcodeList.add(barcode1);
		IPList.add(IP1);
		// ItemProduct 1
		
		// ItemProduct 2
		Barcode barcode2 = new Barcode(new Numeral[] {Numeral.two});
		ItemProduct IP2 = DummyBarcodeLookup.createItemProduct(barcode2,
				1500,
				"1.5L Orange Juice.",
				3.75);
		BarcodeList.add(barcode2);
		IPList.add(IP2);
		// ItemProduct 2
		
		// ItemProduct 3
		Barcode barcode3 = new Barcode(new Numeral[] {Numeral.three});
		ItemProduct IP3 = DummyBarcodeLookup.createItemProduct(barcode3,
				750,
				"Corn Flakes Cereal",
				2.99);
		BarcodeList.add(barcode3);
		IPList.add(IP3);
		// ItemProduct 3
	//===============================================================================

	}
}
