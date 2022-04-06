package org.controlSoftware.deviceHandlers.payment;

import java.util.HashMap;

/**
 * @author Divyansh Rana - 30117089
 * Paying with a Gift Card Use Case
 * Returns the amount left to be paid after using a giftCard, needs to be used along the class GiftCardDatabase as that would be the one 
 * updating the gift card info after a gift card has been used 
 * 
 * To do-:
 * Might link  this class this GiftCardDatabase so  GiftCardDatabase is not used on its own ever, dont know if thats possible
 *  GiftCardDatabase will prob be a singleton but we could be dealing with multiple stores.
 */

public class GiftCardScannerHandler 
{
    private String GiftCardNumber;
    private double value;

    public double payWithGiftCard(String GiftCardNumber, double valuePurchase)
    {
        if(valuePurchase>= this.value)
        {
            valuePurchase = valuePurchase - this.value;
            value = 0; // as all the value has been used up
            return valuePurchase;
        }

        else
        {
            value = value - valuePurchase;
            valuePurchase = 0; // as we paid for the entire purchase
            return valuePurchase;
        }
    }
}
