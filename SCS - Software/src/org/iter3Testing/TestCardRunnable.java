package org.iter3Testing;

import java.io.IOException;

import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.ElectronicScale;

public class TestCardRunnable implements Runnable {

	private CardReader reader;
	private Card testCard;
	private String method;
	private String pin;
	public TestCardRunnable(CardReader reader, String method, String type, String num, String name, String cvv, String pin, boolean isTap, boolean hasChip)
	{
		this.reader = reader;
		this.method = method;
		this.pin = pin;
		this.testCard = new Card(type, num, name, cvv, pin, isTap, hasChip);
	}
	
	@Override
	public void run() {
		try {
			switch(method)
			{
			case "tap":
				reader.tap(testCard);
				break;
			case "swipe":
				reader.swipe(testCard);
				break;
			case "insert":
				reader.insert(testCard, pin);
				break;
			default:
				reader.swipe(testCard);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
