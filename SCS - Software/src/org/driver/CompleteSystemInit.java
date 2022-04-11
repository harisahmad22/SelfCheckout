package org.driver;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.driver.AttendantData;
import org.driver.AttendantData.AttendantState;
import org.driver.AttendantUnit;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutStationUnit;
import org.driver.SelfCheckoutData.StationState;
import org.driver.databases.TestBarcodedProducts;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SupervisionStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class CompleteSystemInit {

	public static void main(String[] args) throws InterruptedException, OverloadException {
		//SelfCheckoutStationUnit unit = new SelfCheckoutStationUnit();
		
		AttendantUnit aUnit = new AttendantUnit();
		
		SelfCheckoutStationUnit sUnit1 = new SelfCheckoutStationUnit(0);
		sUnit1.getSelfCheckoutData().setAttendantUnit(aUnit);
		sUnit1.getSelfCheckoutData().setThisUnit(sUnit1);
		aUnit.attachCheckoutStationUnit(sUnit1);
		sUnit1.getSelfCheckoutData().changeState(StationState.INACTIVE);
		
		SelfCheckoutStationUnit sUnit2 = new SelfCheckoutStationUnit(1);
		sUnit2.getSelfCheckoutData().changeState(StationState.INACTIVE);
		
		ArrayList<SelfCheckoutStationUnit> checkoutStations = new ArrayList<SelfCheckoutStationUnit>();
		checkoutStations.add(sUnit1);
		checkoutStations.add(sUnit2);
		
		aUnit.attachCheckoutStationUnit(sUnit2);
		
//		sUnit1.getSelfCheckoutData().changeState(StationState.WELCOME);
//		sUnit2.getSelfCheckoutData().changeState(StationState.WELCOME);

		AttendantData data = aUnit.getAttendantData();
		
		//aUnit.getAttendantGUI().setTargetStation(sUnit1);
		data.changeState(AttendantState.START);
		
		/*ArrayList<BarcodedProduct> testProducts = new TestBarcodedProducts().getBarcodedProductList();
		sUnit1.getSelfCheckoutData().debugAddProductToCheckout(testProducts.get(0));
		sUnit1.getSelfCheckoutData().debugAddProductToCheckout(testProducts.get(1));
		sUnit1.getSelfCheckoutData().debugAddProductToCheckout(testProducts.get(2));
		sUnit1.getSelfCheckoutData().debugAddProductToCheckout(testProducts.get(3));
		sUnit1.getSelfCheckoutData().debugAddProductToCheckout(testProducts.get(4));
		sUnit1.getSelfCheckoutData().debugAddProductToCheckout(testProducts.get(5));*/
		
		//CardOptionGUI gui = new CardOptionGUI(station, data);
		//gui.showCardOptionGUI();
		
	}

}