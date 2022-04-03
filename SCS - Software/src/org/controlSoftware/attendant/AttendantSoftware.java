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
	private Keyboard keyboard;
	private ArrayList<SelfCheckoutStationUnit> checkoutStationUnits;
	
	
	public AttendantSoftware(SupervisionStation supervisionStation, ArrayList<SelfCheckoutStationUnit> checkoutStationUnits)
	{
		this.supervisionStation = supervisionStation;
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
	
	public void blockStation(SelfCheckoutStationUnit station)
	{
		//TODO not implemented yet
	}

	

}
