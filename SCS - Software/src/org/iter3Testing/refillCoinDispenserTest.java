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

public class refillCoinDispenserTest {

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
		aUnit.attachCheckoutStationUnit(sUnit1);
		data = aUnit.getAttendantData();
		data.changeState(AttendantState.START);

		try {
			bot = new Robot();
		} catch (AWTException e) {
		}
	}

	@Test
	public void testRefillCoinDispenser() {

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

		// maintenance
		bot.mouseMove(650, 150);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// refill coin dispenser
		bot.mouseMove(450, 420);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// add 0.05
		bot.mouseMove(530, 150);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// remove 0.05
		bot.mouseMove(480, 150);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// add 0.1
		bot.mouseMove(530, 220);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// remove 0.1
		bot.mouseMove(480, 220);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// add 0.25
		bot.mouseMove(530, 280);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// remove 0.25
		bot.mouseMove(480, 280);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// add 1
		bot.mouseMove(530, 360);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// remove 1
		bot.mouseMove(480, 360);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// add 2
		bot.mouseMove(530, 440);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// remove 2
		bot.mouseMove(480, 440);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// confirm
		bot.mouseMove(550, 540);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

	}

}