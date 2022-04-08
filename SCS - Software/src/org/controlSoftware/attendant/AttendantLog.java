
//Jingke Huang - 30115284

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

//import apple.laf.JRSUIConstants.Size;

public class AttendantLog {

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
	
	
	public AttendantLog(SupervisionStation supervisionStation, ArrayList<SelfCheckoutStationUnit> checkoutStationUnits)
	{
		this.supervisionStation = supervisionStation;
		this.touchScreenDevice = supervisionStation.screen;
		this.keyboard = supervisionStation.keyboard;
		
		//Use this array list to access all the data/software/hardware of any connected checkout station
		this.checkoutStationUnits = checkoutStationUnits;
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
	
	
}