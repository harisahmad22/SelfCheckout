package org.iter3Testing;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.math.BigDecimal;

import javax.swing.JFrame;

import org.driver.*;
import org.driver.SelfCheckoutData.*;
import org.driver.databases.PLUProductDatabase;
import org.driver.databases.PLUTestProducts;
import org.driver.databases.TestBarcodedProducts;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.*;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class CustomerGUITesting {
	SelfCheckoutStationUnit unit;
	SelfCheckoutStation station;
	SelfCheckoutData data;
	JFrame frame;
	Robot bot;
	PLUCodedProduct banana;
	Item bananaItem;
	TestBarcodedProducts testProducts;
	PLUTestProducts pluProducts;
	BarcodedProduct milkJug;
	BarcodedItem milkJugItem;
	
	@Before
	public void setUp() {
		unit = new SelfCheckoutStationUnit(1);
		unit.getSelfCheckoutData().changeState(StationState.WELCOME);
		station = unit.getSelfCheckoutStation();
		data = unit.getSelfCheckoutData();
		frame = station.screen.getFrame();
		
		testProducts = new TestBarcodedProducts();
		pluProducts = new PLUTestProducts();
		
		banana = data.getPLUTestProducts().getPLUProductList().get(2);
		bananaItem = pluProducts.getItem(banana, 500);

		milkJug = data.getBarcodedProductDatabase()
					.get(testProducts.getBarcodeList().get(0));
		milkJugItem = testProducts.getItem(milkJug);
		
		try {
			bot = new Robot();
		} catch (AWTException e) {}
	}
	
	@Test
	public void allStates() {
		//Welcome screen
		bot.mouseMove(450,350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//Letter search
		bot.mouseMove(800,100);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//Return
		bot.mouseMove(800,100);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//PLU search
		bot.mouseMove(800,300);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			
		//Return
		bot.mouseMove(800,100);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//Checkout
		bot.mouseMove(800,500);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//Return
		bot.mouseMove(250, 250);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//Checkout
		bot.mouseMove(800,500);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Checkout final
		bot.mouseMove(500,250);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//data.getStationHardware().baggingArea.add(bananaItem);
	}
}
