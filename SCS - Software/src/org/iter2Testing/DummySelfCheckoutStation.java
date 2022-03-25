//Brody Long - 30022870 

package org.iter2Testing;

import java.math.BigDecimal;
import java.util.Currency;

import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

//Creates an instance of SelfCheckoutStation 
//Uses Canadian currency
//Accepts $5, $10, $20, $50 bills
//Accepts $0.05, $0.10, $0.25, $1.00, $2.00 coins
//Max weight = 50Kg
//Sensitivity = 25g
public class DummySelfCheckoutStation extends SelfCheckoutStation {

	private static Currency currency = Currency.getInstance("CAD");
	private static int[] banknoteDenominations = {5, 10, 20, 50};
	private static BigDecimal[] coinDenominations = {new BigDecimal(0.05),
										  			 new BigDecimal(0.10),
										  			 new BigDecimal(0.25),
										  			 new BigDecimal(1.00),
										  			 new BigDecimal(2.00)};
	private static int scaleMaximumWeight = 50000; //Set limit to 50,000 grams (50Kg)
	private static int scaleSensitivity = 25; //25 gram sensitivity
	
	public DummySelfCheckoutStation() {
		super(currency, banknoteDenominations, coinDenominations, scaleMaximumWeight, scaleSensitivity);
	}
	
	public static Currency getCurrency()
	{
		return currency;
	}
}
