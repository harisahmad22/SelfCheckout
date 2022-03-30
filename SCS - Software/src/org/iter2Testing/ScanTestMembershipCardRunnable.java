package org.iter2Testing;

import java.io.IOException;

import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.ElectronicScale;

public class ScanTestMembershipCardRunnable implements Runnable {

	private CardReader reader;
	private Card testMembershipCard;
	public ScanTestMembershipCardRunnable(CardReader reader, String type)
	{
		this.reader = reader;
		//Membership Card has no pin, chip, or ability to tap
		this.testMembershipCard = new Card(type, "123456789", "Test User", null, null, false, false);
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
