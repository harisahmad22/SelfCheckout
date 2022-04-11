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

public class SupervisorGUIMasterMainTest {

	AttendantUnit aUnit;
	SelfCheckoutStationUnit sUnit1;
	SelfCheckoutStation station;
	AttendantData data;
	JFrame frame;
	Robot bot;

	// may require multiple runs in order to work correctly
	// bug where bot sometimes doesn't click "block station button"
	@Before
	public void setUp() {

		aUnit = new AttendantUnit();
		sUnit1 = new SelfCheckoutStationUnit(0);
		sUnit1.getSelfCheckoutData().changeState(StationState.INACTIVE);
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
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Password input: 1
		bot.mouseMove(325, 150);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// del
		bot.mouseMove(350, 400);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Password input: 0
		bot.mouseMove(450, 400);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Confirm
		bot.mouseMove(550, 500);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Select Station
		bot.mouseMove(220, 150);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Start up station
		bot.mouseMove(450, 150);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Block station
		bot.mouseMove(600, 200);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Remove Item
		bot.mouseMove(575, 300);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Back
		bot.mouseMove(75, 525);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Unblock station
		bot.mouseMove(600, 200);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Shut down station
		bot.mouseMove(450, 150);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

}
