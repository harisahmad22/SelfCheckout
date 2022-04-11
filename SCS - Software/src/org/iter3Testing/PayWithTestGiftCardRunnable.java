package org.iter3Testing;

import java.io.IOException;

import org.controlSoftware.deviceHandlers.payment.GiftCardScannerHandler;
import org.driver.SelfCheckoutData;
import org.driver.databases.GiftCardDatabase;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.ElectronicScale;

public class PayWithTestGiftCardRunnable  implements Runnable {

	private CardReader reader;
	private Card testGiftCard;
	private GiftCardScannerHandler giftCardHandler;
	GiftCardDatabase giftCardDB;
	String giftCardNo;
	public PayWithTestGiftCardRunnable(CardReader reader, String type, SelfCheckoutData stationData, GiftCardDatabase giftCardDB1, String giftCardNo1)
	{
		this.reader = reader;
		giftCardHandler = new GiftCardScannerHandler(stationData);
		giftCardDB = giftCardDB1;
		giftCardNo = giftCardNo1;
		//Gift Card has no pin, chip, or ability to tap
		this.testGiftCard = new Card(type, "giftCard", "Test User", null, null, false, false);
	}
	
	@Override
	public void run() {
		try {
			reader.swipe(testGiftCard);
			giftCardHandler.payWithGiftCard(giftCardNo, giftCardDB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}