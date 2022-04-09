package org.controlSoftware.deviceHandlers.payment;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

import org.controlSoftware.customer.CheckoutHandler;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutData.StationState;
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

public class CashPaymentHandler implements BanknoteValidatorObserver, CoinValidatorObserver, CoinDispenserObserver, CoinStorageUnitObserver {
    private SelfCheckoutStation station;
	private BanknoteValidator banknoteValidator;
    private CoinValidator coinValidator;
    private CoinStorageUnit coinStorageUnit;
    private Map<BigDecimal, CoinDispenser> coinDispensers;
    private BigDecimal lastValidCoinInserted;
	private SelfCheckoutData stationData; 
	private boolean isInValidDetected = false;

    public CashPaymentHandler(SelfCheckoutData stationData){
        this.stationData = stationData;
        this.station = stationData.getStationHardware();
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
		stationData.addToTotalPaid(new BigDecimal(value));
		
		printTotals();
		
		if (stationData.getTotalPaidThisTransaction().compareTo(stationData.getTransactionPaymentAmount()) >= 0)
		{//Total paid this transaction >= User defined payment amount, ask to print receipt
			stationData.changeState(StationState.PRINT_RECEIPT_PROMPT);
		}
	}

	@Override
	public void invalidBanknoteDetected(BanknoteValidator validator) {
        System.out.println("Invalid Banknote.");
        setInValidDetected(true);
	}


    // stores coin value of last valid coin
    @Override 
    public void validCoinDetected(CoinValidator validator, BigDecimal value) {
        lastValidCoinInserted = value;
    }

    @Override
    public void invalidCoinDetected(CoinValidator validator) {
        System.out.println("Invalid coin.");
        setInValidDetected(true);
    }

    // valid coin makes its way to the dispenser
    @Override
    public void coinAdded(CoinDispenser dispenser, Coin coin) {
    	stationData.addToTotalPaid(coin.getValue());
		
    	printTotals();
		
		if (stationData.getTotalPaidThisTransaction().compareTo(stationData.getTransactionPaymentAmount()) >= 0)
		{//Total paid this transaction >= User defined payment amount, ask to print receipt
			stationData.changeState(StationState.PRINT_RECEIPT_PROMPT);
		}
    }
    
    private void printTotals()
    {
    	System.out.println("!!! total due: " + stationData.getTotalDue());
		System.out.println("!!! total money paid: " + stationData.getTotalMoneyPaid());
		System.out.println("!!! total paid this transaction: " + stationData.getTotalPaidThisTransaction());
		System.out.println("!!! transaction amount: " + stationData.getTransactionPaymentAmount());
    }
    // valid coin makes its way to the storage unit
    @Override
    public void coinAdded(CoinStorageUnit unit) {
    	stationData.addToTotalPaid(lastValidCoinInserted);
    	
    	printTotals();
    	
		if (stationData.getTotalPaidThisTransaction().compareTo(stationData.getTransactionPaymentAmount()) >= 0)
		{//Total paid this transaction >= User defined payment amount, ask to print receipt
			stationData.changeState(StationState.PRINT_RECEIPT_PROMPT);
		}
    	
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
	public boolean isInValidDetected() {
		return isInValidDetected;
	}
	public void setInValidDetected(boolean isValidDetected) {
		this.isInValidDetected = isValidDetected;
	}
}
