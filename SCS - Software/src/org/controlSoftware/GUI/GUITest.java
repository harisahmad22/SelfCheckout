package org.controlSoftware.GUI;

import org.driver.SelfCheckoutData;
import org.iter2Testing.DummySelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

/**
 * @author Harrison Drew
 * Stub GUI Test Method. To be removed upon release
 */
public class GUITest {

	public static void main(String[] args) {
		SelfCheckoutStation station = new DummySelfCheckoutStation();
		SelfCheckoutData data = new SelfCheckoutData(station);
		CardOptionGUI gui = new CardOptionGUI(station, data);
		gui.showCardOptionGUI();
		
	}

}