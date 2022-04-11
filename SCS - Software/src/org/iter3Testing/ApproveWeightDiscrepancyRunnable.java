package org.iter3Testing;

import org.driver.AttendantUnit;

public class ApproveWeightDiscrepancyRunnable implements Runnable {

	private AttendantUnit attendantUnit;
	private int stationIndex;
	
	public ApproveWeightDiscrepancyRunnable(AttendantUnit attendantUnit, int stationIndex) {
		this.attendantUnit = attendantUnit;
		this.stationIndex = stationIndex;
	}

	@Override
	public void run() {
		System.out.println("Approving weight discrepancy");
		attendantUnit.getAttendantSoftware().overrideWeightIssue(stationIndex);
	}

}