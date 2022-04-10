package org.controlSoftware.data;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.Card.CardData;

/**
 * @author Divyansh Rana - 30117089
 * Stub that Simulates Gift card Info for gift card payments
 */
public class GiftCardInfo implements CardData {

	private final String type;
	private final String number;
	// public final boolean isTapEnabled;
	// public final boolean hasChip;
	private int failedTrials = 0;
	private BigDecimal balance;
	
	/**
	 * @param type
	 * @param number
	 * @param isTapEnabled
	 * @param hasChip
	 * @param balance
	 */
	public GiftCardInfo(String type, String number, BigDecimal balance) { // boolean isTapEnabled, boolean hasChip, could add these two
		
		this.type = type;
		this.number = number;
		// this.isTapEnabled = isTapEnabled;
		// this.hasChip = hasChip;
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
	 * @return balance
	 * Returns the Balance
	 */
	public BigDecimal getBalance() {
		return balance;
		
	}
	
	public void updateBalance(BigDecimal spent) {
		this.balance = this.balance.subtract(spent);
	}

    @Override // empty methods
    public String getCardholder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override // empty methods
    public String getCVV() {
        // TODO Auto-generated method stub
        return null;
    }
	
}
