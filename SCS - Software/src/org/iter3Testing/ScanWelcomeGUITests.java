package org.iter3Testing;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import javax.swing.JFrame;

import org.driver.*;
import org.driver.SelfCheckoutData.*;
import org.driver.databases.PLUTestProducts;
import org.driver.databases.TestBarcodedProducts;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.*;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class ScanWelcomeGUITests {
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
	BarcodedItem milkJugItem1;
	BarcodedItem milkJugItem2;
	
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
		milkJugItem1 = testProducts.getItem(milkJug);
		milkJugItem2 = testProducts.getItem(milkJug);
		try {
			station.printer.addInk(500);
			station.printer.addPaper(250);
		} catch (OverloadException e1) {}
		try {
			bot = new Robot();
		} catch (AWTException e) {}
	}
	
	/*
	 * Tests going through almost all the states
	 */
	@Test
	public void AllStates() {
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
		bot.mouseMove(650, 250);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//Checkout
		bot.mouseMove(800,500);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Checkout final
		bot.mouseMove(300,250);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// No bags
		bot.mouseMove(550, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Not a member
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
	}
	
	/*
	 * !!! ISSUES !!!
	 * Tests going through scanning states
	 */
	@Test
	public void ScanItemStates() {
		//Welcome screen
		bot.mouseMove(450,350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Scan and bag
		data.getStationHardware().mainScanner.scan(milkJugItem1);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		data.getStationHardware().baggingArea.add(milkJugItem1);
		
		//Scan and not bag
		data.getStationHardware().mainScanner.scan(milkJugItem2);
		bot.mouseMove(650,250);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
	}
	
	/*
	 * Goes through states in PLU method
	 */
	@Test
	public void PLUItemState() {		
		//Welcome screen
		bot.mouseMove(450,350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//PLU search
		bot.mouseMove(850,300);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//Press keypad
		bot.mouseMove(760, 260);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(880, 260);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(940, 260);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(760, 340);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(880, 550);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(760, 340);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//Go
		bot.mouseMove(940, 550);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		//Adds item
		data.getStationHardware().scanningArea.add(bananaItem);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		data.getStationHardware().scanningArea.remove(bananaItem);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(700,250);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//PLU search
		bot.mouseMove(850,300);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				
		//Press keypad
		bot.mouseMove(760, 260);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(760, 260);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);bot.mouseMove(760, 260);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);bot.mouseMove(760, 260);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//Search
		bot.mouseMove(940, 550);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		//Return
		bot.mouseMove(550, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
	}

	/*
	 * Goes through states in letter method
	 */
	@Test
	public void LetterItemState() {
		//Welcome screen
		bot.mouseMove(450,350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// gets the item
		GetBanana();
		
	}

	/*
	 * Goes through states concerning member cards
	 */
	@Test
	public void MemberStates() {
		//Moving to member screens
		bot.mouseMove(450,350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(800,500);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(300,250);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(550, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Yes member
		bot.mouseMove(350, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Cancel
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Yes member
		bot.mouseMove(350, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Manual input
		bot.mouseMove(550, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Press Numpad
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Delete
		bot.mouseMove(400, 400);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Press Numpad
		bot.mouseMove(550, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Press go
		bot.mouseMove(600, 550);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Retry
		bot.mouseMove(550, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Manual input
		bot.mouseMove(350, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Yes member
		bot.mouseMove(350, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Manual input
		bot.mouseMove(550, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Cancel
		bot.mouseMove(350, 550);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
	}
	
	/*
	 * Goes through states concerning bagging
	 */
	@Test
	public void BaggingStates() {
		// Moving to bagging screens
		bot.mouseMove(450,350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(800,500);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(300,250);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//Yes
		bot.mouseMove(300,350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//Cancel
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//Yes
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//Add bags
		bot.mouseMove(600,350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
	}
	
	/*
	 * !!! ISSUES !!!
	 * Goes through weightIssueScreen
	 */
	@Test
	public void WeightState() {
		//Welcome screen
		bot.mouseMove(450,350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		data.getStationHardware().baggingArea.add(milkJugItem1);
	}
	
	/*
	 * Tests inactive state
	 */
	@Test
	public void InactiveState() {
		data.changeState(StationState.INACTIVE);
	}
	
	/*
	 * Tests blocked state
	 */
	@Test
	public void BlockedState() {
		data.changeState(StationState.BLOCKED);	
	}

	/*
	 * Tests finished state
	 */
	@Test
	public void FinishState() {
		//Moves to the end of checkout
		bot.mouseMove(450,350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(800,500);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(300,250);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(550, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(550, 200);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseMove(350, 400);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
	}
	
	public void GetBanana() {
		//Letter search
		bot.mouseMove(800,100);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//List select
		bot.mouseMove(800,300);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		//Search
		bot.mouseMove(800,550);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Click on Banana
		bot.mouseMove(100,80);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Get item
		bot.mouseMove(900,550);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		//adds item
		data.getStationHardware().scanningArea.add(bananaItem);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		data.getStationHardware().scanningArea.remove(bananaItem);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(700,250);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
	}
}