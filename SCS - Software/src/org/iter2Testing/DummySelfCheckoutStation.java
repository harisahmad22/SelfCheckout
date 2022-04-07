//Brody Long - 30022870 

package org.iter2Testing;

import java.math.BigDecimal;
import java.util.Currency;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

//Creates an instance of SelfCheckoutStation 
//Uses Canadian currency
//Accepts $5, $10, $20, $50 bills
//Accepts $0.05, $0.10, $0.25, $1.00, $2.00 coins
//Max weight = 50Kg
//Sensitivity = 10g
public class DummySelfCheckoutStation extends SelfCheckoutStation {

	private static Currency CAD = Currency.getInstance("CAD");
	private static int[] banknoteDenominations = {50, 20, 10, 5};
	private static BigDecimal[] coinDenominations = {new BigDecimal(2.00),
													 new BigDecimal(1.00),
										  			 new BigDecimal(0.25),
										  			 new BigDecimal(0.10),
										  			 new BigDecimal(0.05)};
	private static int scaleMaximumWeight = 50000; //Set limit to 50,000 grams (50Kg)
	private static int scaleSensitivity = 10; //10 gram sensitivity
	
	private static Banknote fiveDollarBanknote = new Banknote(DummySelfCheckoutStation.getCurrency(), 5);
	private static Banknote tenDollarBanknote = new Banknote(DummySelfCheckoutStation.getCurrency(), 10);
	private static Banknote twentyDollarBanknote = new Banknote(DummySelfCheckoutStation.getCurrency(), 20);
	private static Banknote fiveDollarBanknoteUSD = new Banknote(Currency.getInstance("USD"), 5);
	private static Banknote twelveDollarBanknote = new Banknote(DummySelfCheckoutStation.getCurrency(), 12);
	private static Coin nickel = new Coin(DummySelfCheckoutStation.getCurrency(), new BigDecimal(0.05));
	private static Coin dime = new Coin(DummySelfCheckoutStation.getCurrency(), new BigDecimal(0.10));
	private static Coin quarter = new Coin(DummySelfCheckoutStation.getCurrency(), new BigDecimal(0.25));
	private static Coin loonie = new Coin(DummySelfCheckoutStation.getCurrency(), new BigDecimal(1.00));
	private static Coin toonie = new Coin(DummySelfCheckoutStation.getCurrency(), new BigDecimal(2.00));
	
	public DummySelfCheckoutStation() {
		super(CAD, banknoteDenominations, coinDenominations, scaleMaximumWeight, scaleSensitivity);
		for (BigDecimal val : super.coinDispensers.keySet())
		{
			try { //Load half full
				for (int i = 0; i < 100; i++) { super.coinDispensers.get(val).load(new Coin(CAD, val)); }
				
			} catch (OverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}
		for (int val : super.banknoteDispensers.keySet())
		{
			try { //Load half full
				for (int i = 0; i < 50; i++) { super.banknoteDispensers.get(val).load(new Banknote(CAD, val)); }
				
			} catch (OverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}
	}
	
	public static Currency getCurrency()
	{
		return CAD;
	}
}
