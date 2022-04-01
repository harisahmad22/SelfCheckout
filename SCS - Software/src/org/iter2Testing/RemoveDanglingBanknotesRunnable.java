package org.iter2Testing;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.BanknoteSlot;

public class RemoveDanglingBanknotesRunnable implements Runnable {

	private BanknoteSlot slot;
	private int count;
	
	public RemoveDanglingBanknotesRunnable(BanknoteSlot slot, int count)
	{
		this.slot = slot;
		this.count = count;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < count; i++) 
		{ 
			System.out.println("Removing a dangling banknote!");
			Banknote[] banknotes = slot.removeDanglingBanknotes();
			for (Banknote bn : banknotes)
			{
				CheckoutTest.banknoteChangeValue += bn.getValue();
			}
			
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}
	}

}
