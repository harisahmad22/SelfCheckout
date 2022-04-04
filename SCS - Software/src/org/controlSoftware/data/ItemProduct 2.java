//Brody Long - 30022870

package org.controlSoftware.data;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class ItemProduct {
	
	/*
	 * Wrapper class for BarcodedItem and BarcodedProduct
	 * Combines the two into its own object, so that we can 
	 * easily retrieve both the weight and associated price of
	 * an item. A given item/product combo may have different barcodes. 
	 */
	
	private BarcodedItem item;
	private BarcodedProduct product;
	
	public ItemProduct(BarcodedItem item, BarcodedProduct product)
	{
		this.item = item;
		this.product = product;
	}

	public BarcodedProduct getProduct() {
		return product;
	}

	public BarcodedItem getItem() {
		return item;
	}
	
	public BigDecimal getPrice() {
		return product.getPrice();
	}

	public double getWeight() {
		return item.getWeight();
	}
	
	public Barcode getItemBarcode() {
		return item.getBarcode();
	}
	
	public Barcode getProductBarcode() {
		return product.getBarcode();
	}
	
	public String getProductDescription() {
		return product.getDescription();
	}
	
	public boolean isPerUnit() {
		return product.isPerUnit();
	}
}
