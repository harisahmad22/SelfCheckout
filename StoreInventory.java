package org.driver.databases;

import java.util.Map;

import org.controlSoftware.data.NegativeNumberException;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

public class StoreInventory {

	private Map<Product, Integer> database;

	public StoreInventory() {
		this.database = ProductDatabases.INVENTORY;
	}

	// quantity of goods is set to same amount for all products. Need to update
	public StoreInventory(PLUTestProducts PLUInventory, int PLUQuantity, BarcodedTestProducts barcodeInventory,
			int barcodeQuantity) {

		this.database = ProductDatabases.INVENTORY;

		if (!PLUInventory.getPLUProductList().isEmpty() || PLUInventory.getPLUProductList() != null) {
			for (PLUCodedProduct p : PLUInventory.getPLUProductList()) {
				database.put(p, PLUQuantity);
			}
		}

		if (!barcodeInventory.getBarcodedProductList().isEmpty() || barcodeInventory.getBarcodedProductList() != null) {
			for (BarcodedProduct p : barcodeInventory.getBarcodedProductList()) {
				database.put(p, barcodeQuantity);
			}
		}
	}

	public Map<Product, Integer> getDatabase() {
		return database;
	}

	public void addPLUProductsToInventory(PLUCodedProduct p, int quantity) {
		database.put(p, quantity);
	}

	public void addBarcodeProductsToInventory(BarcodedProduct p, int quantity) {
		database.put(p, quantity);
	}

	public void updatePLUProductQuantity(PLUCodedProduct p, int change) {
		Integer oldQuantity = database.get(p);

		if ((oldQuantity - change) >= 0) {
			Integer updatedQuantity = oldQuantity - change;
			database.put(p, updatedQuantity);
		} else {
			throw new NegativeNumberException();
		}
	}

	public void updateBarcodeProductsQuantity(BarcodedProduct p, int change) {
		Integer oldQuantity = database.get(p);

		if ((oldQuantity - change) >= 0) {
			Integer updatedQuantity = oldQuantity - change;
			database.put(p, updatedQuantity);
		} else {
			throw new NegativeNumberException();
		}
	}
}
