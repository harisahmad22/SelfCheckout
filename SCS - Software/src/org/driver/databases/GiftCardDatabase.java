package org.driver.databases;

import java.util.HashMap;
import java.util.Map;

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
	
	private Map<String, Double> giftCardDB = new HashMap<String, Double>();

	public void AddGiftCard(String giftCardID, Double value)
	{		
		//Populate the database with some gift cards and their values as gift cards are given out by the store
		giftCardDB.put(giftCardID, value);
	}

	public void updateGiftCard(String giftCardID, Double value)
	{		
		//update the database if gift card has been used
		giftCardDB.replace(giftCardID, value);
	}
	
	public Map<String, Double> getDatabase()
	{
		return giftCardDB;
	}
	
}
