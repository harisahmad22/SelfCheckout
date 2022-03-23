//Gurleen Arora - 30123071

package org.controlSoftware;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CoinDispenserObserver;

public class PayWithCoin implements CoinDispenserObserver {

    /*
    (Gurleen) 
    1) check if a coin has been added and get the value of the coin add it to total
    */

    @Override
    public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
    }
    @Override
    public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
        
    }

	@Override
	public void coinsFull(CoinDispenser dispenser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coinsEmpty(CoinDispenser dispenser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coinAdded(CoinDispenser dispenser, Coin coin) {
		Checkout.addToTotalPaid(coin.getValue());
	}

	@Override
	public void coinRemoved(CoinDispenser dispenser, Coin coin) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {
		// TODO Auto-generated method stub
		
	} 


}