package org.driver;

import org.driver.AttendantData.AttendantState;
import org.driver.SelfCheckoutData.StationState;
import org.lsmr.selfcheckout.devices.OverloadException;

import java.util.ArrayList;

import org.controlSoftware.GUI.SupervisorGUIMaster;
import org.controlSoftware.attendant.AttendantSoftware;
//import org.driver.SelfCheckoutData.StationState;\
import org.lsmr.selfcheckout.devices.SupervisionStation;

public class AttendantData {
	// private SupervisionStation station;
	
	// Buffer for passing values between gui and logic
	private String guiBuffer = null;
	
	private SupervisorGUIMaster gui;
	private AttendantSoftware software;
	
	private ArrayList<SelfCheckoutStationUnit> checkoutStations;
	
	public AttendantData() {
		// station = newStation;
	}

	//Put states in with methods for changing state
	
	public enum AttendantState {
		// Current GUI associated states. Can be changed in GUI to match implementation. Not sure what they are there.
		START, TEST_LOGIN, STATIONS, MAINTENANCE, WEIGHT_ERROR, REFILL_PAPER, REFILL_INK, EMPTY_COINS, EMPTY_NOTES, REFILL_COINS, REFILL_NOTES,
		BARCODE, BARCODE_CHECK, PLUCODE, PLU_CHECK, CATALOGUE, 
		
		// General error state. No implementation yet. Potentially when item is not bagged? Notify attendant?
		// Maybe error sub-states are required? Maintenance state?
		ERROR, 
		
		//State that Checkout station will default to on initialization
		//Represents the 'off' state, No GUI.
		//Any other state would represent an 'on' state.
		//This State can only be entered if system is in WELCOME state. (Not being used by a customer)
		//Later could have all system's methods except startupStation() not work if the state == INACTIVE
		INACTIVE,
		
		//Active state, the default state that on transition informs GUI to display all attendant's options
		ACTIVE,
		
		//Log in state, this state is the state we enter upon startup
		//GUI will display a keypad that attendant uses to enter in their ID
		//If the given ID is valid the system transitions to the ACTIVE state
		LOG_IN,
		
		//State for when attendant has chosen to perform a weight override
		//Will inform GUI to display a list of all stations connected to attendant as buttons
		//upon pressing one of the buttons, attendant software handles informing that station of the override
		//And state will return to ACTIVE
		STATIONS_TO_OVERRIDE_WEIGHT,
		
		//State for when attendant wants to remove an item from a station
		//Will inform GUI to display a list of all stations connected to attendant
		STATIONS_TO_REMOVE_ITEM,
		
		//State for when attendant has chosen the station to remove an item from 
		//Will inform GUI to display a list of all the products that have been added 
		//to that station
		//Upon choosing an item to be removed, the particular station's list of added products
		//is updated, as well as all of the stations expected weights
		//After removing an item, return to the ACTIVE state
		REMOVE_ITEM_AT_STATION,
		
		//State for when attendant wants to lookup an item from a station
		//Will inform GUI to display a list of all stations connected to attendant
		STATIONS_TO_LOOKUP_ITEM,
		
		//State for when attendant has chosen the station to lookup an item from 
		//Will inform GUI to display a the PLU/product lookup (Reuse the code from customer station)
		//Upon choosing an item, call the method responsible for adding a chosen product to it's list/dictionary 
		//of added products on the chosen station
		//After choosing an item, return to the ACTIVE state
		PRODUCT_LOOKUP,
		
		//State for when attendant wants to block a station to empty or refill its devices
		//(Printer, coin/banknote dispensers and storage units)
		//Will inform GUI to display a list of all stations connected to attendant
		STATIONS_TO_EMPTY_OR_REFILL,
		
		//State for when attendant has chosen the station to refill its receipt printer
		//Chosen station will be blocked and attendant station will display a new GUI window with a Continue button
		//Once the attendant presses the continue button, the station is unblocked and the attendant station returns to the ACTIVE state 
		EMPTY_OR_REFILL_STATION,
		
		//State for when a station notifies the attendant of some event, will inform the GUI to display a window with a 
		//message describing the issue/event.
		NOTIFIED_BY_STATION;

	}
	
	private AttendantState currentState = AttendantState.INACTIVE;
	private AttendantState previousState;
	
	public void registerGUI(SupervisorGUIMaster newGui) {
		gui = newGui;
	}
	
	public void registerSoftware(AttendantSoftware newSoftware) {
		software = newSoftware;
	}
	
	public void setGuiBuffer(String text) {
		guiBuffer = text;
		System.out.println("GUI buffer in attendant data set to " + guiBuffer);
	}
	public String getGuiBuffer() {
		return guiBuffer;
	}
	
	// Changes to new state while properly exiting old one (enabling/disabling relevant hardware)
	public void changeState(AttendantState targetState) {
		// Disable hardware for old state
		exitState(getCurrentState(), targetState);
		// Enable hardware for new state
		switch(targetState) {
		
		case START:
			break;
		case STATIONS:
			break;
		case MAINTENANCE:
			break;
		case WEIGHT_ERROR:
			break;
		
		case INACTIVE:	
			//SIGNAL GUI TO CLOSE ALL WINDOWS
			break;
		
		case ACTIVE:
			//SIGNAL GUI TO DISPLAY MAIN ATTENDANT WINDOW
			break;
			
		case LOG_IN:
			//SIGNAL GUI TO DISPLAY LOG IN WINDOW  
			break;
			
		case ERROR:
			break;
			
		case STATIONS_TO_OVERRIDE_WEIGHT:
			break;
		
		case STATIONS_TO_REMOVE_ITEM: 
			break;
			
		case REMOVE_ITEM_AT_STATION:
			break;
			
		case STATIONS_TO_LOOKUP_ITEM:
			break;

		case PRODUCT_LOOKUP:
			break;
		
		case STATIONS_TO_EMPTY_OR_REFILL:
			break;
		 
		case EMPTY_OR_REFILL_STATION: 
			break;
		
		case NOTIFIED_BY_STATION:
			previousState = currentState;
			break;
			
		default:
			break;
		} 
		//Made it here, assume target state is valid
		setCurrentState(targetState);

		notifyStateChanged();
	}
	

	private void exitState(AttendantState currentState, AttendantState newState) {
		switch(currentState) {
		
		case INACTIVE:
			//Let the enter state method handle the transition out of this state
			break;
		
		case ACTIVE:
			//Let the enter state method handle the transition out of this state
			break;
			
		case LOG_IN:
			break;
			
		case ERROR:
			break;
			
		case STATIONS_TO_OVERRIDE_WEIGHT:
			break;
		
		case STATIONS_TO_REMOVE_ITEM: 
			break;
			
		case REMOVE_ITEM_AT_STATION:
			break;
			
		case STATIONS_TO_LOOKUP_ITEM:
			break;

		case PRODUCT_LOOKUP:
			break;
		
		case STATIONS_TO_EMPTY_OR_REFILL:
			break;
		 
		case EMPTY_OR_REFILL_STATION: 
			break;
		
		case NOTIFIED_BY_STATION:
			break;
			
		default:
			break;
		}
	}
	
	private void notifyStateChanged() {
		gui.stateChanged();
	}
	public AttendantState getCurrentState() {
		return currentState;
	}
	public AttendantState getPreviousState() {
		return previousState;
	}

	private void setCurrentState(AttendantState targetState) {
		this.currentState = targetState;
	}
	
	public void attachCheckoutStationUnits(ArrayList<SelfCheckoutStationUnit> unitList)
	{
		checkoutStations = unitList;
	}
	
	public SelfCheckoutStationUnit getUnitAt(int index) {
		if (checkoutStations.size() <= index) {
			return null;
		}
		else {
			return checkoutStations.get(index);
		}
	}
	
	public AttendantSoftware getSoftware() {
		return software;
	}

	public int getUnitIndex(SelfCheckoutStationUnit unit) {
		return checkoutStations.indexOf(unit);
	}
}
