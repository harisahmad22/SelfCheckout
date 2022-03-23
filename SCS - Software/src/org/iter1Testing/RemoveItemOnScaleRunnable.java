//Brody Long - 30022870

package org.iter1Testing;

import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.ElectronicScale;

public class RemoveItemOnScaleRunnable implements Runnable {

	private ElectronicScale scale;
	private BarcodedItem item;
	
	public RemoveItemOnScaleRunnable(ElectronicScale scale, BarcodedItem item)
	{
		this.scale = scale;
		this.item = item;
	}
	
	@Override
	public void run() {
		scale.remove(item);
	}

}
