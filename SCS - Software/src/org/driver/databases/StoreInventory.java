package org.driver.databases;

import java.util.Map;

import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

public class StoreInventory {
	
	private Map<Product, Integer> database;

	public StoreInventory()
	{
		this.database = ProductDatabases.INVENTORY;
		
		//Populate the database with some products and their PLU codes
//		database.put(null, null)
	}
	
	public Map<Product, Integer> getDatabase()
	{
		return database;
	}
	
}
