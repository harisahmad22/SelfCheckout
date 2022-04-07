package org.controlSoftwareGUI;

import javax.swing.JFrame;

import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class SelfCheckoutGUIMaster {
	private SelfCheckoutStation scs;
	
	private JFrame frame;
	
	public SelfCheckoutGUIMaster(SelfCheckoutStation station) {
		scs = station;
		frame = scs.screen.getFrame();
	}


}
