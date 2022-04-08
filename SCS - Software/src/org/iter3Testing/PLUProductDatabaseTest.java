package org.iter3Testing;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.driver.databases.PLUProductDatabase;
import org.driver.databases.PLUTestProducts;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.PriceLookupCode;

public class PLUProductDatabaseTest {

	private PLUProductDatabase PLU_Product_Database;
	private PriceLookupCode PLUCode;
	private String description;
	private BigDecimal price;
	private PLUTestProducts PLUTestProducts;

	@Before
	public void setUp() {
		PLUTestProducts = new PLUTestProducts();
		PLU_Product_Database = new PLUProductDatabase(PLUTestProducts.getPLUProductList());
		PLUCode = new PriceLookupCode("4567");
		description = "Apricot-0.05kg";
		price = new BigDecimal("1.79");
	}

	@Test
	public void testAddPLUProductToDatabase1() {
		PLU_Product_Database.addPLUProductToDatabase(PLUCode, description, price);
		assertTrue(PLU_Product_Database.getDatabase().size() == 4);
	}

	@Test
	public void testAddPLUProductToDatabase2() {
		double price2 = 1.79;
		PLU_Product_Database.addPLUProductToDatabase(PLUCode, description, price2);
		assertTrue(PLU_Product_Database.getDatabase().size() == 4);
	}

	@Test
	public void testGetPLUProductFromDatabase() {
		PLU_Product_Database.addPLUProductToDatabase(PLUCode, description, price);
		assertTrue(PLU_Product_Database.getPLUProductFromDatabase(PLUCode).getDescription() == "Apricot-0.05kg");
	}

	@Test
	public void testProductSearch() {
		// target is Rice-1kg.
		assertTrue(PLU_Product_Database.productSearch('R').contains(PLUTestProducts.getPLUProductList().get(0)));
	}

	@Test
	public void testRemovePLUProductFromDatabase() {
		PLU_Product_Database.addPLUProductToDatabase(PLUCode, description, price);
		PLU_Product_Database.removePLUProductFromDatabase(PLUCode);
		assertTrue(PLU_Product_Database.getDatabase().size() == 3);
	}

}
