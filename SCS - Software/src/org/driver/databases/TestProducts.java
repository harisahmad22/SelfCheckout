//Brody Long - 30022870 

package org.driver.databases;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class TestProducts {
	
	private ArrayList<Barcode> BarcodeList = new ArrayList<Barcode>();

	private ArrayList<BarcodedProduct> BPList = new ArrayList<BarcodedProduct>();
	
	public TestProducts()
	{
		// Product 1
		Barcode barcode1 = new Barcode(new Numeral[] {Numeral.one});
		BarcodedProduct P1 = new BarcodedProduct(
				barcode1,
				"4L Milk Jug.",
				new BigDecimal("4.89"),
				4000.0);
		BarcodeList.add(barcode1);
		BPList.add(P1);
		// Product 1
		
		// Product 2
		Barcode barcode2 = new Barcode(new Numeral[] {Numeral.two});
		BarcodedProduct P2 = new BarcodedProduct(
				barcode2,
				"1.5L Orange Juice.",
				new BigDecimal("3.75"),
				1500.0);
		BarcodeList.add(barcode2);
		BPList.add(P2);
		// Product 2
		
		// Product 3
		Barcode barcode3 = new Barcode(new Numeral[] {Numeral.three});
		BarcodedProduct P3 = new BarcodedProduct(
				barcode3,
				"Corn Flakes Cereal",
				new BigDecimal("4.00"),
				750.0);
		BarcodeList.add(barcode3);
		BPList.add(P3);
		// ItemProduct 3
	//===============================================================================

	}
	
	//For quickly creating an item based off of the corresponding barcoded product
	public BarcodedItem getItem(BarcodedProduct product)
	{
		return new BarcodedItem(product.getBarcode(), product.getExpectedWeight());
	}
	
	public ArrayList<Barcode> getBarcodeList() {
		return BarcodeList;
	}

	public ArrayList<BarcodedProduct> getBarcodedProductList() {
		return BPList;
	}
}
