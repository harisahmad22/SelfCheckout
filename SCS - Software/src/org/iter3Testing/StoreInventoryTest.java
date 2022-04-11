package org.iter3Testing;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.controlSoftware.data.NegativeNumberException;
import org.driver.databases.PLUTestProducts;
import org.driver.databases.StoreInventory;
import org.driver.databases.TestBarcodedProducts;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class StoreInventoryTest {

	private TestBarcodedProducts barcodedTestProducts;
	private PLUTestProducts PLUTestProducts;
	private StoreInventory storeInventory;
	private PLUCodedProduct PLUproduct;
	private BarcodedProduct barcodeProduct;

	@Before
	public void setup() {
		barcodedTestProducts = new TestBarcodedProducts();
		PLUTestProducts = new PLUTestProducts();
		storeInventory = new StoreInventory(PLUTestProducts, 3, barcodedTestProducts, 5);

		Barcode barcode = new Barcode(new Numeral[] { Numeral.four });
		BigDecimal barcodePrice = new BigDecimal("3.79");

		PriceLookupCode PLUCode = new PriceLookupCode("4567");
		BigDecimal PLUprice = new BigDecimal("1.79");

		PLUproduct = new PLUCodedProduct(PLUCode, "Apricot-0.05kg", PLUprice);
		barcodeProduct = new BarcodedProduct(barcode, "Apple Juice 1L", barcodePrice, 1000);
	}

	@Test
	public void testAddPLUProductsToInventory() {
		storeInventory.addPLUProductsToInventory(PLUproduct, 1);
		assertTrue(storeInventory.getDatabase().get(PLUproduct) == 1);
	}

	@Test
	public void testAddBarcodeProductsToInventory() {
		storeInventory.addBarcodeProductsToInventory(barcodeProduct, 1);
		assertTrue(storeInventory.getDatabase().get(barcodeProduct) == 1);
	}
	
	@Test
	public void testUpdatePLUProductQuantity() {
		storeInventory.addPLUProductsToInventory(PLUproduct, 3);
		storeInventory.updatePLUProductQuantity(PLUproduct, 2);
		assertTrue(storeInventory.getDatabase().get(PLUproduct) == 1);
	}

	@Test(expected = NegativeNumberException.class)
	public void testUpdatePLUProductQuantityNegative() {
		storeInventory.addPLUProductsToInventory(PLUproduct, 1);
		storeInventory.updatePLUProductQuantity(PLUproduct, 2);
	}

	@Test
	public void testUpdateBarcodeProductsQuantity() {
		storeInventory.addBarcodeProductsToInventory(barcodeProduct, 3);
		storeInventory.updateBarcodeProductsQuantity(barcodeProduct, 2);
		assertTrue(storeInventory.getDatabase().get(barcodeProduct) == 1);
	}

	@Test(expected = NegativeNumberException.class)
	public void testUpdateBarcodeProductsQuantityNegative() {
		storeInventory.addPLUProductsToInventory(PLUproduct, 1);
		storeInventory.updatePLUProductQuantity(PLUproduct, 2);
	}

}
