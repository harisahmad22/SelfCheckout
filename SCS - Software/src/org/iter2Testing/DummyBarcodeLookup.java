//Brody Long - 30022870 

package org.iter2Testing;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.controlSoftware.data.BarcodeLookup;
import org.controlSoftware.data.ItemProduct;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class DummyBarcodeLookup extends BarcodeLookup{
	
	public DummyBarcodeLookup(ArrayList<ItemProduct> iPList)
	{
		for (ItemProduct ip : iPList)
		{
			this.add(ip);
		}
	}
	
	public static ItemProduct createItemProduct(Barcode barcode, double weightInGrams, String description, double price)
	{
		Barcode IPbarcode = barcode;
		double IPWeightInGrams = weightInGrams;
		String IPDescription = description;
		BigDecimal IPPrice = new BigDecimal(Double.toString(price));
		
		BarcodedItem IPItem = new BarcodedItem(IPbarcode, IPWeightInGrams);
		BarcodedProduct IPProduct = new BarcodedProduct(IPbarcode, IPDescription, IPPrice, IPWeightInGrams);
		
		ItemProduct IP = new ItemProduct(IPItem, IPProduct);
		
		return IP;
	}
}
