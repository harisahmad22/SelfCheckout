package org.iter3Testing;

import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutData.StationState;

public class ChangeStateRunnable implements Runnable {

	private SelfCheckoutData stationData;
	private StationState newState;

	public ChangeStateRunnable(SelfCheckoutData stationData, StationState newState)
	{
		this.stationData = stationData;
		this.newState = newState;
	}
	
	@Override
	public void run() {
		System.out.println("Changing State to: " + newState.toString());
		stationData.changeState(newState);

	}

}
