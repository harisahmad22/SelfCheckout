package org.controlSoftware.GUI;

import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutStationUnit;
import org.driver.SelfCheckoutData.StationState;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class TestingTEMP {

	public static void main(String[] args) throws InterruptedException {
		//SelfCheckoutStationUnit unit = new SelfCheckoutStationUnit();
		
		SelfCheckoutStationUnit unit = new SelfCheckoutStationUnit(1);
		
		SelfCheckoutStation station = unit.getSelfCheckoutStation();
		SelfCheckoutData data = unit.getSelfCheckoutData();
		System.out.println(data.getCurrentState());
		data.changeState(StationState.WELCOME);
		System.out.println(data.getCurrentState());
		//CardOptionGUI gui = new CardOptionGUI(station, data);
		//gui.showCardOptionGUI();
		
	}

}
