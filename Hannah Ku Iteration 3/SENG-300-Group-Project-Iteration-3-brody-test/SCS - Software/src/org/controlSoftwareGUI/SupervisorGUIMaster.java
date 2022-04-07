package org.controlSoftwareGUI;

import javax.swing.JFrame;

import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class SupervisorGUIMaster {
	private SelfCheckoutStation scs;
	
	private JFrame frame;
	
	public SupervisorGUIMaster(SelfCheckoutStation station) {
		scs = station;
		frame = scs.screen.getFrame();
	}
}
