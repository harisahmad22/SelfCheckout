package org.controlSoftware.GUI;

import javax.swing.JFrame;

import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SupervisionStation;

public class SupervisorGUIMaster {
	private SupervisionStation scs;
	
	private JFrame frame;
	
	public SupervisorGUIMaster(SupervisionStation station) {
		scs = station;
		frame = scs.screen.getFrame();
	}
}
