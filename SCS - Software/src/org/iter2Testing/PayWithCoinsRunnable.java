//Brody Long - 30022870

package org.iter2Testing;

import java.util.concurrent.TimeUnit;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.CoinSlot;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;

public class PayWithCoinsRunnable implements Runnable {

	private CoinSlot coinSlot;
	private Coin[] coins;
	
	public PayWithCoinsRunnable(CoinSlot coinSlot, Coin[] coins)
	{
		this.coins = coins;
		this.coinSlot = coinSlot;
		this.coinSlot.enable();
	}
	@Override
	public void run() {
		for (Coin coin: coins)
		{
			try {
				System.out.println("Inserting a $" + coin.getValue() + " coin.");
				try {
					coinSlot.accept(coin);
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				TimeUnit.SECONDS.sleep(1);
				
			} catch (DisabledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			
		}

	}

}
