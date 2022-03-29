package org.controlSoftware;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.Card.CardData;

/**
 * @author Harrison Drew - 30115014
 * Stub that Simulates Banking Info for debit card payments
 */
public class BankClientInfo implements CardData {

	private final String type;
	private final String number;
	private final String cardholder;
	private final String cvv;
	private final String pin;
	public final boolean isTapEnabled;
	public final boolean hasChip;
	private int failedTrials = 0;
	private boolean isBlocked;
	private BigDecimal balance;
	
	/**
	 * @param type
	 * @param number
	 * @param cardholder
	 * @param cvv
	 * @param pin
	 * @param isTapEnabled
	 * @param hasChip
	 * @param balance
	 * Fake Banking Information
	 */
	public BankClientInfo(String type, String number, String cardholder, String cvv, String pin, boolean isTapEnabled, boolean hasChip, BigDecimal balance) {
		
		this.type = type;
		this.number = number;
		this.cardholder = cardholder;
		this.cvv = cvv;
		this.pin = pin;
		this.isTapEnabled = isTapEnabled;
		this.hasChip = hasChip;
		this.balance = balance;
	}

	/* (non-Javadoc)
	 * @see org.lsmr.selfcheckout.Card.CardData#getType()
	 */
	public String getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see org.lsmr.selfcheckout.Card.CardData#getNumber()
	 */
	public String getNumber() {
		return number;
	}

	/* (non-Javadoc)
	 * @see org.lsmr.selfcheckout.Card.CardData#getCardholder()
	 */
	public String getCardholder() {
		return cardholder;
	}

	/* (non-Javadoc)
	 * @see org.lsmr.selfcheckout.Card.CardData#getCVV()
	 */
	public String getCVV() {
		return cvv;
	}
	
	/**
	 * @return balance
	 * Returns the Balance
	 */
	public BigDecimal getBalance() {
		return balance;
		
	}
	
}
