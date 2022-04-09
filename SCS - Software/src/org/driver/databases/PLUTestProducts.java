package org.driver.databases;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class PLUTestProducts {
	private ArrayList<PriceLookupCode> PLUList = new ArrayList<PriceLookupCode>();

	private ArrayList<PLUCodedProduct> PLUProductList = new ArrayList<PLUCodedProduct>();

	public PLUTestProducts() {
		// Product 1
		PriceLookupCode PLUCode1 = new PriceLookupCode("1234");
		PLUCodedProduct P1 = new PLUCodedProduct(PLUCode1, "Rice", new BigDecimal("7.43"));
		PLUList.add(PLUCode1);
		PLUProductList.add(P1);
		// Product 1

		// Product 2
		PriceLookupCode PLUCode2 = new PriceLookupCode("2345");
		PLUCodedProduct P2 = new PLUCodedProduct(PLUCode2, "Pear", new BigDecimal("1.43"));
		PLUList.add(PLUCode2);
		PLUProductList.add(P2);
		// Product 2

		// Product 3
		PriceLookupCode PLUCode3 = new PriceLookupCode("3456");
		PLUCodedProduct P3 = new PLUCodedProduct(PLUCode3, "Banana", new BigDecimal("1.00"));
		PLUList.add(PLUCode3);
		PLUProductList.add(P3);
		// ItemProduct 3
		// ===============================================================================

	}

	// For quickly creating an item based off of the corresponding PLU product
	public PLUCodedItem getItem(PLUCodedProduct product, double weightInGrams) {
		return new PLUCodedItem(product.getPLUCode(), weightInGrams);
	}

	public ArrayList<PriceLookupCode> getPLUList() {
		return PLUList;
	}

	public ArrayList<PLUCodedProduct> getPLUProductList() {
		return PLUProductList;
	}
}
