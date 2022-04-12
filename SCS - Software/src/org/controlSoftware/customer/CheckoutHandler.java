//Brody Long - 30022870 
//Shufan Zhai - 30117333

package org.controlSoftware.customer;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.controlSoftware.data.NegativeNumberException;
import org.controlSoftware.deviceHandlers.ReceiptHandler;
import org.controlSoftware.deviceHandlers.payment.GiveChange;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutData.StationState;
import org.driver.SelfCheckoutSoftware;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.CoinSlot;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation; // needed for GiveChange

public class CheckoutHandler {
	private SelfCheckoutData stationData;
	private SelfCheckoutSoftware stationSoftware;

	public CheckoutHandler(SelfCheckoutData stationData, SelfCheckoutSoftware stationSoftware) {		
		this.stationData = stationData;
		this.stationSoftware = stationSoftware;
		
	}

		
	public void startCheckout() {
		// User has begun checkout
		
		//Attendant Block check
		stationSoftware.attendantBlockCheck();				
		
		// Set our expected Weight to the current scale weight
		// Allows scale observer to set weightValid
		try {
			stationData.setExpectedWeight(stationData.getBaggingAreaScale().getCurrentWeight());
		} catch (OverloadException e) {
			System.out.println("Overloaded scale at start of checkout!");
		}

		// TouchScreen method that will ask user if they have their own bags
		// and how many if they do. If user does not have bags they will enter 0 bags
		if (stationData.isFirstCheckout())
		{ //Only prompt user for bags and membership if they haven't already been
			
			//If user has bags, system will change to adding bags state and wait for user to inform system that
			//they have put their bags down
			//Once bags have been put down, the expected scale weight is updated to the current scale weight
			stationData.setIsFirstCheckout(false);
			stationData.changeState(StationState.ADD_BAGS_PROMPT);
			return;
		}
		else 
		{	
			stationData.changeState(StationState.PAYMENT_AMOUNT_PROMPT);
			return;			
		}
		
	}

	public void handleChange() {
		BigDecimal changeAmount = BigDecimal.ZERO;
		
		if (stationData.getTotalMoneyPaid().compareTo(stationData.getTotalDue()) == 1)
		{ //Payment has exceeded totalDue, get the change amount
			changeAmount = stationData.getTotalMoneyPaid().subtract(stationData.getTotalDue());
			GiveChange someChange = new GiveChange(stationData.getStationHardware(), changeAmount);
	        
			try {
				someChange.dispense();
			} catch (EmptyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DisabledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//Otherwise change is defaulted to 0 when a partial payment is completed
	
		stationSoftware.getReceiptHandler().setFinalTotal(stationData.getTotalDue());
		stationSoftware.getReceiptHandler().setMoneyPaid(stationData.getTotalMoneyPaid());
		stationSoftware.getReceiptHandler().setFinalChange(changeAmount.toString());
	
	}
}
