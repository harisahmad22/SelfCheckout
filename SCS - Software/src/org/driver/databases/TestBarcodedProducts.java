//Brody Long - 30022870 

package org.driver.databases;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class TestBarcodedProducts {
	
	private ArrayList<Barcode> BarcodeList = new ArrayList<Barcode>();

	private ArrayList<BarcodedProduct> BPList = new ArrayList<BarcodedProduct>();
	
	private ArrayList<Item> ItemList = new ArrayList<Item>();
	
	public TestBarcodedProducts()
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
		ItemList.add(getItem(P1));
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
		ItemList.add(getItem(P2));
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
		ItemList.add(getItem(P3));
		// Product 3
		
		// Product 4
		Barcode barcode4 = new Barcode(new Numeral[] {Numeral.four});
		BarcodedProduct P4 = new BarcodedProduct(
				barcode4,
				"Spaghetti",
				new BigDecimal("2.99"),
				600.0);
		BarcodeList.add(barcode4);
		BPList.add(P4);
		ItemList.add(getItem(P4));
		// Product 4
		
		// Product 5
		Barcode barcode5 = new Barcode(new Numeral[] {Numeral.five});
		BarcodedProduct P5 = new BarcodedProduct(
				barcode5,
				"Canned Soup",
				new BigDecimal("0.99"),
				200.0);
		BarcodeList.add(barcode5);
		BPList.add(P5);
		ItemList.add(getItem(P5));
		// Product 5
		
		// Product 6
		Barcode barcode6 = new Barcode(new Numeral[] {Numeral.six});
		BarcodedProduct P6 = new BarcodedProduct(
				barcode6,
				"Ground Beef",
				new BigDecimal("5.00"),
				454.0);
		BarcodeList.add(barcode6);
		BPList.add(P6);
		ItemList.add(getItem(P6));
		// Product 6
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
	
	public ArrayList<Item> getItemList() {
		return ItemList;
	}
}
