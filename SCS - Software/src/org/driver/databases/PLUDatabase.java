package org.driver.databases;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class PLUDatabase {
	private Map<PriceLookupCode, PLUCodedProduct> database;

	public PLUDatabase() {
		this.database = ProductDatabases.PLU_PRODUCT_DATABASE;

		// Populate the database with some products and their PLU codes
//		database.put(null, null)
	}

	public PLUDatabase(ArrayList<PLUCodedProduct> objects) {
		this.database = ProductDatabases.PLU_PRODUCT_DATABASE;

		// Populate the database with some products and their PLU codes
		if (!objects.isEmpty() || objects != null) {
			for (PLUCodedProduct p : objects) {
				database.put(p.getPLUCode(), p);
			}
		}

	}

	public Map<PriceLookupCode, PLUCodedProduct> getDatabase() {
		return database;
	}

	public void addPLUProductToDatabase(PriceLookupCode pluCode, String description, BigDecimal price) {
		PLUCodedProduct product = new PLUCodedProduct(pluCode, description, price);
		database.put(pluCode, product);
	}

	public void addPLUProductToDatabase(PriceLookupCode pluCode, String description, double price) {
		// Convert to a string first to get exact value after BigDecimal conversion
		BigDecimal thisPrice = new BigDecimal(Double.toString(price));
		PLUCodedProduct product = new PLUCodedProduct(pluCode, description, thisPrice);
		database.put(pluCode, product);
	}

	public void addPLUProductToDatabase(PLUCodedProduct product) {
		database.put(product.getPLUCode(), product);
	}

}
