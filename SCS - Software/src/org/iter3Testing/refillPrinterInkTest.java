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

public class refillPrinterInkTest {

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
	public void testRefillPrinterInk() {
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

		// refill printer ink
		bot.mouseMove(650, 150);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Password input: 1
		bot.mouseMove(325, 200);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// del
		bot.mouseMove(350, 450);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Password input: 0
		bot.mouseMove(450, 450);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		// Confirm
		bot.mouseMove(550, 550);
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

}
