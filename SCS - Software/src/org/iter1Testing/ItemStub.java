//Shufan Zhai - 30117333

package org.iter1Testing;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.SimulationException;

public class ItemStub extends Item {
	private double weightInGrams;

	protected ItemStub(double weightInGrams) {
		super(weightInGrams);

		if (weightInGrams <= 0.0)
			throw new SimulationException(new IllegalArgumentException("The weight has to be positive."));

		this.weightInGrams = weightInGrams;
	}

	public double getWeight() {
		return weightInGrams;
	}
}
