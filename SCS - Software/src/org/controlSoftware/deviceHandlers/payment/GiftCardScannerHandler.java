package org.controlSoftware.deviceHandlers.payment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.controlSoftware.data.GiftCardInfo;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutData.StationState;
import org.driver.databases.GiftCardDatabase;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;

/**
 * @author Divyansh Rana - 30117089
 * Paying with a Gift Card Use Case
 * Returns the amount left to be paid after using a giftCard, needs to be used along the class GiftCardDatabase as that would be the one 
 * updating the gift card info after a gift card has been used 
 * 
 * To do-:
 * Might link  this class this GiftCardDatabase so  GiftCardDatabase is not used on its own ever, dont know if thats possible
 *  GiftCardDatabase will prob be a singleton but we could be dealing with multiple stores.
 * 
 * 1) add swipe to the gift card(look at membership card)
 * 2) Add giftCardDatabase to selfCheckoutData
 */

public class GiftCardScannerHandler implements CardReaderObserver
{
    private SelfCheckoutData stationData;
	
	public GiftCardScannerHandler(SelfCheckoutData stationData) {
		this.stationData = stationData;
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
	}
	
	/**
	 * Announces that a card has swiped on the indicated card reader.
	 * 
	 * @param reader
	 *            The reader where the event occurred.
	 */
	@Override
	public void cardSwiped(CardReader reader) {
		System.out.println("A Card has been Swiped!");
		stationData.setCardSwiped(true); //Inform checkout of swipe
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
		if (stationData.getCardSwiped() && (data.getType() == "GiftCard") && (stationData.getCurrentState() == StationState.PAY_GIFTCARD)) {
			stationData.setGiftCardNo(data.getNumber());
		}
	}

    public BigDecimal payWithGiftCard(String GiftCardNumber, BigDecimal valuePurchase, GiftCardDatabase giftCardDB)
    {
        Map<String, GiftCardInfo> giftCardDataBase = giftCardDB.getDatabase();
        GiftCardInfo giftCard = giftCardDataBase.get(GiftCardNumber);
        if(valuePurchase.equals(giftCard.getBalance()) || (valuePurchase.compareTo(giftCard.getBalance()) == 1))
        {
            valuePurchase = valuePurchase.subtract(giftCard.getBalance());
            giftCard.updateBalance(giftCard.getBalance());
             // as all the value has been used up
            return valuePurchase;
        }

        else
        {
            giftCard.updateBalance(valuePurchase);
            valuePurchase.valueOf(0); // as we paid for the entire purchase
            return valuePurchase;
        }
    }
}
