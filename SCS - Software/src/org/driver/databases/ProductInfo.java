package org.driver.databases;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

//This class allows you to create a tuple of a Product and its weight since the 
//base Product class does not store a weight value, can assign the weight value following
//the event of putting the product on the scanner area scale and learning its weight.
public class ProductInfo {
	 public Product product = null; //Stores Price + IsPerUnit
	 public Double weight = null; //Will come from Barcode Database or Via scanner scale 
	 public String description = null; //Will come from Database
	 public Barcode barcode = null; //Will come from Barcode database
	 public PriceLookupCode pluCode = null; //Will come from PLU database
	 public int quantity = 1;
	 
	  
	 public ProductInfo(Product product, String description, double weight)
	 {
		 this.product = product;
		 this.description = description;
		 this.weight = weight;
	 }
	 
	 public ProductInfo(BarcodedProduct product)
	 {
		 this.product = product;
		 this.description = product.getDescription();
		 this.weight = product.getExpectedWeight();
		 this.barcode = product.getBarcode();
		 this.quantity = 1;
	 }
	 
	 public ProductInfo(PLUCodedProduct product, double weight)
	 {
		 this.product = product;
		 this.description = product.getDescription();
		 this.weight = weight; //Come from scale 
		 this.pluCode = product.getPLUCode();
		 this.quantity = 1;
	 }
	 
	 public Product getProduct()
	 {
		 return this.product;
	 }
	
	 public double getWeight()
	 {
		 return this.weight;
	 }
	 
	 public String getDescription()
	 {
		 return this.description;
	 }
	 
	 public Barcode getBarcode()
	 {
		 return this.barcode;
	 }
	 
	 public PriceLookupCode getPLUCode()
	 {
		 return this.pluCode;
	 }

	public void increaseQuantity() {
		this.quantity += 1;
		
	}

	public int getQuantity() {
		return this.quantity;
	}
}
