package org.controlSoftware.deviceHandlers.membership;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.controlSoftware.customer.CheckoutSoftware;
import org.controlSoftware.deviceHandlers.payment.PaymentHandler;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;

public class ScansMembershipCard implements CardReaderObserver{
	private SelfCheckoutStation station;
	private PaymentHandler payment;
	
	String memberNumber;
	Map<String, Integer> membershipCards = new HashMap<>();
	
	int discountPercentage = 0;
	
	//if customer is currently trying to scan their membership card, this will be set to true
	protected AtomicBoolean scanMembershipCard = new AtomicBoolean(false);
	
	//whether scan was successful or not
	private AtomicBoolean scanSuccessful = new AtomicBoolean(false);
	
	
	public ScansMembershipCard(SelfCheckoutStation station, PaymentHandler pay) {
		this.station = station;
		station.cardReader.attach(this);
		
		this.payment = pay;
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
	
	public void wantToScan() {
		scanMembershipCard = new AtomicBoolean(true);
	}
	public void disableMemberScan() {
		scanMembershipCard = new AtomicBoolean(false);
	}
	
	public void reset() {
		memberNumber = null;
		scanMembershipCard = new AtomicBoolean(false);
		scanSuccessful = new AtomicBoolean(false);
		
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
		String type = data.getType();
		
		String[] memberCard = {"member", "Member"};
		if((type.indexOf(memberCard[0]) > 0) || (type.indexOf(memberCard[1]) > 0) ) {
			String cardNum = data.getNumber();
			if(membershipCards.containsKey(cardNum) == true) {
				scanSuccessful = new AtomicBoolean(true);
				payment.membershipCardScanSuccessful(getPercentDiscount());
			}
			else {
				//membership number does not exist
				payment.membershipScanUnsuccessful();
			}
		}
		else {
			//card not of type membership
			payment.membershipScanUnsuccessful();		}
		
		/*
		if (checkout.getCardSwiped() && (data.getType() == "Membership") && (checkout.isWaitingForMembership())) {
			checkout.setMembershipNumber(data.getNumber());
		}
		*/
	}
	
	/**
	 * Announces that a card has swiped on the indicated card reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	@Override
	public void cardSwiped(CardReader reader) {
		//checkout.setCardSwiped(true); //Inform checkout of swipe		//idk if this is still needed
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
			payment.membershipScanUnsuccessful();	//something to inform customer of unsuccessful scan
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
			payment.membershipScanUnsuccessful();	//something to inform customer of unsuccessful scan
		}
	}
	



}
