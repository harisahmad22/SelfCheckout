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
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.Keyboard;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SupervisionStation;
import org.lsmr.selfcheckout.devices.TouchScreen;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.TouchScreenObserver;

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
	private AttendantData attendantData;
	private Keyboard keyboard;
	private ArrayList<SelfCheckoutStationUnit> checkoutStationUnits;
	
	
	
	public AttendantSoftware(SupervisionStation supervisionStation, AttendantData attendantData, ArrayList<SelfCheckoutStationUnit> checkoutStationUnits)
	{
		this.supervisionStation = supervisionStation;
		this.attendantData = attendantData;
		this.touchScreenDevice = supervisionStation.screen;
		this.keyboard = supervisionStation.keyboard;
		
		//Use this array list to access all the data/software/hardware of any connected checkout station
		this.checkoutStationUnits = checkoutStationUnits;
	}
	
	public void overrideWeightIssue(int stationIndex) {
		// Idea: Set the weight_valid flag for the corresponding station to true
		// maybe check if station is in checkout or just scanned something also
		SelfCheckoutStationUnit stationToOverride = checkoutStationUnits.get(stationIndex);
		stationToOverride.getSelfCheckoutSoftware().performAttendantWeightOverride();
		
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
	
	public void unBlockStation(SelfCheckoutStationUnit station)
	{
		System.out.println("Blocking station: " + station.getStationID());
		station.getSelfCheckoutSoftware().unBlockStation();
	}
	
	public void unBlockStation(int stationID)
	{
		System.out.println("Blocking station: " + stationID);
		checkoutStationUnits.get(stationID).getSelfCheckoutSoftware().unBlockStation();
	}
	
	public void unBlockStation(String stationID)
	{
		System.out.println("Unblocking station: " + stationID);
		checkoutStationUnits.get(Integer.parseInt(stationID)).getSelfCheckoutSoftware().unBlockStation();
	}

	public void setCheckoutStationUnits(ArrayList<SelfCheckoutStationUnit> newCheckoutStationUnits) {
		this.checkoutStationUnits = newCheckoutStationUnits;
		
	}
	public ArrayList<SelfCheckoutStationUnit> getCheckoutStationUnits() {
		return this.checkoutStationUnits;
		
	}

	public AttendantData getAttendantData() {
		return attendantData;
	}
	
	

	

}
