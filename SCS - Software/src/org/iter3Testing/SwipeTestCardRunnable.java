package org.iter3Testing;

import java.io.IOException;

import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.ElectronicScale;

public class SwipeTestCardRunnable implements Runnable {

	private CardReader reader;
	private Card testMembershipCard;
	public SwipeTestCardRunnable(CardReader reader, String type, String num, String name, String cvv, String pin, boolean isTap, boolean hasChip)
	{
		this.reader = reader;
		//Membership Card has no pin, chip, or ability to tap
		this.testMembershipCard = new Card(type, num, name, cvv, pin, isTap, hasChip);
	}
	
	@Override
	public void run() {
		try {
			reader.swipe(testMembershipCard);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
