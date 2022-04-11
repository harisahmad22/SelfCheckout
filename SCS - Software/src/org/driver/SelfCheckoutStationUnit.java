package org.driver;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import org.controlSoftware.GUI.SelfCheckoutGUIMaster;
import org.iter2Testing.DummySelfCheckoutStation;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.Keyboard;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.TouchScreen;

public class SelfCheckoutStationUnit {
	//This class will initialize a new SelfCheckoutStation along with the Touch Screen,
	//Keyboard, and software to make it all work together.
	
	private SelfCheckoutStation station;
	private SelfCheckoutData stationData;

	private SelfCheckoutSoftware stationSoftware;
	
	private AttendantUnit attendantUnit;

	private SelfCheckoutGUIMaster stationGUI;
	private TouchScreen touchScreen;
	
	private int stationID; //The Number of this station

	private ArrayList<String> AttendantID; // Attendant ID, datebase
	private ArrayList<String> Password;
	
	public static Currency CAD = Currency.getInstance("CAD");
	private static int[] banknoteDenominations = {50, 20, 10, 5};
	private static BigDecimal[] coinDenominations = {new BigDecimal("2.00"),
													 new BigDecimal("1.00"),
										  			 new BigDecimal("0.25"),
										  			 new BigDecimal("0.10"),
										  			 new BigDecimal("0.05")};
	private static int scaleMaximumWeight = 50000; //Set limit to 50,000 grams (50Kg)
	private static int scaleSensitivity = 10; //10 gram sensitivity
	
	

	public SelfCheckoutStationUnit(int stationID) {
		this.stationID = stationID;
		this.station = new SelfCheckoutStation(CAD, banknoteDenominations, coinDenominations, scaleMaximumWeight, scaleSensitivity);
		
		loadStation();
		
		//Initialize Data + Software
		this.stationData = new SelfCheckoutData(station);
		this.touchScreen = station.screen;
		
		//Initialize GUI
		this.stationGUI = new SelfCheckoutGUIMaster(station, stationData);
		this.stationData.registerGUI(stationGUI);

		//SelfCheckoutSoftware will attach the handlers to the hardware
		this.stationSoftware = new SelfCheckoutSoftware(this, stationData);
		this.stationData.attachStationSoftware(this.stationSoftware);
	}
	
	private void loadStation() {
		for (BigDecimal val : station.coinDispensers.keySet())
		{
			//Money Loading should be moved to testing/attendant methods 
			try { //Load half full
				for (int i = 0; i < 100; i++) { station.coinDispensers.get(val).load(new Coin(CAD, val)); }
				
			} catch (OverloadException e) {
				e.printStackTrace();
			};
		}
		for (int val : station.banknoteDispensers.keySet())
		{
			try { //Load half full
				for (int i = 0; i < 50; i++) { station.banknoteDispensers.get(val).load(new Banknote(CAD, val)); }
				
			} catch (OverloadException e) {
				e.printStackTrace();
			};
		}
		try {
			station.printer.addPaper(1 << 9); //Half full?
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			station.printer.addInk(1 << 19); //Half full?
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SelfCheckoutStationUnit getSelfCheckoutStationUnit()
	{
		return this;
	}
	
	public AttendantUnit getAttendantUnit()
	{
		return attendantUnit;
	}
	
	public SelfCheckoutStation getSelfCheckoutStationHardware()
	{
		return this.station;
	}
	
	public SelfCheckoutData getSelfCheckoutData()
	{
		return this.stationData;
	}
	
	public SelfCheckoutSoftware getSelfCheckoutSoftware()
	{
		return this.stationSoftware;
	}
	
	public TouchScreen getTouchScreen()
	{
		return touchScreen;
	}
	
	public static Currency getCurrency()
	{
		return CAD;
	}
	
	public int getStationID() {
		return stationID;
	}

	public ArrayList getAttendantID() {
		return AttendantID;
	}

	public ArrayList getPassword() {
		return Password;
	}
	//==============================ATTENDANT RELATED METHODS===================================
	
	public void attachAttendant(AttendantUnit attendantUnit) {
		this.attendantUnit = attendantUnit;
	}
	
	public void informAttendantOfStartup() {
		this.attendantUnit.stationStarted(this.stationID);
	}

	public void informAttendantOfShutdown() {
		this.attendantUnit.stationShutdown(this.stationID);
		
	}
	
	public void informAttendantOfNoPaper() {
		this.attendantUnit.handleNoPaper(this.stationID);
	}
	
	public void informAttendantOfNoInk() {
		this.attendantUnit.handleNoInk(this.stationID);
	}

	public void sendAttendantMessage(String message) {
		String id = "(Station ID: " + Integer.toString(getStationID()) + ") ";
		getAttendantUnit().displayMessage(id + message);
		
	}

	public void informAttendantLogin(String AttendantID, String password) {
		this.attendantUnit.stationLogin(AttendantID, password);
		
	}

	public void informAttendantLogout() {
		this.attendantUnit.stationLogout();
		
	}
	//==============================ATTENDANT RELATED METHODS===================================

	public SelfCheckoutStation getSelfCheckoutStation() {
		return station;
	}
	
	public static BigDecimal[] getCoinDenominations() {
		return coinDenominations;
	}
	
	public static int[] getBanknoteDenominations() {
		return banknoteDenominations;
	}
	
	public SelfCheckoutSoftware getSoftware() {
		return stationSoftware;
	}
}
