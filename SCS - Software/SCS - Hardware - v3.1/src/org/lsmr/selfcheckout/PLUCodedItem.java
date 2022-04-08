package org.lsmr.selfcheckout;

/**
 * Represents items for sale, each with a particular barcode and weight.
 */
public class PLUCodedItem extends Item {
	private PriceLookupCode pluCode;

	/**
	 * Basic constructor.
	 * 
	 * @param pluCode
	 *            The PLU code representing the identifier of the product of which
	 *            this is an item.
	 * @param weightInGrams
	 *            The actual weight of the item.
	 */
	public PLUCodedItem(PriceLookupCode pluCode, double weightInGrams) {
		super(weightInGrams);

		if(pluCode == null)
			throw new NullPointerSimulationException("pluCode");

		this.pluCode = pluCode;
	}

	/**
	 * Gets the PLU code of this item.
	 * 
	 * @return The PLU code.
	 */
	public PriceLookupCode getPLUCode() {
		return pluCode;
	}
}
