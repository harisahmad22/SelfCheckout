package org.iter3Testing;

import static org.junit.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.controlSoftware.GUI.PaymentOptionGUI;
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
	PaymentOptionGUI gui;
	BigDecimal total;
	
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
		
		total = new BigDecimal(1.00);
		gui = new PaymentOptionGUI(station, data);
		data.setTotalDue(total);
		
		try {
			bot = new Robot();
		} catch (AWTException e) {}
	}
	
	@Test
	public void allStates() {
		//Welcome screen
		data.changeState(StationState.WELCOME);
		gui.stateChanged();
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
        		
        // Back
		
        data.changeState(StationState.PAYMENT_AMOUNT_PROMPT);
     	bot.mouseMove(290,440);
     	try{Thread.sleep(250);}catch(InterruptedException e){}
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
        
		// Full Payment
		
        try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(280,180);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);		
		
		// Debit Payment
		
		data.changeState(StationState.PAYMENT_MODE_PROMPT);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(555,205);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		// Credit Payment
		
		data.changeState(StationState.PAYMENT_MODE_PROMPT);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(105,205);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		// Cash Payment
		
		data.changeState(StationState.PAYMENT_MODE_PROMPT);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(105,355);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
						
		// Membership
		
		data.changeState(StationState.PAYMENT_MODE_PROMPT);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(350,490);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		// Gift Card
		
		data.changeState(StationState.PAYMENT_MODE_PROMPT);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(555,355);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		// Return
		
		data.changeState(StationState.PAYMENT_MODE_PROMPT);
		bot.mouseMove(280,430);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		// Attendant Button
		
		data.changeState(StationState.PAYMENT_MODE_PROMPT);
		bot.mouseMove(750,490);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		// Insufficient Funds		
		
		data.changeState(StationState.INSUFFICIENT_FUNDS);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(140,340);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		try{Thread.sleep(250);}catch(InterruptedException e){}
				
		data.changeState(StationState.INSUFFICIENT_FUNDS);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(440,340);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		// Partial Payment
		
		data.changeState(StationState.PAYMENT_AMOUNT_PROMPT);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(280,305);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		// Cancel Button
		
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(305, 490);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		try{Thread.sleep(250);}catch(InterruptedException e){}
			
		// Confirm Proper Payment
		
		data.changeState(StationState.PARTIAL_PAYMENT_KEYPAD);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(305,140);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		bot.mouseMove(305, 365);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		bot.mouseMove(305,140);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(555, 480);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		// Confirm Proper Payment
		
		data.changeState(StationState.PARTIAL_PAYMENT_KEYPAD);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(305,140);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseMove(555, 480);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try{Thread.sleep(250);}catch(InterruptedException e){}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		try{Thread.sleep(250);}catch(InterruptedException e){}
		
		
		
	}
	
	@Test
	public void showHidePaymentGUI() {
		gui.showPaymentOptionGUI();
		gui.hidePaymentOptionGUI();
	}
	
	@Test
	public void stateChangedPAYMENTMODEPROMPT() {
		data.setCurrentState(StationState.PAYMENT_MODE_PROMPT);
		gui.stateChanged();
		assertTrue(data.getCurrentState() == StationState.PAYMENT_MODE_PROMPT);
	}
	
	@Test
	public void stateChangedPAYMENTAMOUNTPROMPT() {
		data.setCurrentState(StationState.PAYMENT_AMOUNT_PROMPT);
		gui.stateChanged();
		assertTrue(data.getCurrentState() == StationState.PAYMENT_AMOUNT_PROMPT);
	}
	
	@Test
	public void stateChangedPARTIALPAYMENTKEYPAD() {
		data.setCurrentState(StationState.PARTIAL_PAYMENT_KEYPAD);
		gui.stateChanged();
		assertTrue(data.getCurrentState() == StationState.PARTIAL_PAYMENT_KEYPAD);
	}
	
	@Test
	public void stateChangedINSUFFICIENTFUNDS() {
		data.setCurrentState(StationState.INSUFFICIENT_FUNDS);
		gui.stateChanged();
		assertTrue(data.getCurrentState() == StationState.INSUFFICIENT_FUNDS);
	}
	
	@Test
	public void stateChangedOTHER() {
		data.setCurrentState(StationState.WELCOME);
		gui.stateChanged();
		assertTrue(data.getCurrentState() == StationState.WELCOME);
	}
}
