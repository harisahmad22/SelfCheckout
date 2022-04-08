package org.iter3Testing;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.driver.databases.BarcodedProductDatabase;
import org.driver.databases.BarcodedTestProducts;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class BarcodedProductDatabaseTest {

	private BarcodedProductDatabase Barcoded_Product_Database;
	private Barcode barcode;
	private String description;
	private BigDecimal price;
	private BarcodedTestProducts BarcodedTestProducts;
	private double weight;

	@Before
	public void setUp() {
		BarcodedTestProducts = new BarcodedTestProducts();
		Barcoded_Product_Database = new BarcodedProductDatabase(BarcodedTestProducts.getBarcodedProductList());
		barcode = new Barcode(new Numeral[] { Numeral.four });
		description = "Apple Juice 1L";
		price = new BigDecimal("3.79");
		weight = 1000;
	}

	@Test
	public void testAddBarcodedProductToDatabase() {
		Barcoded_Product_Database.addBarcodedProductToDatabase(barcode, weight, description, price);
		assertTrue(Barcoded_Product_Database.getDatabase().size() == 4);
	}

	@Test
	public void testAddBarcodedProductToDatabase2() {
		double doublePrice = 3.79;
		Barcoded_Product_Database.addBarcodedProductToDatabase(barcode, weight, description, doublePrice);
		assertTrue(Barcoded_Product_Database.getDatabase().size() == 4);
	}

	@Test
	public void testAddBarcodedProductToDatabase3() {
		String stringPrice = "3.79";
		String stringWeight = "1000";
		Barcoded_Product_Database.addBarcodedProductToDatabase(barcode, stringWeight, description, stringPrice);
		assertTrue(Barcoded_Product_Database.getDatabase().size() == 4);
	}

	@Test
	public void testAddBarcodedProductToDatabase4() {
		BarcodedProduct testProduct = new BarcodedProduct(barcode, description, price, weight);
		Barcoded_Product_Database.addBarcodedProductToDatabase(testProduct);
		assertTrue(Barcoded_Product_Database.getDatabase().size() == 4);
	}

	@Test
	public void testGetBarcodedProductFromDatabase() {
		Barcoded_Product_Database.addBarcodedProductToDatabase(barcode, weight, description, price);
		assertTrue(
				Barcoded_Product_Database.getBarcodedProductFromDatabase(barcode).getDescription() == "Apple Juice 1L");
	}

	@Test
	public void testRemoveBarcodedProductFromDatabase() {
		Barcoded_Product_Database.addBarcodedProductToDatabase(barcode, weight, description, price);
		Barcoded_Product_Database.removeBarcodedProductFromDatabase(barcode);
		assertTrue(Barcoded_Product_Database.getDatabase().size() == 3);
	}
}
