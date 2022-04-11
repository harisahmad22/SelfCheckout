package org.iter3Testing;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import javax.swing.JFrame;

import org.driver.AttendantData;
import org.driver.AttendantData.AttendantState;
import org.driver.AttendantUnit;
import org.driver.SelfCheckoutData.StationState;
import org.driver.SelfCheckoutStationUnit;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class SupervisorGUIMasterTestPLUandBarcode {

	AttendantUnit aUnit;
	SelfCheckoutStationUnit sUnit1;
	SelfCheckoutStation station;
	AttendantData data;
	JFrame frame;
	Robot bot;

	@Before
	public void setUp() {

		aUnit = new AttendantUnit();
		sUnit1 = new SelfCheckoutStationUnit(0);
		sUnit1.getSelfCheckoutData().changeState(StationState.INACTIVE);
		sUnit1.getSelfCheckoutData().changeState(StationState.NORMAL);
		aUnit.attachCheckoutStationUnit(sUnit1);
		data = aUnit.getAttendantData();
		data.changeState(AttendantState.START);

		try {
			bot = new Robot();
		} catch (AWTException e) {
		}
	}

	@Test
	public void allStates() {

		// Welcome screen: log in
		bot.mouseMove(450, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Password input: 1
		bot.mouseMove(325, 150);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// del
		bot.mouseMove(350, 400);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Password input: 0
		bot.mouseMove(325, 150);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Confirm
		bot.mouseMove(550, 500);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Select Station
		bot.mouseMove(220, 150);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Start up station
		bot.mouseMove(550, 150);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Block station
		bot.mouseMove(220, 300);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		
		// Add item
		bot.mouseMove(300, 300);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);		
		
		
		// barcode Code
		bot.mouseMove(450, 150);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		
		// 1
		bot.mouseMove(325, 200);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Confirm
		bot.mouseMove(700, 550);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Confirm
		bot.mouseMove(700, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		// fix weight error
		bot.mouseMove(600, 200);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// Confirm
		bot.mouseMove(700, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		// block station
		bot.mouseMove(600, 200);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		
		// Add item
		bot.mouseMove(300, 300);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);		
		
		// PLU Code
		bot.mouseMove(450, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		
		// 1
		bot.mouseMove(325, 200);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		
		// 2
		bot.mouseMove(475, 200);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// 3
		bot.mouseMove(600, 200);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		
		// 4
		bot.mouseMove(350, 300);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		
		// Confirm
		bot.mouseMove(700, 550);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		// PLU Code
		bot.mouseMove(550, 350);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
		
		// Unblock station
		bot.mouseMove(600, 200);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Shut down station
		bot.mouseMove(450, 150);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Logout
		bot.mouseMove(550, 600);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

}