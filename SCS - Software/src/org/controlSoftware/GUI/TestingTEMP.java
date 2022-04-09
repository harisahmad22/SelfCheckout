package org.controlSoftware.GUI;

import java.math.BigDecimal;

import org.driver.AttendantData.AttendantState;
import org.driver.AttendantUnit;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutStationUnit;
import org.driver.SelfCheckoutData.StationState;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class TestingTEMP {

	public static void main(String[] args) throws InterruptedException, OverloadException {
		//SelfCheckoutStationUnit unit = new SelfCheckoutStationUnit();
		
		SelfCheckoutStationUnit unit = new SelfCheckoutStationUnit(1);
		SelfCheckoutStation station = unit.getSelfCheckoutStation();
		SelfCheckoutData data = unit.getSelfCheckoutData();
		
		AttendantUnit attendant = new AttendantUnit();
		
		attendant.attachCheckoutStationUnit(unit);
		
		attendant.getAttendantData().changeState(AttendantState.ACTIVE);
		
		station.printer.addInk(500);
		station.printer.addPaper(250);
		
		unit.getSelfCheckoutData().setTotalDue(new BigDecimal("20.00"));
		
		System.out.println(data.getCurrentState());
		data.changeState(StationState.WELCOME);
		System.out.println(data.getCurrentState());
		//CardOptionGUI gui = new CardOptionGUI(station, data);
		//gui.showCardOptionGUI();
		
	}

}
