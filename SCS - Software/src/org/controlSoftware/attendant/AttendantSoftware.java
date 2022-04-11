//Brody Long - 30022870 

package org.controlSoftware.attendant;

import java.awt.event.ComponentListener;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.Keyboard;
import org.lsmr.selfcheckout.devices.OverloadException;
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
	
	
	//(BRODY): I am trying out using the selfCheckoutStaionUnit as a wrapper for each station, instead of relying on 
	// the list of SelfCheckoutStations stored in the SupervisionStation class

	private TouchScreen touchScreenDevice;
//	private JFrame guiFrame;
	private SupervisionStation supervisionStation;
	private Keyboard keyboard;
	private ArrayList<SelfCheckoutStationUnit> checkoutStationUnits;
	private PLUProductDatabase pluProductData;
	
	
	public AttendantSoftware(SupervisionStation supervisionStation, 
			ArrayList<SelfCheckoutStationUnit> checkoutStationUnits, 
			PLUProductDatabase pluDatabase)
	{
		this.supervisionStation = supervisionStation;
		this.touchScreenDevice = supervisionStation.screen;
		this.keyboard = supervisionStation.keyboard;		
		
		//Use this array list to access all the data/software/hardware of any connected checkout station
		this.checkoutStationUnits = checkoutStationUnits;
		
		this.pluProductData = pluDatabase; // attendant can now access the plu data base
	}
	
	public void startupStation(SelfCheckoutStationUnit station)
	{
		System.out.println("Starting station: " + station.getStationID());
		station.getSelfCheckoutSoftware().startupStation();
	}
	
	public void startupStation(int stationID)
	{
		System.out.println("Starting station: " + stationID);
		checkoutStationUnits.get(stationID).getSelfCheckoutSoftware().startupStation();
	}
	
	public void startupStation(String stationID)
	{
		System.out.println("Starting station: " + stationID);
		checkoutStationUnits.get(Integer.parseInt(stationID)).getSelfCheckoutSoftware().startupStation();
	}
	
	public void shutdownStation(SelfCheckoutStationUnit station)
	{
		System.out.println("Shutting down station: " + station.getStationID());
		station.getSelfCheckoutSoftware().shutdownStation();
	}
	
	public void shutdownStation(int stationID)
	{
		System.out.println("Shutting down station: " + stationID);
		checkoutStationUnits.get(stationID).getSelfCheckoutSoftware().shutdownStation();
	}
	
	public void shutdownStation(String stationID)
	{
		System.out.println("Shutting down station: " + stationID);
		checkoutStationUnits.get(Integer.parseInt(stationID)).getSelfCheckoutSoftware().shutdownStation();
	}
	
	
	public void blockStation(SelfCheckoutStationUnit station)
	{
		System.out.println("Blocking station: " + station.getStationID());
		station.getSelfCheckoutSoftware().blockStation();
	}
	
	public void blockStation(int stationID)
	{
		System.out.println("Blocking station: " + stationID);
		checkoutStationUnits.get(stationID).getSelfCheckoutSoftware().blockStation();
	}
	
	public void blockStation(String stationID)
	{
		System.out.println("Blocking station: " + stationID);
		checkoutStationUnits.get(Integer.parseInt(stationID)).getSelfCheckoutSoftware().blockStation();
	}
	
	public void unBlockStation(int stationID)
	{
		System.out.println("Blocking station: " + stationID);
		checkoutStationUnits.get(stationID).getSelfCheckoutSoftware().unBlockStation();
	}
	
	public void unBlockStation(SelfCheckoutStationUnit station)
	{
		System.out.println("Blocking station: " + station.getStationID());
		station.getSelfCheckoutSoftware().unBlockStation();
	}
		
	public void unBlockStation(String stationID)
	{
		System.out.println("Unblocking station: " + stationID);
		checkoutStationUnits.get(Integer.parseInt(stationID)).getSelfCheckoutSoftware().unBlockStation();
	}

	public void LogInStation(SelfCheckoutStationUnit station, String attendantIdEntered,  String passwordEntered)
	{
		//System.out.println("Starting station: " + station.getStationID());
        System.out.println("Log in: " + station.getStationID());

        ArrayList<String> AttendantIdStored = station.getAttendantID();
        ArrayList<String> PasswordStored = station.getPassword();
        for(int i = 0 ; i < AttendantIdStored.size(); i++){
            if((AttendantIdStored.get(i) == attendantIdEntered) && (PasswordStored.get(i) == passwordEntered)){
                station.getSelfCheckoutSoftware().LogInStation(attendantIdEntered, passwordEntered);
                unBlockStation(station);
                break;
            }
        }
        System.out.println("Error! Wrong ID or password. Fail to log in.");
	}
	
	public void logOutStation(SelfCheckoutStationUnit station)
	{
		System.out.println("Log out: " + station.getStationID());
		station.getSelfCheckoutSoftware().LogOutStation();
        blockStation(station);
	}
	
	public void setCheckoutStationUnits(ArrayList<SelfCheckoutStationUnit> newCheckoutStationUnits) {
		this.checkoutStationUnits = newCheckoutStationUnits;
		
	}
	public ArrayList<SelfCheckoutStationUnit> getCheckoutStationUnits() {
		return this.checkoutStationUnits;
		
	}
	
	/*
	 * Attendant empties the coin storage unit
	 * takes in stationID, either in form of integer, or string
	 * return all the coins that have been emptied from the storage unit
	 */
	public List<Coin> emptyCoinStorageUnit(int stationID)
	{
		return checkoutStationUnits.get(stationID).getSelfCheckoutStationHardware().coinStorage.unload();
	}
	
	public List<Coin> emptyCoinStorageUnit(String stationID)
	{
		return checkoutStationUnits.get(Integer.parseInt(stationID)).getSelfCheckoutStationHardware().coinStorage.unload();
	}
	
	/*
	 * Attendant empties the banknote storage unit
	 * takes in stationID, either in form of integer, or string
	 * return all the banknotes that have been emptied from the storage unit
	 */
	public List<Banknote> emptyBanknoteStorageUnit(int stationID)
	{
		return checkoutStationUnits.get(stationID).getSelfCheckoutStationHardware().banknoteStorage.unload();
	}
	
	public List<Banknote> emptyBanknoteStorageUnit(String stationID)
	{
		return checkoutStationUnits.get(Integer.parseInt(stationID)).getSelfCheckoutStationHardware().banknoteStorage.unload();
	}
	
	//Hannah Ku
	public void overrideWeightIssue(int stationIndex) {
		// Idea: Set the weight_valid flag for the corresponding station to true
		// maybe check if station is in checkout or just scanned something also
		SelfCheckoutStationUnit stationToOverride = checkoutStationUnits.get(stationIndex);
		stationToOverride.getSelfCheckoutSoftware().performAttendantWeightOverride();
		
	}
	//Hannah Ku
	public void removeProduct(int stationIndex, String description) {
		// Idea: Set the weight_valid flag for the corresponding station to true
		// maybe check if station is in checkout or just scanned something also
		SelfCheckoutStationUnit stationToOverride = checkoutStationUnits.get(stationIndex);
		stationToOverride.getSelfCheckoutSoftware().removeProduct(description);
		
	}
	
	
	/*
	 * Attendant refills the coin dispenser 
	 * takes in stationID, either in form of integer, or string, and coins that are to be refilled into the station
	 */
	public void refillCoinDispenser(int stationID, Coin... coins) throws SimulationException, OverloadException
	{
		if (coins.length == 0) {
			return;
		}
		else {
			for(Coin coin : coins)
				checkoutStationUnits.get(stationID).getSelfCheckoutStationHardware().coinDispensers.get(coin.getValue()).load(coin);
		}
	}
	
	
	public void refillCoinDispenser(String stationID, Coin... coins) throws SimulationException, OverloadException
	{
		if (coins.length == 0) {
			return;
		}
		else {
			for(Coin coin : coins)
				checkoutStationUnits.get(Integer.parseInt(stationID)).getSelfCheckoutStationHardware().coinDispensers.get(coin.getValue()).load(coin);
		}
	}

	
	/*
	 * Attendant refills the banknote dispenser 
	 * takes in stationID, either in form of integer, or string, and banknotes that are to be refilled into the station
	 */
	public void refillbanknoteDispenser(int stationID, Banknote... banknotes) throws SimulationException, OverloadException
	{
		if (banknotes.length == 0) {
			return;
		}
		else {
			for(Banknote banknote : banknotes)
				checkoutStationUnits.get(stationID).getSelfCheckoutStationHardware().banknoteDispensers.get(banknote.getValue()).load(banknote);
		}
	}
	
	public void refillbanknoteDispenser(String stationID, Banknote... banknotes) throws SimulationException, OverloadException
	{
		if (banknotes.length == 0) {
			return;
		}
		else {
			for(Banknote banknote : banknotes)
				checkoutStationUnits.get(Integer.parseInt(stationID)).getSelfCheckoutStationHardware().banknoteDispensers.get(banknote.getValue()).load(banknote);
		}
	}
	
	//=============================================Yiannis Hontzias=============================================
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
	
	
	// added methods to use stationIDs but could be removed if not used
	public void productLookUp(int stationID, PriceLookupCode plu) throws OverloadException
	{
		double new_weight;
		
		blockStation(stationID);
		PLUCodedProduct attendProd;
		attendProd = pluProductData.getPLUProductFromDatabase(plu);
		// notify customer to place product on the scale
		unBlockStation(stationID);
		System.out.println("Please place the product on the scale.");
		// wait for customer to weight the product
		new_weight = checkoutStationUnits.get(stationID).getSelfCheckoutStationHardware().scanningArea.getCurrentWeight();
		checkoutStationUnits.get(stationID).getSelfCheckoutData().addProductToCheckout(attendProd, new_weight);
		
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
	
	public void updatePrinterInk(int stationID, int amount) throws OverloadException
	{
		blockStation(stationID);
		checkoutStationUnits.get(stationID).getSelfCheckoutStationHardware().printer.addInk(amount);
		// wait for the customer/attendant to press continue after the ink has been filled
				// once conitune has been pressed and no errors after adding the ink then the station
				// will be unblocked and the customer can continue
		unBlockStation(stationID);
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
	
	public void updatePrinterPaper(int stationID, int units) throws OverloadException 
	{
		blockStation(stationID);
		checkoutStationUnits.get(stationID).getSelfCheckoutStationHardware().printer.addPaper(units);
		// wait for the customer/attendant to press continue after the paper has been added
		// once conitune has been pressed and no errors after adding the paper then the station
		// will be unblocked and the customer can continue
		unBlockStation(stationID);
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
	//=============================================Yiannis Hontzias=============================================


}
