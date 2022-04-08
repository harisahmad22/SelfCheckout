package org.driver.databases;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.controlSoftware.data.GiftCardInfo;
import org.lsmr.selfcheckout.Card;

/*
 * @author Divyansh Rana - 30117089
 * Paying with a Gift Card Use Case
 * 
 * need to update the database once the payment is completed
 * ----------Populating the Database-------------------------------
 * In a test setup need to populate the GiftCardDatabase
 * GiftCardDatabase giftData = new GiftCardDatabase();
 * giftData.AddGiftCard(1, 50); // gift card 1
 * giftData.AddGiftCard(2, 150); // gift card 2
 * giftData.AddGiftCard(3, 250); // gift card 3
 * ----------Populating the Database--------------------------------
 */


public class GiftCardDatabase {
	
	private Map<String, GiftCardInfo> giftCardDB = new HashMap<String, GiftCardInfo>();

	public void AddGiftCard(String giftCardID, BigDecimal value)
	{		
		//Populate the database with some gift cards and their values as gift cards are given out by the store
		GiftCardInfo giftCard = new GiftCardInfo("GiftCard", giftCardID, value);
		giftCardDB.put(giftCardID, giftCard);
	}

	// public void updateGiftCard(String giftCardID, Double value) // dont know when to update
	// {		
	// 	//update the database if gift card has been used
	// 	GiftCardInfo giftCard = giftCardDB.get(giftCardID);
	// 	giftCardDB.replace(giftCardID, value);
	// }
	
	public Map<String, GiftCardInfo> getDatabase()
	{
		return giftCardDB;
	}
	
}
