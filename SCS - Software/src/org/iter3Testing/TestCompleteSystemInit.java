package org.iter3Testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.controlSoftware.attendant.AttendantSoftware;
import org.driver.AttendantUnit;
import org.driver.SelfCheckoutStationUnit;
import org.driver.SelfCheckoutData.StationState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.devices.SupervisionStation;

//This Class will create a list of 10 checkout stations
//And link it to a supervision station

@RunWith(JUnit4.class)
public class TestCompleteSystemInit {
	
	private int stationCount = 10;
	private ArrayList<SelfCheckoutStationUnit> checkoutStationUnits = new ArrayList<SelfCheckoutStationUnit>();
	private AttendantUnit attendantUnit;
	
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private ExecutorService systemExecutor = Executors.newFixedThreadPool(stationCount);
	
	//Initialize
	@Before
	public void setup() {
		for (int i = 0; i < stationCount; i++)
		{
			SelfCheckoutStationUnit unit = new SelfCheckoutStationUnit(i);
			checkoutStationUnits.add(unit);
		}
		
		this.attendantUnit = new AttendantUnit();
		attendantUnit.attachCheckoutStationUnits(checkoutStationUnits);
		
		for (SelfCheckoutStationUnit unit : checkoutStationUnits)
		{
			unit.attachAttendant(this.attendantUnit);
		}
		
	}
	
	public ArrayList<SelfCheckoutStationUnit> getCheckoutStationUnits() {
		return checkoutStationUnits;
	}

	public AttendantUnit getAttendantUnit() {
		return attendantUnit;
	}
	
	@Test
	public void testAttendantAttachment()
	{
		boolean attached = true;
		
		for (SelfCheckoutStationUnit unit : checkoutStationUnits)
		{ if (!unit.getAttendantUnit().equals(this.attendantUnit)) { attached = false; } }
		
		assertTrue(attached);
	}
	

	//========================================Attendant Use Case Tests========================================
	
	@Test
	public void testAttendantBlocksInactiveStation()
	{
		Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size()); 
		
		SelfCheckoutStationUnit stationToBlock = checkoutStationUnits.get(randIndex);
		
		attendantUnit.getAttendantSoftware().blockStation(stationToBlock);
		
		assertTrue(stationToBlock.getSelfCheckoutData().getCurrentState() == StationState.INACTIVE);
	}
	
	@Test
	public void testAttendantBlocksStation()
	{
		Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size()); 
		
		SelfCheckoutStationUnit stationToBlock = checkoutStationUnits.get(randIndex);
		stationToBlock.getSelfCheckoutData().setCurrentState(StationState.NORMAL);
		
		attendantUnit.getAttendantSoftware().blockStation(stationToBlock);
		
		assertTrue(stationToBlock.getSelfCheckoutData().getCurrentState() == StationState.BLOCKED);
	}
	
	@Test
	public void testAttendantUnBlocksNormalStation()
	{
		Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size()); 
		
		SelfCheckoutStationUnit stationToBlock = checkoutStationUnits.get(randIndex);
		stationToBlock.getSelfCheckoutData().setCurrentState(StationState.NORMAL);
		
		attendantUnit.getAttendantSoftware().unBlockStation(stationToBlock);
		
		assertTrue(stationToBlock.getSelfCheckoutData().getCurrentState() == StationState.NORMAL);
	}
	
	@Test
	public void testAttendantUnBlocksStation()
	{
		Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size()); 
		
		SelfCheckoutStationUnit stationToBlock = checkoutStationUnits.get(randIndex);
		stationToBlock.getSelfCheckoutData().setCurrentState(StationState.BLOCKED);
		
		attendantUnit.getAttendantSoftware().unBlockStation(stationToBlock);
		
		assertTrue(stationToBlock.getSelfCheckoutData().getCurrentState() == StationState.INACTIVE);
	}
	//========================================Attendant Use Case Tests========================================	
	
	

}
