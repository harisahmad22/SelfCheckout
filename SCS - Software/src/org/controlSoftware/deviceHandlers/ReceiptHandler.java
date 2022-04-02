package org.controlSoftware.deviceHandlers;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;

public class ReceiptHandler {
	
	private static ArrayList<String> scannedProductList = new ArrayList<String>();
	private static String membershipID = "null\n"; //Default to null, change when membership card is scanned in
	private static String membershipPoints = "0\n";
	private static String finalTotal = "$0.0\n";
	private static String finalChange = "$0.0\n";
	private static String moneyPaid;
	private ReceiptPrinter printer;
	
	public ReceiptHandler(ReceiptPrinter printer)
	{
		this.printer = printer;
	}
	
	public void printReceipt()
	{
		//For each entry in the scannedProductList, convert the string
		//to a byte array, and loop over each character printing it via the printer
		//The print can print a MAX of 60 chars on a single line
		for (String productInfo : scannedProductList)
		{
			char[] productCharArray = productInfo.toCharArray();
			
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

	public static ArrayList<String> getScannedProductList() {
		return scannedProductList;
	}
	
	//Pass in string values for the product we just scanned
	//Formated as: Description --- Price
	public static void addProductToReceipt(String productDescription, String productPrice) {
		String receiptEntry = productDescription + " --- $" + productPrice + "\n";
		ReceiptHandler.scannedProductList.add(receiptEntry);
	}
	public static String getMembershipID() {
		return membershipID;
	}
	public static void setMembershipID(String membershipID) {
		ReceiptHandler.membershipID = membershipID + "\n";
	}
	
	public static String getMembershipPoints() {
		return membershipPoints;
	}
	public static void setMembershipPoints(String membershipPoints) {
		ReceiptHandler.membershipPoints = membershipPoints + "\n";
	}
	
	public static void setMembershipPoints(int membershipPoints) {
		ReceiptHandler.membershipPoints = membershipPoints + "\n";
	}
	
	public static String getFinalTotal() {
		return finalTotal;
	}
	public static void setFinalTotal(BigDecimal finalTotal) {
		ReceiptHandler.finalTotal = "$" + finalTotal.toString() + "\n";
	}
	public static void setFinalTotal(String finalTotal) {
		ReceiptHandler.finalTotal = "$" + finalTotal + "\n";
	}

	public static String getFinalChange() {
		return finalChange;
	}

	public static void setFinalChange(double finalChange) {
		ReceiptHandler.finalChange = "$" + String.valueOf(finalChange) + "\n";
	}
	public static void setFinalChange(String finalChange) {
		ReceiptHandler.finalChange = "$" + finalChange + "\n";
	}

	public static void setMoneyPaid(String moneyPaid) {
		ReceiptHandler.moneyPaid = "$" + moneyPaid + "\n";
	}
}
