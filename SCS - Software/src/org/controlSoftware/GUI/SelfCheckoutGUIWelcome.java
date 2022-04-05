package org.controlSoftware.GUI;

import javax.swing.JFrame;

import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutStationUnit;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class SelfCheckoutGUIWelcome {
	private SelfCheckoutStation station;
	private SelfCheckoutData stationData;
	
	private JFrame frame;
	
	public SelfCheckoutGUIWelcome(SelfCheckoutStation newStation, SelfCheckoutData newData) {
		station = newStation;
		stationData = newData;
		
		frame = station.screen.getFrame();
	}
	
	public void stateChanged() {
		switch (stationData.getState()) {
		
		case WELCOME:
			System.out.println("GUI Welcome recognized state changed to welcome")
			
		
		default:
			break;
		}
	}
	
	
}
