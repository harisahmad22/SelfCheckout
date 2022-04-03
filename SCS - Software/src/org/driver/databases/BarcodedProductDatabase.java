package org.driver.databases;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import org.controlSoftware.data.ItemProduct;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class BarcodedProductDatabase {
	
	private Map<Barcode, BarcodedProduct> database;

	public BarcodedProductDatabase(ArrayList<BarcodedProduct> objects)
	{
		this.database = ProductDatabases.BARCODED_PRODUCT_DATABASE;
		
		//Populate the database with some products and their barcodes 
		if (!objects.isEmpty() || objects != null)
		{	
			for (BarcodedProduct p : objects) { database.put(p.getBarcode(), p); }
		}
		
	}
	
	public Map<Barcode, BarcodedProduct> getDatabase() { return database; }
	
	public void addBarcodedProductToDatabase(Barcode barcode, double weightInGrams, String description, BigDecimal price)
	{
		BarcodedProduct product = new BarcodedProduct(barcode, description, price, weightInGrams);
		database.put(barcode, product);
	}
	
	public void addBarcodedProductToDatabase(Barcode barcode, double weightInGrams, String description, double price)
	{
		//Convert to a string first to get exact value after BigDecimal conversion
		BigDecimal thisPrice = new BigDecimal(Double.toString(price));
		BarcodedProduct product = new BarcodedProduct(barcode, description, thisPrice, weightInGrams);
		database.put(barcode, product);
	}
	
	public void addBarcodedProductToDatabase(Barcode barcode, String weightInGrams, String description, String price)
	{
		double thisWeightInGrams = Double.parseDouble(weightInGrams);
		BigDecimal thisPrice = new BigDecimal(price);
		BarcodedProduct product = new BarcodedProduct(barcode, description, thisPrice, thisWeightInGrams);
		database.put(barcode, product);
	}
	
	public void addBarcodedProductToDatabase(BarcodedProduct product)
	{
		database.put(product.getBarcode(), product);
	}
}
