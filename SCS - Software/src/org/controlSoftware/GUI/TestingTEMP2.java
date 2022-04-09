package org.controlSoftware.GUI;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.driver.AttendantData;
import org.driver.AttendantData.AttendantState;
import org.driver.AttendantUnit;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutStationUnit;
import org.driver.SelfCheckoutData.StationState;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SupervisionStation;

public class TestingTEMP2 {

	public static void main(String[] args) throws InterruptedException, OverloadException {
		//SelfCheckoutStationUnit unit = new SelfCheckoutStationUnit();
		
		AttendantUnit aUnit = new AttendantUnit();
		
		SelfCheckoutStationUnit sUnit1 = new SelfCheckoutStationUnit(1);
		SelfCheckoutStationUnit sUnit2 = new SelfCheckoutStationUnit(2);
		
		ArrayList<SelfCheckoutStationUnit> checkoutStations = new ArrayList<SelfCheckoutStationUnit>();
		checkoutStations.add(sUnit1);
		checkoutStations.add(sUnit2);
		
		aUnit.attachCheckoutStationUnits(checkoutStations);
		
		sUnit1.getSelfCheckoutData().changeState(StationState.WELCOME);
		sUnit2.getSelfCheckoutData().changeState(StationState.WELCOME);
		
		AttendantData data = aUnit.getAttendantData();
		
		data.changeState(AttendantState.STATIONS);

		//CardOptionGUI gui = new CardOptionGUI(station, data);
		//gui.showCardOptionGUI();
		
	}

}
