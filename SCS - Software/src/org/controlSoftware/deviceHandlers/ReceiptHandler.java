package org.controlSoftware.deviceHandlers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.controlSoftware.data.ProductInfo;
import org.driver.SelfCheckoutData;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;

public class ReceiptHandler {
	
//	private ArrayList<String> scannedProductList = new ArrayList<String>();
	private String membershipID = "null\n"; //Default to null, change when membership card is scanned in
	private String membershipPoints = "0\n";
	private String finalTotal = "$0.0\n";
	private String finalChange = "$0.0\n";
	private String moneyPaid;
	private ReceiptPrinter printer;
	private SelfCheckoutData stationData;
	
	public ReceiptHandler(SelfCheckoutData stationData, ReceiptPrinter printer)
	{
		this.stationData = stationData;
		this.printer = printer;
	}
	
	public void printReceipt()
	{
		//For each entry in the scannedProductList, convert the string
		//to a byte array, and loop over each character printing it via the printer
		//The print can print a MAX of 60 chars on a single line
		
		HashMap<String, ProductInfo> productsInCheckout = stationData.getProductsAddedToCheckoutHashMap();
		for (String productDescription : productsInCheckout.keySet())
		{
			//Get the price via description as key, then get Product from ProductInfo wrapper, then get the price.
			BigDecimal productPrice = productsInCheckout.get(productDescription).getProduct().getPrice();
			
			String receiptEntry = productDescription + " --- $" + productPrice + "\n";
			char[] productCharArray = receiptEntry.toCharArray();
			
			printChars(productCharArray);
		}	
		//Now All scanned items will have been printed on the receipt
		//Print membership ID followed by points
		if (!membershipID.equals("null\n"))
		{
			char[] id = ("Membership ID: " + membershipID).toCharArray();
//			char[] points = ("Available Membership Points: " + membershipPoints).toCharArray();
			
			printChars(id);
//			printChars(points);
		}
		
		//Now print the total cost of all items, and if any change was given back
		char[] total = ("Total: " + finalTotal).toCharArray();
		char[] paid = ("Paid: " + moneyPaid).toCharArray();
		char[] change = ("Change: " + finalChange).toCharArray();
		printChars(total);
		printChars(paid);
		printChars(change);
		
//		resetReceipt();
		//Done printing receipt, cut paper so user can take
		printer.cutPaper(); 
	}
	
	
	private void resetReceipt() {
		membershipID = "null\n"; //Default to null, change when membership card is scanned in
		membershipPoints = "0\n";
		finalTotal = "$0.0\n";
		finalChange = "$0.0\n";
	}
	
	private void printChars(char[] charArray) {
		for (char c : charArray)
		{
			try {
				printer.print(c);
			} catch (EmptyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

//	public ArrayList<String> getScannedProductList() {
//		return scannedProductList;
//	}
//	
//	//Pass in string values for the product we just scanned
//	//Formated as: Description --- Price
//	public void addProductToReceipt(String productDescription, String productPrice) {
//		String receiptEntry = productDescription + " --- $" + productPrice + "\n";
//		this.scannedProductList.add(receiptEntry);
//	}
	public String getMembershipID() {
		return this.membershipID;
	}
	public void setMembershipID(String ID) {
		this.membershipID = ID + "\n";
	}
	
	public String getMembershipPoints() {
		return this.membershipPoints;
	}
	public void setMembershipPoints(String points) {
		this.membershipPoints = points + "\n";
	}
	
	public void setMembershipPoints(int points) {
		this.membershipPoints = points + "\n";
	}
	
	public String getFinalTotal() {
		return this.finalTotal;
	}
	public void setFinalTotal(BigDecimal total) {
		this.finalTotal = "$" + total.toString() + "\n";
	}
	public void setFinalTotal(String total) {
		this.finalTotal = "$" + total + "\n";
	}

	public String getFinalChange() {
		return this.finalChange;
	}

	public void setFinalChange(double change) {
		this.finalChange = "$" + String.valueOf(change) + "\n";
	}
	public void setFinalChange(String change) {
		this.finalChange = "$" + change + "\n";
	}

	public void setMoneyPaid(String paid) {
		this.moneyPaid = "$" + paid + "\n";
	}
}
