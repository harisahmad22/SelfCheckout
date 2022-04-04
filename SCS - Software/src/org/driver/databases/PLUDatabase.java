package org.driver.databases;

import java.util.Map;

import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class PLUDatabase {
	private Map<PriceLookupCode, PLUCodedProduct> database;

	public PLUDatabase()
	{
		this.database = ProductDatabases.PLU_PRODUCT_DATABASE;
		
		//Populate the database with some products and their PLU codes
//		database.put(null, null)
	}
	
	public Map<PriceLookupCode, PLUCodedProduct> getDatabase()
	{
		return database;
	}
	
}
