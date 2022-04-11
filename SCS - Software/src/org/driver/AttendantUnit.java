package org.driver;

import java.util.ArrayList;

import org.controlSoftware.GUI.SelfCheckoutGUIMaster;
import org.controlSoftware.GUI.SupervisorGUIMaster;
import org.controlSoftware.attendant.AttendantSoftware;
import org.driver.AttendantData.AttendantState;
import org.driver.SelfCheckoutData.StationState;
import org.driver.databases.PLUProductDatabase;
import org.driver.databases.PLUTestProducts;
import org.driver.databases.TestBarcodedProducts;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SupervisionStation;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SupervisionStation;


public class AttendantUnit {
	
	private SupervisionStation attendantStation;
	private AttendantSoftware attendantSoftware;
	private ArrayList<SelfCheckoutStationUnit> checkoutStations;
	private AttendantData attendantData;
	private SupervisorGUIMaster gui;
	
	public AttendantUnit()
	{
		checkoutStations = new ArrayList<SelfCheckoutStationUnit>();
		this.attendantData = new AttendantData();
		this.attendantData.attachCheckoutStationUnits(checkoutStations);
		this.attendantStation = new SupervisionStation();
		
		ArrayList<PLUCodedProduct> testProducts = new PLUTestProducts().getPLUProductList();
		this.attendantSoftware = new AttendantSoftware(attendantStation, checkoutStations, new PLUProductDatabase(testProducts));
		//Have to add in attendant touch screen software 
		
		this.gui = new SupervisorGUIMaster(attendantStation, attendantData);
		this.attendantData.registerGUI(gui);
		this.attendantData.registerSoftware(attendantSoftware);
	}
	public void attachCheckoutStationUnit(SelfCheckoutStationUnit unit)
	{
		this.checkoutStations.add(unit);
		
		this.attendantStation.remove(unit.getSelfCheckoutStationHardware());
		
		this.attendantStation.add(unit.getSelfCheckoutStationHardware());
		
	}
	public void attachCheckoutStationUnits(ArrayList<SelfCheckoutStationUnit> unitList)
	{
		checkoutStations = unitList;
		attendantData.attachCheckoutStationUnits(unitList);
		
		this.attendantSoftware.setCheckoutStationUnits(checkoutStations);
		

		for (SelfCheckoutStation station : this.attendantStation.supervisedStations())
		{
			this.attendantStation.remove(station);
		}
		
		for (SelfCheckoutStationUnit unit : checkoutStations)
		{
			this.attendantStation.add(unit.getSelfCheckoutStationHardware());
		}
	}

	public void stationStarted(int stationID) {
		//INFORM GUI TO DISPLAY NOTIFICATION OF STATION STARTUP
		
		//For now just print to console
		System.out.println("Station " + stationID + " has been successfully started up!");
		
	}
	
	public void stationShutdown(int stationID) {
		//INFORM GUI TO DISPLAY NOTIFICATION OF STATION shutdown
		
		//For now just print to console
		System.out.println("Station " + stationID + " is being shutdown!");		
	}


//	public void logInStation(String attendantIdEntered,  String passwordEntered)
//	{
//		//System.out.println("Starting station: " + station.getStationID());
//        System.out.println("Logging in");
//
//        ArrayList<String> AttendantIdStored = station.getAttendantID();
//        ArrayList<String> PasswordStored = station.getPassword();
//        for(int i = 0 ; i < AttendantIdStored.size(); i++){
//            if((AttendantIdStored.get(i) == attendantIdEntered) && (PasswordStored.get(i) == passwordEntered)){
//                station.getSelfCheckoutSoftware().LogInStation(attendantIdEntered, passwordEntered);
//                unBlockStation(station);
//                break;
//            }
//        }
//        System.out.println("Error! Wrong ID or password. Fail to log in.");
//	}
//	
//	
//	public void logOutStation(SelfCheckoutStationUnit station)
//	{
//		System.out.println("Log out: " + station.getStationID());
//		station.getSelfCheckoutSoftware().LogOutStation();
//        blockStation(station);
//	}

	public void stationLogin(String AttendantID, String password) {
		//INFORM GUI TO DISPLAY NOTIFICATION OF STATION STARTUP
		
		//For now just print to console
		System.out.println("Attendant: " + AttendantID + " has been successfully logged in!");
		
	}
	
	public void stationLogout() {
		//INFORM GUI TO DISPLAY NOTIFICATION OF STATION shutdown
		
		//For now just print to console
		System.out.println("Successfully logged out!");		
	}


	
	public void handleNoPaper(int stationID) {
		// SIGNAL GUI TO DISPLAY LOW INK ALERT
		// For now, print to console
		attendantData.setGuiBuffer("Station " + stationID + " is out of paper in its receipt printer!");
		attendantData.changeState(AttendantState.NOTIFIED_BY_STATION);
	}
	
	public void handleNoInk(int stationID) {
		// SIGNAL GUI TO DISPLAY LOW INK ALERT
		// For now, print to console
		attendantData.setGuiBuffer("Station " + stationID + " is out of ink in its receipt printer!");
		attendantData.changeState(AttendantState.NOTIFIED_BY_STATION);
	}
	
	public SupervisionStation getAttendantStation() {
		return attendantStation;
	}

	public void setAttendantStation(SupervisionStation attendantStation) {
		this.attendantStation = attendantStation;
	}

	public AttendantSoftware getAttendantSoftware() {
		return attendantSoftware;
	}
	
	public SupervisorGUIMaster getAttendantGUI() {
		return gui;
	}
	
	public AttendantData getAttendantData() {
		return attendantData;
	}

	public void setAttendantSoftware(AttendantSoftware attendantSoftware) {
		this.attendantSoftware = attendantSoftware;
	}

	public void displayMessage(String message) {
		System.out.println("Received a message from a station!");
		System.out.println(message);
		
	}

}
