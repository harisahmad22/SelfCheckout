package org.controlSoftware.data;

import org.lsmr.selfcheckout.products.Product;

//This class allows you to create a tuple of a Product and its weight since the 
//base Product class does not store a weight value, can assign the weight value following
//the event of putting the product on the scanner area scale and learning its weight.
public class ProductInfo {
	 public final Product product;
	 public final Double weight;
	 
	 public ProductInfo(Product product, Double weight)
	 {
		 this.product = product;
		 this.weight = weight;
	 }
	 
	 public Product getProduct()
	 {
		 return this.product;
	 }
	
	 public double getWeight()
	 {
		 return this.weight;
	 }
}
