package org.controlSoftwareGUI;

import org.iter2Testing.DummySelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class GUITest {

	public static void main(String[] args) {
		SelfCheckoutStation station = new DummySelfCheckoutStation();
		PaymentOptionGUI gui = new PaymentOptionGUI(station);
		gui.showPaymentOptionGUI();
	}

}
