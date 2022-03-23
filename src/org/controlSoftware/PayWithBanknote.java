//Mikail Munir - 30086727

package org.controlSoftware;

import java.math.BigDecimal;
import java.util.Currency;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;
import org.lsmr.selfcheckout.devices.BanknoteValidator;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BanknoteStorageUnitObserver;
import org.lsmr.selfcheckout.devices.observers.BanknoteValidatorObserver;

public class PayWithBanknote implements BanknoteValidatorObserver{

	
	private boolean isEnabled;
	private boolean valid;
	
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub
		this.isEnabled = true;
		this.valid = true;
		System.out.println("Banknote payment device enabled. ");
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		this.isEnabled = false;
		
	}

	@Override
	public void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) {
		Checkout.addToTotalPaid(new BigDecimal(value));

	}

	@Override
	public void invalidBanknoteDetected(BanknoteValidator validator) {
		this.valid = false;
		
	}

	public boolean getValid() {
		return valid;
	}
	
}
