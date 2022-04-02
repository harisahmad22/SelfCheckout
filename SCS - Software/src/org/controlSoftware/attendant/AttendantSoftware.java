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
import org.lsmr.selfcheckout.devices.AbstractDevice;
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

	private TouchScreen touchScreenDevice;
//	private JFrame guiFrame;
	private SupervisionStation supervisionStation;
	
	
	public AttendantSoftware(SupervisionStation supervisionStation, TouchScreen touchScreenDevice)
	{
		this.supervisionStation = supervisionStation;
		this.touchScreenDevice = touchScreenDevice;
	}
	
	public void overrideWeightIssue(int stationIndex) {
		// Idea: Set the weight_valid flag for the corresponding station to true
		// maybe check if station is in checkout or just scanned something also
		List<SelfCheckoutStation> staionList = supervisionStation.supervisedStations();
		
	}
	
	public void blockStation(SelfCheckoutStationUnit station)
	{
		
	}

	

}
