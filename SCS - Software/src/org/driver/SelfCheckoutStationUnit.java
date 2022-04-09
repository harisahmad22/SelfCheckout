package org.driver;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import org.controlSoftware.GUI.SelfCheckoutGUIMaster;
import org.controlSoftware.general.TouchScreenSoftware;
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
	private TouchScreenSoftware touchScreenSoftware;
	
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
	
	private static Banknote fiveDollarBanknote = new Banknote(CAD, 5);
	private static Banknote tenDollarBanknote = new Banknote(CAD, 10);
	private static Banknote twentyDollarBanknote = new Banknote(CAD, 20);
	private static Banknote fiveDollarBanknoteUSD = new Banknote(Currency.getInstance("USD"), 5);
	private static Banknote twelveDollarBanknote = new Banknote(CAD, 12);
	private static Coin nickel = new Coin(CAD, new BigDecimal("0.05"));
	private static Coin dime = new Coin(CAD, new BigDecimal("0.10"));
	private static Coin quarter = new Coin(CAD, new BigDecimal("0.25"));
	private static Coin loonie = new Coin(CAD, new BigDecimal("1.00"));
	private static Coin toonie = new Coin(CAD, new BigDecimal("2.00"));
	

	public SelfCheckoutStationUnit(int stationID) {
		this.stationID = stationID;
		this.station = new SelfCheckoutStation(CAD, banknoteDenominations, coinDenominations, scaleMaximumWeight, scaleSensitivity);
		
		loadStation();
		
		//Initialize Data + Software
		this.stationData = new SelfCheckoutData(station);
		this.touchScreen = station.screen;
		
		//Initialize GUI
		this.stationGUI = new SelfCheckoutGUIMaster(station, stationData);
		stationData.registerGUI(stationGUI);

		//TouchScreenSoftware will attach itself to the touch screen
		this.touchScreenSoftware = new TouchScreenSoftware(System.in, touchScreen, stationData);
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
	
	public TouchScreenSoftware getTouchScreenSoftware()
	{
		return touchScreenSoftware;
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
	//==============================ATTENDANT RELATED METHODS===================================

	public void informAttendantLogin(String AttendantID, String password) {
		this.attendantUnit.stationLogin(AttendantID, password);
		
	}

	public void informAttendantLogout() {
		this.attendantUnit.stationLogout();
		
	}

	public SelfCheckoutStation getSelfCheckoutStation() {
		return station;
	}
}
