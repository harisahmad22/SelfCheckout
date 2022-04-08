package org.driver.databases;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class PLUProductDatabase {
	private Map<PriceLookupCode, PLUCodedProduct> database;

	public PLUProductDatabase() {
		this.database = ProductDatabases.PLU_PRODUCT_DATABASE;

		// Populate the database with some products and their PLU codes
//		database.put(null, null)
	}

	public PLUProductDatabase(ArrayList<PLUCodedProduct> objects) {
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

	public PLUCodedProduct getPLUProductFromDatabase(PriceLookupCode pluCode) {
		return database.get(pluCode);
	}

	public void addPLUProductToDatabase(PLUCodedProduct product) {
		database.put(product.getPLUCode(), product);
	}

	public void removePLUProductFromDatabase(PriceLookupCode pluCode) {
		database.remove(pluCode);
	}

	// *** don't think this is in the right spot
	// Checks the PLU database for products starting with the specified letter
	// inputed by the customer
	// returns list of products whose first letter matches the inputed letter
	@SuppressWarnings("null")
	public ArrayList<PLUCodedProduct> productSearch(char c) {

		ArrayList<PLUCodedProduct> searchOutcomes = new ArrayList<>();

		for (PLUCodedProduct p : database.values())
			if (p.getDescription().charAt(0) == c) {
				searchOutcomes.add(p);
			}
		return searchOutcomes;
	}
}
