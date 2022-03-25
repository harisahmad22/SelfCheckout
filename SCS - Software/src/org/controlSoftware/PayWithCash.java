package org.controlSoftware;

import java.math.BigDecimal;
import java.util.Currency;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteValidator;
import org.lsmr.selfcheckout.devices.CoinValidator;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BanknoteValidatorObserver;
import org.lsmr.selfcheckout.devices.observers.CoinValidatorObserver;

public class PayWithCash implements BanknoteValidatorObserver, CoinValidatorObserver {
    private SelfCheckoutStation station;
	private BanknoteValidator banknoteValidator;
	private CoinValidator coinValidator;
    private BigDecimal amountDue;
    private BigDecimal amountPaid;

    public PayWithCash(SelfCheckoutStation aStation, BigDecimal totalCost){
        station = aStation;
        banknoteValidator = station.banknoteValidator;
		coinValidator = station.coinValidator;
		
		banknoteValidator.attach(this);
		coinValidator.attach(this);

        amountDue = totalCost;
        amountPaid = new BigDecimal(0);
    }

	@Override
	public void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) {
		amountPaid = amountPaid.add(new BigDecimal(value));
        if (amountPaid.compareTo(amountDue) >= 0){
            //either give change here, or go back to Checkout or??
        }
	}

    @Override
    public void validCoinDetected(CoinValidator validator, BigDecimal value) {
        amountPaid = amountPaid.add(value);
        if (amountPaid.compareTo(amountDue) >= 0){
            //either give change here, or go back to Checkout or??
        }
	}
    }	

	@Override
	public void invalidBanknoteDetected(BanknoteValidator validator) {
        System.out.println("Invalid Banknote.");
	}

    @Override
    public void invalidCoinDetected(CoinValidator validator) {
        System.out.println("Invalid Coin.");
    }

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {

	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		
	}
}
