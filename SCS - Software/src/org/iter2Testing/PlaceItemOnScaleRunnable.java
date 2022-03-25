//Brody Long - 30022870

package org.iter2Testing;

import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.ElectronicScale;

public class PlaceItemOnScaleRunnable implements Runnable {

	private ElectronicScale scale;
	private BarcodedItem item;
	
	public PlaceItemOnScaleRunnable(ElectronicScale scale, BarcodedItem item)
	{
		this.scale = scale;
		this.item = item;
	}
	
	@Override
	public void run() {
		scale.add(item);
	}

}
