package org.controlSoftware.GUI;

import javax.swing.JFrame;

import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutStationUnit;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class SelfCheckoutGUIMaster {
	private SelfCheckoutStation station;
	private SelfCheckoutData stationData;
	
	private JFrame frame;
	
	private final int WIDTH = 1000;
	private final int HEIGHT = 600;
	
	private SelfCheckoutGUIWelcome welcomeGUI;
	private ScanningScreenGUI scanningGUI;
	private SelfCheckoutGUIPayments paymentGUI;
	private PaymentOptionGUI payOptionsGUI;
	// private SelfCheckoutGUIOther otherGUI;
	
	public SelfCheckoutGUIMaster(SelfCheckoutStation newStation, SelfCheckoutData newData) {
		station = newStation;
		stationData = newData;
		stationData.registerGUI(this);
		
		frame = station.screen.getFrame();
		frame.setSize(WIDTH,HEIGHT);
		frame.setLayout(null);
		
		welcomeGUI = new SelfCheckoutGUIWelcome(station, stationData);
		scanningGUI = new ScanningScreenGUI(station, stationData);
		paymentGUI = new SelfCheckoutGUIPayments(station, stationData);
		payOptionsGUI = new PaymentOptionGUI(station, stationData);
		// otherGUI = new SelfCheckoutOtherGUI(stationUnit)
	}
	
	public void stateChanged() {
		//Wipe the current screen
		frame.getContentPane().removeAll();
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();

		welcomeGUI.stateChanged();
		scanningGUI.stateChanged();
		paymentGUI.stateChanged();
		payOptionsGUI.stateChanged();
		// otherGUI.stateChanged;
	}
}
