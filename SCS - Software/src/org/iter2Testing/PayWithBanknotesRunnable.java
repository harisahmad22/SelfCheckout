//Brody Long - 30022870

package org.iter2Testing;

import java.util.concurrent.TimeUnit;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;

public class PayWithBanknotesRunnable implements Runnable {

	private BanknoteSlot banknoteSlot;
	private Banknote[] banknotes;
	
	public PayWithBanknotesRunnable(BanknoteSlot banknoteSlot, Banknote[] banknotes)
	{
		this.banknotes = banknotes;
		this.banknoteSlot = banknoteSlot;
	}
	@Override
	public void run() {
		for (Banknote banknote : banknotes)
		{
			try {
				System.out.println("Inserting a $" + banknote.getValue() + " banknote.");
				banknoteSlot.accept(banknote);
				TimeUnit.SECONDS.sleep(1);
				
			} catch (DisabledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			
		}

	}

}
