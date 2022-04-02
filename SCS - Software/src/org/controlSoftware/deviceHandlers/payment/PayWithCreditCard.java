package org.controlSoftware.deviceHandlers.payment;

import java.io.IOException;
import java.math.BigDecimal;

import org.controlSoftware.customer.CheckoutSoftware;
import org.controlSoftware.data.BankClientInfo;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;

/**
 * @author Jacky Liang - 30092916
 * Paying with a Credit Card Use Case
 */

public class PayWithCreditCard implements CardReaderObserver
{
	
	private SelfCheckoutStation checkout;
	private Card CreditCard;
	private CardData cardData;
	private String pin;
	private BankClientInfo bankClientsInfo;
	private boolean wasCardSwiped = false;
	
	public PayWithCreditCard(SelfCheckoutStation checkout, Card CreditCard, CardData cardData, String pin, BankClientInfo bankClientsInfo) 
	{
		this.checkout=checkout;
		this.CreditCard=CreditCard;
//		this.cardData=cardData;
		this.pin=pin;
		this.bankClientsInfo=bankClientsInfo;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardInserted(CardReader reader) 
	{
		try
		{
			cardData=reader.insert(CreditCard, pin);
		}
		catch (IOException e) {}
		
	}

	@Override
	public void cardRemoved(CardReader reader) 
	{
		reader.remove();
		
	}

	@Override
	public void cardTapped(CardReader reader) 
	{
		try 
		{
			cardData = reader.tap(CreditCard);	
		} catch (IOException e) {}
		
	}

	@Override
	public void cardSwiped(CardReader reader) 
	{
		try
		{	
			wasCardSwiped = true;
			cardData = reader.swipe(CreditCard);
		} catch (IOException e) {}
		
	}

	@Override
	public void cardDataRead(CardReader reader, CardData data) {	
	}
	
	public boolean checkBankClientInfo(CardReader reader, BigDecimal totalDue)
	{
		if (bankClientsInfo.getBalance().add(totalDue).compareTo(bankClientsInfo.getMonthlyLimit())<=0 
				&& bankClientsInfo.getNumber().equals(cardData.getNumber()) 
				&& bankClientsInfo.getCardholder().equals(cardData.getCardholder())) 
		{
			if (!wasCardSwiped)
			{
				if (bankClientsInfo.getCVV() == cardData.getCVV())
				{
					completeTransaction(totalDue);
					return true;
				}
				else { return false; }
			}
			else
			{
				completeTransaction(totalDue);
				wasCardSwiped = false; //reset
				return true;
			}
		}
		else
		{
			wasCardSwiped = false; //reset
			return false;
		}
	}
	
	private void completeTransaction(BigDecimal totalDue)
	{
		CheckoutSoftware.addToTotalPaid(totalDue);
		bankClientsInfo.updateBalance(totalDue);
	}

}