package org.driver;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.controlSoftware.general.TouchScreenSoftware;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.Product;

/*
 *  
 *  Jonah Richards
 *
 *  Defines object to be associated with each self checkout machine that contains any data that
 *  might be needed across individual parts of the system. Running weights, totals, etc..
 *  
 *  Just attributes with getters/setters.
 *  
 *  **TO-DO
 *  Exception handling
 *  Input validation?
 *  
 */

public class SelfCheckoutData {
	// Can't make attributes static, unfortunately, as multiple machines will all have their own data.
	private BigDecimal totalDue = BigDecimal.ZERO;
	private BigDecimal totalMoneyPaid = BigDecimal.ZERO;
    
	private SelfCheckoutStation station;
	
	private TouchScreenSoftware touchScreen;
	private double expectedWeight;
	private String membershipID = "null\n"; //Default to null, change when membership card is scanned in
	
	// No implementation yet
	private String membershipPoints = "0\n";
	
	// List of scanned products for later use (List of product objects, different from list of strings for receipt printing)
	// **Future problem: PLU products are going to have a weight at checkout not part of its object definition
	private ArrayList<Product> scannedProductList = new ArrayList<Product>();
	
	
	public SelfCheckoutData(SelfCheckoutStation newStation) {
		station = newStation;
	}
	
	
	/* 
	 * High level states the self checkout station can be in
	 * Boolean state checks can be done in logic.
	 * 
	 *		**I have no idea how this is going to mesh with multithreading. Consultation needed.
	 */
	protected enum State {
		// Welcome screen
		WELCOME,
		
		// Ready for item to be scanned. Could proceed to checkout from here
		SCANNING,
		
		// Scanned item need be bagged. Should return to scanning once bagged.
		BAGGING,
		
		// State for adding bags. Help scale observers differentiate reason for weight change.
		ADDING_BAGS,
		
		// Interim checkout menu, can go back and scan more items or proceed to some payment option
		CHECKOUT,
		
		// Make full or partial cash payment. Return to CHECKOUT when completed.
		PAY_CASH,		// **Only change with cash payment?
		
		// Make full or partial credit payment. Return to CHECKOUT when completed.
		PAY_CREDIT,	
		
		// Make full or partial debit payment. Return to CHECKOUT when completed.
		PAY_DEBIT,		// **Combine credit/debit into PAY_CARD? Any difference?
		
		// Dedicated state for scanning membership card/typing number
		ADD_MEMBERSHIP,
		
		// General post-checkout state. Print receipt? Dispense change? Just a thank you message? Returns to WELCOME
		FINISHED,
		
		// General error state. No implementation yet. Potentially when item is not bagged? Notify attendant?
		// Maybe error sub-states are required? Maintenance state?
		ERROR
	}

	protected State state = State.WELCOME;
	
	// Getters/setters
	
	public void setTotalDue(BigDecimal total) {
		totalDue = total;
	}
	public BigDecimal getTotalDue() {
		return totalDue;
	}
	
	public void setTotalMoneyPaid(BigDecimal total) {
		totalMoneyPaid = total;
	}
	public BigDecimal getTotalMoneyPaid() {
		return totalMoneyPaid;
	}
	
	public void setMembershipID(String ID) {
		membershipID = ID;
	}
	public String getMembershipID() {
		return membershipID;
	}
	
	public void addScannedProduct(Product product) {
		scannedProductList.add(product);
	}
	public Product getScannedProduct(int index) {
		return scannedProductList.get(index);
	}
	
	/*
	 *  State changing methods
	 */
	
	// Changes to new state while properly exiting old one (enabling/disabling relevant hardware)
	public void changeState(State targetState) {
		// Disable hardware for old state
		exitState(state);
		state = targetState;
		// Enable hardware for new state
		switch(targetState) {
		
		case WELCOME:
			wipeData();
			break;
		
		case SCANNING:
			station.mainScanner.enable();
			station.handheldScanner.enable();
			station.scanningArea.enable();
			break;
		
		case BAGGING:
			station.baggingArea.enable();
			break;
			
		case ADDING_BAGS:
			station.baggingArea.enable();
			break;
			
		case PAY_CASH:
			station.banknoteInput.enable();
			station.coinSlot.enable();
			break;
			
		case PAY_CREDIT:
			station.cardReader.enable();
			break;
			
		case PAY_DEBIT:
			station.cardReader.enable();
			break;
			
		case ADD_MEMBERSHIP:
			station.cardReader.enable();
			break;
			
		case FINISHED:
			station.printer.enable(); 	// **Not sure where we want receipt printed. Can be changed.
			break;
			
		case ERROR:
			break;
			
		default:
			break;
		} 
	}
	
	private void exitState(State state) {
		switch(state) {
		case WELCOME:
			break;
		
		case SCANNING:
			station.mainScanner.disable();
			station.handheldScanner.disable();
			station.scanningArea.disable();
			break;
		
		case BAGGING:
			station.baggingArea.disable();
			break;
			
		case ADDING_BAGS:
			station.baggingArea.disable();
			break;
			
		case PAY_CASH:
			station.banknoteInput.disable();
			station.coinSlot.disable();
			break;
			
		case PAY_CREDIT:
			station.cardReader.disable();
			break;
			
		case PAY_DEBIT:
			station.cardReader.disable();
			break;
			
		case ADD_MEMBERSHIP:
			station.cardReader.disable();
			break;
			
		case FINISHED:
			station.printer.disable();
			break;
			
		case ERROR:
			break;
			
		default:
			break;
		}
	}
		
	private void wipeData() {
		totalDue = BigDecimal.ZERO;
		totalMoneyPaid = BigDecimal.ZERO;
		expectedWeight = 0.0;
		membershipID = "null\n"; //Default to null, change when membership card is scanned in
		membershipPoints = "0\n";
		scannedProductList = new ArrayList<Product>();
	}
		
}

















