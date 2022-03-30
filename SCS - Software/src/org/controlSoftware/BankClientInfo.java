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

	/**
	 * @return type
	 * Returns type of card
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return number
	 * Returns card number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @return cardholder
	 * Returns cardholder info
	 */
	public String getCardholder() {
		return cardholder;
	}

	/**
	 * @return cvv
	 * Returns cvv info
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
