package org.controlSoftware.GUI;

import javax.swing.JFrame;

import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutStationUnit;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class SelfCheckoutGUIMaster {
	private SelfCheckoutStation station;
	private SelfCheckoutData stationData;
	
	private JFrame frame;
	
	private SelfCheckoutGUIWelcome welcomeGUI;
	// private SelfCheckoutGUIOther otherGUI;
	// private SelfCheckoutGUIOther otherGUI;
	// private SelfCheckoutGUIOther otherGUI;
	// private SelfCheckoutGUIOther otherGUI;
	
	public SelfCheckoutGUIMaster(SelfCheckoutStation newStation, SelfCheckoutData newData) {
		station = newStation;
		stationData = newData;
		stationData.registerGUI(this);
		
		frame = station.screen.getFrame();
		
		welcomeGUI = new SelfCheckoutGUIWelcome(station, stationData);
		// otherGUI = new SelfCheckoutOtherGUI(stationUnit)
		// otherGUI = new SelfCheckoutOtherGUI(stationUnit)
		// otherGUI = new SelfCheckoutOtherGUI(stationUnit)
		// otherGUI = new SelfCheckoutOtherGUI(stationUnit)
	}
	
	public void stateChanged() {
		welcomeGUI.stateChanged();
		// otherGUI.stateChanged;
		// otherGUI.stateChanged;
		// otherGUI.stateChanged;
		// otherGUI.stateChanged;
	}
}
