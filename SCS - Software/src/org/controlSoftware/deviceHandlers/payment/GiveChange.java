package org.controlSoftware.deviceHandlers.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.Collections;

import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;


public class GiveChange {
    private SelfCheckoutStation station;
    private final Map<Integer, BanknoteDispenser> banknoteDispensers;
    private final Map<BigDecimal, CoinDispenser> coinDispensers;
    private final int[] banknoteDenominations;                          //assumes is sorted descending
    private final List<BigDecimal> coinDenominations;                   //assumes is sorted descending
    private BigDecimal changeDue;

    public GiveChange(SelfCheckoutStation aStation, BigDecimal amount){
        station = aStation;
        banknoteDispensers = station.banknoteDispensers;
        coinDispensers = station.coinDispensers;
        coinDenominations = station.coinDenominations;
        Collections.reverse(coinDenominations);
        banknoteDenominations = station.banknoteDenominations;

        changeDue = amount;

    }


    /**
     * starts by dispensing banknotes, then coins, until correct change dispensed
     * assumes banknoteDenominations and coinDenominations are sorted in descending order
     * @throws InterruptedException 
     */
    public void dispense() throws EmptyException, DisabledException, OverloadException, InterruptedException{
        for (int i = banknoteDenominations.length-1; i >= 0; i--){         
            BigDecimal temp = new BigDecimal(banknoteDenominations[i]);     //denomination as BigDecimal
            while (temp.compareTo(changeDue) <= 0){
                try {
                    banknoteDispensers.get(banknoteDenominations[i]).emit();    //calls the dispenser for respective denomination to emit()
                    TimeUnit.SECONDS.sleep(5); //For now sleep for 5 seconds, so we have time to remove dangling banknotes in testing
                    changeDue = changeDue.subtract(temp);
                }
                catch(EmptyException e){    //if dispenser is empty, go to next lower denomination
                    break;
                }
            }
        }

        for (int i = 0; i < coinDenominations.size(); i++){         
            BigDecimal temp = coinDenominations.get(i);     //denomination as BigDecimal
            while (temp.compareTo(changeDue) <= 0){
                try {
                    coinDispensers.get(temp).emit();    //calls the dispenser for respective denomination to emit()
                    System.out.println("Emitting: " + temp.toString());
                    changeDue = changeDue.subtract(temp);
                }
                catch(EmptyException e){    //if dispenser is empty, go to next lower denomination
                    break;
                }
            }
        }

    }

}


