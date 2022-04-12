package org.controlSoftware.deviceHandlers.membership;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.controlSoftware.customer.CheckoutHandler;
import org.controlSoftware.deviceHandlers.payment.CardPaymentSoftware;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutData.StationState;
import org.driver.SelfCheckoutSoftware;
import org.driver.databases.MembershipDatabase;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;

public class ScansMembershipCard implements CardReaderObserver{
	private SelfCheckoutData stationData;
	private SelfCheckoutSoftware stationSoftware;
	private SelfCheckoutStation station;
	private CardPaymentSoftware paymentHandler;
	
	
	private boolean tap = false;
	
	private int pointsPerDollar = 0;
	private int discountPercentage = 0;

	
	String memberNumber;
	Map<String, Integer> membershipCards = new HashMap<>();
	
	public Map<String, Integer> getMembershipCards() {
		return membershipCards;
	}

	public void setMembershipCards(Map<String, Integer> membershipCards) {
		this.membershipCards = membershipCards;
	}

	
	//if customer is currently trying to scan their membership card, this will be set to true
	protected AtomicBoolean scanMembershipCard = new AtomicBoolean(false);
	
	//whether scan was successful or not
	private AtomicBoolean scanSuccessful = new AtomicBoolean(false);
	
	
	
	public ScansMembershipCard(SelfCheckoutData stationData, SelfCheckoutSoftware stationSoftware) {
		this.stationData = stationData;
		this.stationSoftware = stationSoftware;
		this.station = stationData.getStationHardware();
		this.membershipCards = new MembershipDatabase().getDatabase();
		this.paymentHandler = this.stationSoftware.getCardPaymentSoftware();
	}
	

	//called at the end of an entire transaction to apply points to member's account
	public void applyMembershipBenefits(String number, BigDecimal totalPayment) {
		int newPointsPerDollar = (int)(totalPayment.doubleValue() * pointsPerDollar);
		int previousPoints = membershipCards.get(number);
		membershipCards.put(number, Integer.valueOf(previousPoints + newPointsPerDollar));
		
		
	}
	//same as cardRead but for manual input
		public void membershipCardInput(String cardNum) {
			
				if(membershipCards.containsKey(cardNum) == true) {
					scanSuccessful = new AtomicBoolean(true);
					stationData.changeState(StationState.PAYMENT_AMOUNT_PROMPT);				}
				else {
					//membership number does not exist
					stationData.changeState(StationState.BAD_MEMBERSHIP);				
					paymentHandler.membershipScanUnsuccessful();	
				}
				reset();

		}
	
	public void applyMembershipBenefits(String number) {

	}
	
	public int getLoyaltyPoints(String cardNumber) {
		return membershipCards.get(cardNumber);
	}
	
	public void setPercentDiscount(int discount) {
		discountPercentage = discount;
	}
	
	public int getPercentDiscount() {
		return discountPercentage;
	}
	
	public void setPointsPerDollarSpent(int points) {
		pointsPerDollar = points;
	}
	
	
	
	public void reset() {
		memberNumber = null;
		scanMembershipCard = new AtomicBoolean(false);
		scanSuccessful = new AtomicBoolean(false);
		tap = false;
	}
	
	
	/**
	 * Announces that the data has been read from a card.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 * @param data
	 *            The data that was read. Note that this data may be corrupted.
	 */
	@Override
	public void cardDataRead(CardReader reader, CardData data) {
		if(tap == true) {
			data = null;
			return;
		}
		if ((stationData.getCurrentState() == StationState.SWIPE_MEMBERSHIP)) {
			String type = data.getType();
			String[] memberCard = {"member", "Member"};
			if(type.equals("Member") || type.equals("member")) {
				String cardNum = data.getNumber();
				if(membershipCards.containsKey(cardNum) == true) {
					scanSuccessful = new AtomicBoolean(true);
					stationData.setMembershipID(data.getNumber());
					stationData.changeState(StationState.PAYMENT_AMOUNT_PROMPT);
				}
				else {
					//membership number does not exist
					stationData.changeState(StationState.BAD_MEMBERSHIP);
					paymentHandler.membershipScanUnsuccessful();
				}
			}
			else {
				//card not of type membership
				stationData.changeState(StationState.BAD_MEMBERSHIP);
				paymentHandler.membershipScanUnsuccessful();	
			}
		}
		reset();
	}
	
	/**
	 * Announces that a card has swiped on the indicated card reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	@Override
	public void cardSwiped(CardReader reader) {
		 stationData.changeState(StationState.SWIPE_MEMBERSHIP);
	}
	
	/**
	 * Announces that the indicated device has been enabled.
	 * 
	 * @param device
	 *                 The device that has been enabled.
	 */
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device){}

	/**
	 * Announces that the indicated device has been disabled.
	 * 
	 * @param device
	 *                 The device that has been enabled.
	 */
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {}
	
	/**
	 * Announces that a card has been inserted in the indicated card reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	@Override
	public void cardInserted(CardReader reader) {
		if (scanMembershipCard == new AtomicBoolean(true)) {
			scanSuccessful = new AtomicBoolean(false);
			paymentHandler.membershipScanUnsuccessful();	//something to inform customer of unsuccessful scan
		}
	}

	/**
	 * Announces that a card has been removed from the indicated card reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	@Override
	public void cardRemoved(CardReader reader) {
	}

	/**
	 * Announces that a (tap-enabled) card has been tapped on the indicated card
	 * reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	@Override
	public void cardTapped(CardReader reader) {
		if (scanMembershipCard == new AtomicBoolean(true)) {
			scanSuccessful = new AtomicBoolean(false);
			paymentHandler.membershipScanUnsuccessful();	//something to inform customer of unsuccessful scan
			tap = true;
		}
	}
}