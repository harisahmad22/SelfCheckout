//Shufan Zhai - 30117333

package org.iter2Testing;

import org.lsmr.selfcheckout.Item;

public class ItemStub extends Item {
	private double weightInGrams;

	protected ItemStub(double weightInGrams) {
		super(weightInGrams);

		if (weightInGrams <= 0.0)
			throw new IllegalArgumentException("The weight has to be positive.");

		this.weightInGrams = weightInGrams;
	}

	public double getWeight() {
		return weightInGrams;
	}
}
