//Brody Long - 30022870

package org.controlSoftware.data;

import java.util.HashMap;

import org.lsmr.selfcheckout.Barcode;

/*
 	Used to create a dictionary of ItemProducts where the keys are the barcodes for the specific items.
 	Each ItemProduct gives us the weight, price, description, and if the item is priced per unit or per kilo for the given item.
 	Limits interaction with the underlying HashMap to only be able to add, remove, or get elements in the dictionary.
 */
public class BarcodeLookup {
	
	private static HashMap<Barcode, ItemProduct> lookup;
	
	public BarcodeLookup()
	{
		lookup = new HashMap<Barcode, ItemProduct>();
	}
	
	public void add(ItemProduct itemToAdd)
	{
		lookup.put(itemToAdd.getItemBarcode(), itemToAdd);
	}
	
	public ItemProduct get(Barcode barcode)
	{
		return lookup.get(barcode);
	}
	
	public void remove(Barcode barcode)
	{
		lookup.remove(barcode);
	}
}
