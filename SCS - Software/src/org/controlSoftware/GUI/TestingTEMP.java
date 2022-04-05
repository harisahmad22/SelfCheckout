package org.controlSoftware.GUI;

import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutStationUnit;
import org.driver.SelfCheckoutData.State;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class TestingTEMP {

	public static void main(String[] args) throws InterruptedException {
		//SelfCheckoutStationUnit unit = new SelfCheckoutStationUnit();
		
		SelfCheckoutStationUnit unit = new SelfCheckoutStationUnit();
		
		SelfCheckoutStation station = unit.getSelfCheckoutStation();
		SelfCheckoutData data = unit.getSelfCheckoutData();
		
		data.changeState(State.WELCOME);
	
	}

}
