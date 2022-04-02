package org.controlSoftware.deviceHandlers.payment;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

import org.controlSoftware.customer.CheckoutSoftware;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteValidator;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.CoinStorageUnit;
import org.lsmr.selfcheckout.devices.CoinValidator;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BanknoteValidatorObserver;
import org.lsmr.selfcheckout.devices.observers.CoinDispenserObserver;
import org.lsmr.selfcheckout.devices.observers.CoinStorageUnitObserver;
import org.lsmr.selfcheckout.devices.observers.CoinValidatorObserver;

public class PayWithCash implements BanknoteValidatorObserver, CoinValidatorObserver, CoinDispenserObserver, CoinStorageUnitObserver {
    private SelfCheckoutStation station;
	private BanknoteValidator banknoteValidator;
    private CoinValidator coinValidator;
    private CoinStorageUnit coinStorageUnit;
    private Map<BigDecimal, CoinDispenser> coinDispensers;
    private BigDecimal lastValidCoinInserted; 


    public PayWithCash(SelfCheckoutStation aStation){
        this.station = aStation;
        this.banknoteValidator = this.station.banknoteValidator;
        this.coinValidator = this.station.coinValidator;
        this.coinDispensers = this.station.coinDispensers;
        this.coinStorageUnit = this.station.coinStorage;

        //unless done in driver
		banknoteValidator.attach(this);
        coinValidator.attach(this);
        coinStorageUnit.attach(this);
        for(CoinDispenser coinDispenser : coinDispensers.values())
            coinDispenser.attach(this);
        //unless done in driver

    }
    //banknotes
	@Override
	public void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) {
		CheckoutSoftware.addToTotalPaid(new BigDecimal(value));
	}

	@Override
	public void invalidBanknoteDetected(BanknoteValidator validator) {
        System.out.println("Invalid Banknote.");
	}


    // stores coin value of last valid coin
    @Override 
    public void validCoinDetected(CoinValidator validator, BigDecimal value) {
        lastValidCoinInserted = value;
    }

    @Override
    public void invalidCoinDetected(CoinValidator validator) {
        System.out.println("Invalid coin.");
    }

    // valid coin makes its way to the dispenser
    @Override
    public void coinAdded(CoinDispenser dispenser, Coin coin) {
        CheckoutSoftware.addToTotalPaid(coin.getValue());
    }

    // valid coin makes its way to the storage unit
    @Override
    public void coinAdded(CoinStorageUnit unit) {
        CheckoutSoftware.addToTotalPaid(lastValidCoinInserted);
    }


    @Override // storage unit full
    public void coinsFull(CoinStorageUnit unit) {     
    }

    @Override // dispenser full
    public void coinsFull(CoinDispenser dispenser) { 
    }

    @Override // for dispensing change
    public void coinsEmpty(CoinDispenser dispenser) {
    }

    @Override // for dispensing change
    public void coinRemoved(CoinDispenser dispenser, Coin coin) {
    }

    @Override // for physically adding coins
    public void coinsLoaded(CoinStorageUnit unit) {
    }

    @Override // for physically unloading coins
    public void coinsUnloaded(CoinStorageUnit unit) {  
    }

    @Override // for physically loading coins
    public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
    }

    @Override // for physically unloading coins
    public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {  
    }

    @Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}
}
