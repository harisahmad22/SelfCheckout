//Brody Long - 30022870 

package org.controlSoftware;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.TouchScreenObserver;

public class TouchScreen implements TouchScreenObserver {

	// (Brody) Methods made here will handle inputs from the simulated touch screen
	// / GUI
	// E.g Customer wants to checkout, they press the checkout button and all
	// devices/observers not
	// relevant to checking out will be disabled, and enable all payment related
	// devices/observers if necessary
	// then call the method(s) in the Checkout class to handle the logic of checking
	// out.

	public AtomicBoolean waitingForItemAfterScanDetected = new AtomicBoolean(false);
	public AtomicBoolean waitingForItemAfterScanCorrected = new AtomicBoolean(false);
	public AtomicBoolean scanWeightIssueDetected = new AtomicBoolean(false);
	public AtomicBoolean scanWeightIssueCorrected = new AtomicBoolean(false);
	public AtomicBoolean overloadDetected = new AtomicBoolean(false);
	public AtomicBoolean askedToPrintReceipt = new AtomicBoolean(false);
	public AtomicBoolean paymentOptionsDisplayed = new AtomicBoolean(false);
	public AtomicBoolean changeDispensed = new AtomicBoolean(false);
	public AtomicBoolean invalidWeightInCheckoutCorrected = new AtomicBoolean(false);
	public AtomicBoolean invalidWeightInCheckoutDetected = new AtomicBoolean(false);
	public AtomicBoolean resetSuccessful = new AtomicBoolean(false);
	public AtomicBoolean invalidBarcodeDetected = new AtomicBoolean(false);
	public AtomicBoolean informedToTakeItems = new AtomicBoolean(false);
	public AtomicBoolean returnedToAddingItemMode = new AtomicBoolean(false);
	public AtomicBoolean askedForMembership = new AtomicBoolean(false);
	
	public int numberOfPersonalBags = 0;
	private String membershipNum;
	private Scanner userInputScanner = new Scanner(System.in);
	
	
	

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		System.out.println("A device of type <" + device.getClass().getSimpleName() + "> has been enabled!");
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		System.out.println("A device of type <" + device.getClass().getSimpleName() + "> has been disabled!");
	}

	public void waitingForScannedItem() {
		System.out.println("Please place the item in the bagging area!");
		// Put message on screen that does not go away until weight is valid
		waitingForItemAfterScanDetected.set(true);
	}

	public void doneWaitingForScannedItem() {
		System.out.println("Item has been placed in the bagging area!");
		// Remove message from screen and resume previous state
		waitingForItemAfterScanCorrected.set(true);
	}

	public void invalidWeightAfterScan() {
		scanWeightIssueDetected.set(true);
		System.out.println("Invalid Weight detected after Scan! Please correct the issue before continuing!");
		// Put message on screen that does not go away until weight is valid

	}

	public void validWeightAfterScan() {
		System.out.println("Weight issue corrected! Thank You!");
		// Remove message from screen and resume previous state
		scanWeightIssueCorrected.set(true);
	}

	public void scaleOverloaded() {
		System.out.println("Scale max weight exceeded! Please contact an attendant for assistance");
		// Put message on screen that does not go away until system is not overloaded
		overloadDetected.set(true);
	}

	public void overloadFixed() {
		System.out.println("Scale max weight issue corrected! Please continue your purchase");
		// Remove message from screen and resume previous state
		overloadDetected.set(false);
	}

	public void askToPrintReceipt(ReceiptHandler receipt) {
		System.out.println("Would You like to print a receipt?");
		askedToPrintReceipt.set(true);
		
		//Until GUI is implemented, for testing purposes we will print the receipt here
		receipt.printReceipt();
		
	}

	public int showPaymentOption() throws InterruptedException {
		paymentOptionsDisplayed.set(true);
		
		System.out.println("(PRE-GUI) How would you like to pay? 'Cash', 'Credit', or 'Debt'.");
		
		try
		{			
			String choice = userInputScanner.nextLine();
			choice.toLowerCase();
			if (choice.equals("cash"))
			{ 
				System.out.println("(TESTING) Cash Payment Chosen.");
				return 0;
			}
			else if (choice.equals("credit"))
			{
				System.out.println("(TESTING) Credit Payment Chosen.");
				return 1; 
			}
			else if (choice.equals("debt"))
			{
				System.out.println("(TESTING) Debt Payment Chosen.");
				return 2; 
			}
			else { throw new InputMismatchException(); }
		} catch (InputMismatchException InputMismatchException) {
			System.out.println("Error Processing Input! Please try again.");
			userInputScanner.nextLine();
			showPaymentOption();
		} catch (NoSuchElementException NoSuchElementException) {
			System.out.println("Error Processing Input! Please try again.");
			userInputScanner.nextLine();
			showPaymentOption();
		} catch (NumberFormatException NumberFormatException) {
			System.out.println("Error Processing Input! Please try again.");
			userInputScanner.nextLine();
			showPaymentOption();
		}
		return 0; //Default to cash (should never happen) 

		
	}

	public void informChangeDispensed() {
		System.out.println("Change has been dispensed!");
		changeDispensed.set(true);
	}

	public void validWeightInCheckout() {
		System.out.println("Weight issue During Checkout corrected!");
		invalidWeightInCheckoutCorrected.set(true);
	}

	public void invalidWeightInCheckout() {
		System.out.println("Invalid Weight Detected during Checkout! Please correct the issue before continuing!");
		invalidWeightInCheckoutDetected.set(true);

	}

	public void resetToWelcomeScreen() throws InterruptedException {
		System.out.println("Thank You for shopping with us!");

		TimeUnit.SECONDS.sleep(1);

		System.out.println("Returning to Welcome Screen.");

		TimeUnit.SECONDS.sleep(1);

		System.out.println("Welcome! Press Start to continue.");

		resetSuccessful.set(true);
	}

	public void invalidBarcodeScanned() {
		System.out.println("ERROR! COULD NOT FIND BARCODE IN LOOKUP!");
		invalidBarcodeDetected.set(true);

	}

	public void takeItemsNotification() {
		System.out.println("Please take your items!");
		informedToTakeItems.set(true);

	}

	public void usingOwnBagsPrompt() {
		try {
			System.out.println("How many bags did you bring today?");
			numberOfPersonalBags = Integer.parseInt(userInputScanner.nextLine());
//			numberOfPersonalBags = userInputScanner.nextInt();
			if (numberOfPersonalBags < 0 || numberOfPersonalBags > 10) { throw new InputMismatchException(); }
			// determine # of bags customer brought
			//Brody - Should maybe limit to 10 bags max? can worry about when doing GUI
		} catch (InputMismatchException inputMismatchExcpetion) {
			System.out.println("Sorry, please try again!");
			userInputScanner.nextLine();
			usingOwnBagsPrompt();
		} catch (NoSuchElementException NoSuchElementException) {
			System.out.println("Sorry, please try again!!");
			userInputScanner.nextLine();
			usingOwnBagsPrompt();
		} catch (NumberFormatException NumberFormatException) {
			System.out.println("Sorry, please try again!!");
			userInputScanner.nextLine();
			usingOwnBagsPrompt();
		}  
	}

	public int getNumberOfPersonalBags() {
		return numberOfPersonalBags;
	}

	public BigDecimal choosePaymentAmount(BigDecimal totalDue, BigDecimal totalPaid) {
		System.out.println("(PRE-GUI) Would you like you to make a full or partial payment?");
		System.out.println("(PRE-GUI) If you would like to pay a partial amount, input 'partial'.");
		System.out.println("(PRE-GUI) Otherwise Press Enter to make a full payment.");

		BigDecimal remainingDue = totalDue.subtract(totalPaid);
		System.out.println("(TESTING) Remaining Money Due: " + remainingDue);
		
		try
		{
		//---------------Add GUI logic for handling selection/amount---------------
			
			String choice = userInputScanner.nextLine();
			//---------------Add GUI logic for handling selection/amount---------------
			
			if (choice.equals("partial"))
			{ //Simulate choosing partial
				System.out.println("(TESTING) Partial Payment Chosen.");
				//Maybe unnecessary but, takes next line from input, tries to parse it as a double
				//Then converts back to a string so we can pass this value into the BigDecimal constructor
				//And avoid extra decimal place
				//Should probably get a regex for a payment amount with a max of 2 decimal places
				System.out.println("Please enter in how much you would like to pay:");
				BigDecimal partialPaymentAmount = userInputScanner.nextBigDecimal();
				userInputScanner.nextLine(); //Clear out '\n'
				if (partialPaymentAmount.compareTo(BigDecimal.ZERO) <= 0)
				{ //For now if user chooses to pay <= $0, default to full payment
//					userInputScanner.close();
					return remainingDue;
				}
				else if (partialPaymentAmount.compareTo(remainingDue) >= 0)
				{ //If user enters in more than totalDue, default to full payment
//					userInputScanner.close();
					return remainingDue;
				}
//				userInput.close();
				return partialPaymentAmount;
			}
			else 
			{
				System.out.println("(TESTING) Full Payment Chosen.");
//				userInput.close();
				return remainingDue; 
			}
		} catch (InputMismatchException InputMismatchException) {
			System.out.println("Error Processing Input! Please try again.");
			userInputScanner.nextLine();
			choosePaymentAmount(totalDue, totalPaid);
		} catch (NoSuchElementException NoSuchElementException) {
			System.out.println("Error Processing Input! Please try again.");
			userInputScanner.nextLine();
			choosePaymentAmount(totalDue, totalPaid);
		} catch (NumberFormatException NumberFormatException) {
			System.out.println("Error Processing Input! Please try again.");
			userInputScanner.nextLine();
			choosePaymentAmount(totalDue, totalPaid);
		} 
		//Should never be reached 
		return remainingDue;
	}

	public void returnToAddingItems() {
		System.out.println("Payment Complete, you may add more items or make another payment.");
		returnedToAddingItemMode.set(true);
		
	}
	
	public void inputMembershipPrompt(Checkout checkout) throws InterruptedException {
		//For now default choice to swipe
		//Could maybe have a loop that runs until hardware detects a valid swipe
		//or user can press a button to bring up a keypad to enter in their ID
		
		try {
			
			System.out.println("(PRE-GUI) If you have a Membership card, input 'swipe'.");
			System.out.println("(PRE-GUI) If you do not have a Membership card but have a membership, input 'manual'.");
			System.out.println("(PRE-GUI) Otherwise Press Enter to skip.");
			String choice = userInputScanner.nextLine();
			if (choice.equals("manual"))
			{ //Manual Entry
				System.out.println("Please Enter you Membership ID:");
				int inputID = userInputScanner.nextInt();
				userInputScanner.nextLine();
				
				String membership_num = Integer.toString(inputID);
				checkout.setMembershipNumber(membership_num);
			}
			else if (choice.equals("swipe"))
			{	//Wait for swipe				
				System.out.println("Please Swipe you Membership Card.");
				while(!checkout.getCardSwiped())
				{
					TimeUnit.MILLISECONDS.sleep(100);
				}
				checkout.setCardSwiped(false); //Reset flag for next event		
			}
//			userInputScanner.close();
						
		} catch (InputMismatchException InputMismatchException) {
			System.out.println("Error Processing Input! Please enter your membership card number again.");
			userInputScanner.nextLine();
			inputMembershipPrompt(checkout);
		}
		askedForMembership.set(true);
	}
	

}
