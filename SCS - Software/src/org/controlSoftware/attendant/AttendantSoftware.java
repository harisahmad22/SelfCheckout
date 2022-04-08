//Brody Long - 30022870 

package org.controlSoftware.attendant;

import java.awt.event.ComponentListener;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;

import org.driver.SelfCheckoutStationUnit;
import org.driver.databases.PLUProductDatabase;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.AbstractDevice;
<<<<<<< Updated upstream
=======
import org.lsmr.selfcheckout.devices.Keyboard;
import org.lsmr.selfcheckout.devices.OverloadException;
>>>>>>> Stashed changes
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SupervisionStation;
import org.lsmr.selfcheckout.devices.TouchScreen;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.TouchScreenObserver;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class AttendantSoftware {

	// (Brody)
	//Software for performing various tasks via the Supervisor station
	//For now, assume one supervisor station controls all available checkout stations
	//Some number of checkout stations will be created, and each will be added to the supervisor station
	//via its add(SCS) method 
	
	//Listeners for the Attendant GUI will call methods in this class to handle events

	private TouchScreen touchScreenDevice;
//	private JFrame guiFrame;
	private SupervisionStation supervisionStation;
	
	private PLUProductDatabase pluProductData;
	
<<<<<<< Updated upstream
	public AttendantSoftware(SupervisionStation supervisionStation, TouchScreen touchScreenDevice)
	{
		this.supervisionStation = supervisionStation;
		this.touchScreenDevice = touchScreenDevice;
=======
	public AttendantSoftware(SupervisionStation supervisionStation, AttendantData attendantData, ArrayList<SelfCheckoutStationUnit> checkoutStationUnits, PLUProductDatabase pluDatabase)
	{
		this.supervisionStation = supervisionStation;
		this.attendantData = attendantData;
		this.touchScreenDevice = supervisionStation.screen;
		this.keyboard = supervisionStation.keyboard;
		this.pluProductData = pluDatabase; // attendant can now access the plu data base
		
		//Use this array list to access all the data/software/hardware of any connected checkout station
		this.checkoutStationUnits = checkoutStationUnits;
	}
	
//	public void overrideWeightIssue(int stationIndex) {
//		// Idea: Set the weight_valid flag for the corresponding station to true
//		// maybe check if station is in checkout or just scanned something also
//		SelfCheckoutStationUnit stationToOverride = checkoutStationUnits.get(stationIndex);
//		stationToOverride.getSelfCheckoutSoftware().performAttendantWeightOverride();
//		
//	}
	
	public void startupStation(SelfCheckoutStationUnit station)
	{
		System.out.println("Starting station: " + station.getStationID());
		station.getSelfCheckoutSoftware().startupStation();
>>>>>>> Stashed changes
	}
	
	public void overrideWeightIssue(int stationIndex) {
		// Idea: Set the weight_valid flag for the corresponding station to true
		// maybe check if station is in checkout or just scanned something also
		List<SelfCheckoutStation> staionList = supervisionStation.supervisedStations();
		
	}
	
	public void blockStation(SelfCheckoutStationUnit station)
	{
		
	}
<<<<<<< Updated upstream
=======
	
	// Yiannis Hontzias
		// attendant is prompted to type in PLU code from the GUI
				// station will be blocked until the customer is asked to weight the product
		public void productLookUp(SelfCheckoutStationUnit station, PriceLookupCode plu) throws OverloadException
		{
			double new_weight;
			
			blockStation(station);
			PLUCodedProduct attendProd;
			attendProd = pluProductData.getPLUProductFromDatabase(plu);
			// notify customer to place product on the scale
			unBlockStation(station);
			System.out.println("Please place the product on the scale.");
			// wait for customer to weight the product
			new_weight = station.getSelfCheckoutStationHardware().scanningArea.getCurrentWeight();
			station.getSelfCheckoutData().addProductToCheckout(attendProd, new_weight);
			
		}
		
		
		
		public void productLookUp(String stationID, PriceLookupCode plu) throws OverloadException
		{
			double new_weight;
			
			blockStation(stationID);
			PLUCodedProduct attendProd;
			attendProd = pluProductData.getPLUProductFromDatabase(plu);
			// notify customer to place product on the scale
			unBlockStation(stationID);
			System.out.println("Please place the product on the scale.");
			// wait for customer to weight the product
			new_weight = checkoutStationUnits.get(Integer.parseInt(stationID)).getSelfCheckoutStationHardware().scanningArea.getCurrentWeight();
			checkoutStationUnits.get(Integer.parseInt(stationID)).getSelfCheckoutData().addProductToCheckout(attendProd, new_weight);
			
		}
		
		
		
		
		// will wait for the customer or attendant to press "continue" after ink/paper is added then
		// the station will be unblocked and the customer can continue with their checkout
		
		public void updatePrinterInk(SelfCheckoutStationUnit station, int amount) throws OverloadException
		{
			blockStation(station);
			station.getSelfCheckoutStationHardware().printer.addInk(amount);
			// wait for the customer/attendant to press continue after the ink has been filled
			// once conitune has been pressed and no errors after adding the ink then the station
			// will be unblocked and the customer can continue
			unBlockStation(station);
		}
		
		
		public void updatePrinterInk(String stationID, int amount) throws OverloadException
		{
			blockStation(stationID);
			checkoutStationUnits.get(Integer.parseInt(stationID)).getSelfCheckoutStationHardware().printer.addInk(amount);
			// wait for the customer/attendant to press continue after the ink has been filled
					// once conitune has been pressed and no errors after adding the ink then the station
					// will be unblocked and the customer can continue
			unBlockStation(stationID);
		}
		
		public void updatePrinterPaper(SelfCheckoutStationUnit station, int units) throws OverloadException 
		{
			blockStation(station);
			station.getSelfCheckoutStationHardware().printer.addPaper(units); 
			// wait for the customer/attendant to press continue after the paper has been added
					// once conitune has been pressed and no errors after adding the paper then the station
					// will be unblocked and the customer can continue
			unBlockStation(station);
		}
		
		
		
		public void updatePrinterPaper(String stationID, int units) throws OverloadException 
		{
			blockStation(stationID);
			checkoutStationUnits.get(Integer.parseInt(stationID)).getSelfCheckoutStationHardware().printer.addPaper(units);
			// wait for the customer/attendant to press continue after the paper has been added
			// once conitune has been pressed and no errors after adding the paper then the station
			// will be unblocked and the customer can continue
			unBlockStation(stationID);
		}
>>>>>>> Stashed changes

	

}
