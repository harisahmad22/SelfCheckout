package org.iter3Testing;

import org.lsmr.selfcheckout.IllegalConfigurationPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalErrorPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalPhaseSimulationException;
import org.lsmr.selfcheckout.InvalidArgumentSimulationException;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.observers.ReceiptPrinterObserver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
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
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.InvalidArgumentSimulationException;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SupervisionStation;

//This Class will create a list of 10 checkout stations
//And link it to a supervision station

@RunWith(JUnit4.class)
public class TestCompleteSystemInit {
	
	private int stationCount = 10;
	private ArrayList<SelfCheckoutStationUnit> checkoutStationUnits = new ArrayList<SelfCheckoutStationUnit>();
	private AttendantUnit attendantUnit;
	
	private static Banknote fiveDollarBanknote = new Banknote(SelfCheckoutStationUnit.CAD, 5);
	private static Banknote tenDollarBanknote = new Banknote(SelfCheckoutStationUnit.CAD, 10);
	private static Banknote twentyDollarBanknote = new Banknote(SelfCheckoutStationUnit.CAD, 20);
	private static Banknote fiftyDollarBanknote = new Banknote(SelfCheckoutStationUnit.CAD, 50);
	private static Banknote fiveDollarBanknoteUSD = new Banknote(Currency.getInstance("USD"), 5);
	private static Banknote twelveDollarBanknote = new Banknote(SelfCheckoutStationUnit.CAD, 12);
	
	private static Coin nickel = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal("0.05"));
	private static Coin quarter = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal("0.25"));
	private static Coin loonie = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal("1.00"));
	private static Coin toonie = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal("2.00"));
	private static Coin quarterUSD = new Coin(Currency.getInstance("USD"), new BigDecimal("0.25"));
	private static Coin invalidCoin = new Coin(SelfCheckoutStationUnit.CAD, new BigDecimal("0.75"));
	
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
	public void testAttendantLogsIn()
	{
		// do some stuff
	}
	
	@Test
	public void testAttendantLogsOut()
	{
		// do more stuff
	}
	
	@Test
	public void testAttendantStartsRunningStation()
	{
		Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size());
		
		SelfCheckoutStationUnit stationToStart = checkoutStationUnits.get(randIndex);
		stationToStart.getSelfCheckoutData().setCurrentState(StationState.NORMAL);
		
		attendantUnit.getAttendantSoftware().startupStation(stationToStart);
		
		assertTrue(stationToStart.getSelfCheckoutData().getCurrentState() == StationState.NORMAL);
	}
	
	@Test
	public void testAttendantStartsStation()
	{
		Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size());
		
		SelfCheckoutStationUnit stationToStart = checkoutStationUnits.get(randIndex);
		
		attendantUnit.getAttendantSoftware().startupStation(stationToStart);
		
		assertTrue(stationToStart.getSelfCheckoutData().getCurrentState() == StationState.WELCOME);
	}
	
	@Test
	public void testAttendantShutsDownRunningStation()
	{
		Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size());
		
		SelfCheckoutStationUnit stationToShutDown = checkoutStationUnits.get(randIndex);
		stationToShutDown.getSelfCheckoutData().setCurrentState(StationState.NORMAL);
		
		attendantUnit.getAttendantSoftware().shutdownStation(stationToShutDown);
		
		assertTrue(stationToShutDown.getSelfCheckoutData().getCurrentState() == StationState.NORMAL);
	}
	
	@Test
	public void testAttendantShutsDownStation()
	{
		Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size());
		
		SelfCheckoutStationUnit stationToShutDown = checkoutStationUnits.get(randIndex);
		
		attendantUnit.getAttendantSoftware().shutdownStation(stationToShutDown);
		
		assertTrue(stationToShutDown.getSelfCheckoutData().getCurrentState() == StationState.INACTIVE);
	}
	
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
	
	/*
     * testing for Attendant empties the coin storage unit
     * Author: Jacky Liang
     * -----------------------------------------------------------------------------------------------------------------------
     */
    
    @Test
    public void testEmptyCoinStorage() throws SimulationException, OverloadException
    {
    	
    	Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size()); 
		
		SelfCheckoutStationUnit stationToTest = checkoutStationUnits.get(randIndex);
    	
    	Coin[] coins= {nickel,quarter,loonie,toonie};
    	
    	stationToTest.getSelfCheckoutStationHardware().coinStorage.load(coins);
    	List<Coin> returnCoins = new ArrayList<Coin>(stationToTest.getAttendantUnit().getAttendantSoftware().emptyCoinStorageUnit(stationToTest.getStationID()));

    	assertTrue(returnCoins.contains(nickel));
    	assertTrue(returnCoins.contains(quarter));
    	assertTrue(returnCoins.contains(loonie));
    	assertTrue(returnCoins.contains(toonie));
    	
    	assertEquals(stationToTest.getSelfCheckoutStationHardware().coinStorage.getCoinCount(),0);
    	
    	String StationIDString=Integer.toString(stationToTest.getStationID());
    	stationToTest.getSelfCheckoutStationHardware().coinStorage.load(coins);
    	returnCoins.clear();
    	returnCoins = stationToTest.getAttendantUnit().getAttendantSoftware().emptyCoinStorageUnit(StationIDString);

    	assertTrue(returnCoins.contains(nickel));
    	assertTrue(returnCoins.contains(quarter));
    	assertTrue(returnCoins.contains(loonie));
    	assertTrue(returnCoins.contains(toonie));
    	
    	assertEquals(stationToTest.getSelfCheckoutStationHardware().coinStorage.getCoinCount(),0);
    }
    
    /*
     * testing for Attendant empties the banknote storage unit
     * Author: Jacky Liang
     * -----------------------------------------------------------------------------------------------------------------------
     */
    
    @Test
    public void testEmptyBanknoteStorage() throws SimulationException, OverloadException
    {
    	
    	Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size()); 
		
		SelfCheckoutStationUnit stationToTest = checkoutStationUnits.get(randIndex);
    	
    	Banknote[] banknotes= {fiveDollarBanknote,tenDollarBanknote,twentyDollarBanknote,fiftyDollarBanknote};
    	
    	stationToTest.getSelfCheckoutStationHardware().banknoteStorage.load(banknotes);
    	List<Banknote> returnBanknotes = new ArrayList<Banknote>(stationToTest.getAttendantUnit().getAttendantSoftware().emptyBanknoteStorageUnit(stationToTest.getStationID()));
    	
    	assertTrue(returnBanknotes.contains(fiveDollarBanknote));
    	assertTrue(returnBanknotes.contains(tenDollarBanknote));
    	assertTrue(returnBanknotes.contains(twentyDollarBanknote));
    	assertTrue(returnBanknotes.contains(fiftyDollarBanknote));
    	
    	assertEquals(stationToTest.getSelfCheckoutStationHardware().banknoteStorage.getBanknoteCount(),0);
    	
    	String StationIDString=Integer.toString(stationToTest.getStationID());
    	stationToTest.getSelfCheckoutStationHardware().banknoteStorage.load(banknotes);
    	returnBanknotes.clear();
    	returnBanknotes = stationToTest.getAttendantUnit().getAttendantSoftware().emptyBanknoteStorageUnit(StationIDString);
    	
    	assertTrue(returnBanknotes.contains(fiveDollarBanknote));
    	assertTrue(returnBanknotes.contains(tenDollarBanknote));
    	assertTrue(returnBanknotes.contains(twentyDollarBanknote));
    	assertTrue(returnBanknotes.contains(fiftyDollarBanknote));
    	
    	assertEquals(stationToTest.getSelfCheckoutStationHardware().banknoteStorage.getBanknoteCount(),0);
    }

    /*
     * testing for Attendant refills the coin dispenser 
     * Author: Jacky Liang
     * -----------------------------------------------------------------------------------------------------------------------
     */
    
    @Test
    public void testRefillCoinDispenser() throws SimulationException, OverloadException
    {
    	Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size()); 
		
		SelfCheckoutStationUnit stationToTest = checkoutStationUnits.get(randIndex);
		
		Coin[] emptyCoin= {};
		
		stationToTest.getAttendantUnit().getAttendantSoftware().refillCoinDispenser(randIndex, emptyCoin);
    	
    	Coin[] coins= {nickel,nickel,quarter,loonie,toonie};
    	
    	stationToTest.getAttendantUnit().getAttendantSoftware().refillCoinDispenser(randIndex, coins);
    	
    	List<Coin> returnCoins = new ArrayList<Coin>(stationToTest.getSelfCheckoutStationHardware().coinDispensers.get(new BigDecimal("0.05")).unload());
    	
    	assertTrue(returnCoins.contains(nickel));
    	
    	int NumOfNickel=0;
    	for(Coin coin : returnCoins)
    		if (coin.equals(nickel)) NumOfNickel++;
    	
    	assertEquals(NumOfNickel,2);
    	returnCoins.clear();
    	
    	returnCoins = stationToTest.getSelfCheckoutStationHardware().coinDispensers.get(new BigDecimal("0.25")).unload();
    	assertTrue(returnCoins.contains(quarter));
    	
    	int NumOfquarter=0;
    	for(Coin coin : returnCoins)
    		if (coin.equals(quarter)) NumOfquarter++;
    	
    	assertEquals(NumOfquarter,1);
    	returnCoins.clear();
    	
    	returnCoins = stationToTest.getSelfCheckoutStationHardware().coinDispensers.get(new BigDecimal("1.00")).unload();
    	assertTrue(returnCoins.contains(loonie));
    	
    	int NumOfloonie=0;
    	for(Coin coin : returnCoins)
    		if (coin.equals(loonie)) NumOfloonie++;
    	
    	assertEquals(NumOfloonie,1);
    	returnCoins.clear();
    	
    	returnCoins = stationToTest.getSelfCheckoutStationHardware().coinDispensers.get(new BigDecimal("2.00")).unload();
    	assertTrue(returnCoins.contains(toonie));
    	
    	int NumOftoonie=0;
    	for(Coin coin : returnCoins)
    		if (coin.equals(toonie)) NumOftoonie++;
    	
    	assertEquals(NumOftoonie,1);
    	returnCoins.clear();
    	
    	String StationIDString=Integer.toString(stationToTest.getStationID());
		
		stationToTest.getAttendantUnit().getAttendantSoftware().refillCoinDispenser(StationIDString, emptyCoin);
    	
    	stationToTest.getAttendantUnit().getAttendantSoftware().refillCoinDispenser(StationIDString, coins);
    	
    	returnCoins = stationToTest.getSelfCheckoutStationHardware().coinDispensers.get(new BigDecimal("0.05")).unload();
    	
    	assertTrue(returnCoins.contains(nickel));
    	
    	NumOfNickel=0;
    	for(Coin coin : returnCoins)
    		if (coin.equals(nickel)) NumOfNickel++;
    	
    	assertEquals(NumOfNickel,2);
    	returnCoins.clear();
    	
    	returnCoins = stationToTest.getSelfCheckoutStationHardware().coinDispensers.get(new BigDecimal("0.25")).unload();
    	assertTrue(returnCoins.contains(quarter));
    	
    	NumOfquarter=0;
    	for(Coin coin : returnCoins)
    		if (coin.equals(quarter)) NumOfquarter++;
    	
    	assertEquals(NumOfquarter,1);
    	returnCoins.clear();
    	
    	returnCoins = stationToTest.getSelfCheckoutStationHardware().coinDispensers.get(new BigDecimal("1.00")).unload();
    	assertTrue(returnCoins.contains(loonie));
    	
    	NumOfloonie=0;
    	for(Coin coin : returnCoins)
    		if (coin.equals(loonie)) NumOfloonie++;
    	
    	assertEquals(NumOfloonie,1);
    	returnCoins.clear();
    	
    	returnCoins = stationToTest.getSelfCheckoutStationHardware().coinDispensers.get(new BigDecimal("2.00")).unload();
    	assertTrue(returnCoins.contains(toonie));
    	
    	NumOftoonie=0;
    	for(Coin coin : returnCoins)
    		if (coin.equals(toonie)) NumOftoonie++;
    	
    	assertEquals(NumOftoonie,1);
    	
    }

    
    /*
     * testing for Attendant refills the banknote dispenser 
     * Author: Jacky Liang
     * -----------------------------------------------------------------------------------------------------------------------
     */
    
    @Test
    public void testRefillBanknoteDispenser() throws SimulationException, OverloadException
    {
    	Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size()); 
		
		SelfCheckoutStationUnit stationToTest = checkoutStationUnits.get(randIndex);
		
		Banknote[] emptyBanknote= {};
		
		stationToTest.getAttendantUnit().getAttendantSoftware().refillbanknoteDispenser(randIndex, emptyBanknote);
    	
		Banknote[] banknotes= {fiveDollarBanknote,fiveDollarBanknote,tenDollarBanknote,twentyDollarBanknote,fiftyDollarBanknote};
    	
    	stationToTest.getAttendantUnit().getAttendantSoftware().refillbanknoteDispenser(randIndex, banknotes);
    	
    	List<Banknote> returnBanknotes = new ArrayList<Banknote>(stationToTest.getSelfCheckoutStationHardware().banknoteDispensers.get(5).unload());
    	
    	assertTrue(returnBanknotes.contains(fiveDollarBanknote));
    	
    	int NumOffiveDollarBanknote=0;
    	for(Banknote banknote : banknotes)
    		if (banknote.equals(fiveDollarBanknote)) NumOffiveDollarBanknote++;
    	
    	assertEquals(NumOffiveDollarBanknote,2);
    	returnBanknotes.clear();
    	
    	returnBanknotes = stationToTest.getSelfCheckoutStationHardware().banknoteDispensers.get(10).unload();
    	assertTrue(returnBanknotes.contains(tenDollarBanknote));
    	
    	int NumOftenDollarBanknote=0;
    	for(Banknote banknote : banknotes)
    		if (banknote.equals(tenDollarBanknote)) NumOftenDollarBanknote++;
    	
    	assertEquals(NumOftenDollarBanknote,1);
    	returnBanknotes.clear();
    	
    	returnBanknotes = stationToTest.getSelfCheckoutStationHardware().banknoteDispensers.get(20).unload();
    	assertTrue(returnBanknotes.contains(twentyDollarBanknote));
    	
    	int NumOftwentyDollarBanknote=0;
    	for(Banknote banknote : banknotes)
    		if (banknote.equals(twentyDollarBanknote)) NumOftwentyDollarBanknote++;
    	
    	assertEquals(NumOftwentyDollarBanknote,1);
    	returnBanknotes.clear();
    	
    	returnBanknotes = stationToTest.getSelfCheckoutStationHardware().banknoteDispensers.get(50).unload();
    	assertTrue(returnBanknotes.contains(fiftyDollarBanknote));
    	
    	int NumOffiftyDollarBanknote=0;
    	for(Banknote banknote : banknotes)
    		if (banknote.equals(fiftyDollarBanknote)) NumOffiftyDollarBanknote++;
    	
    	assertEquals(NumOffiftyDollarBanknote,1);
    	returnBanknotes.clear();
    	
    	String StationIDString=Integer.toString(stationToTest.getStationID());
		
		stationToTest.getAttendantUnit().getAttendantSoftware().refillbanknoteDispenser(StationIDString, emptyBanknote);
    	
    	stationToTest.getAttendantUnit().getAttendantSoftware().refillbanknoteDispenser(StationIDString, banknotes);
    	
    	returnBanknotes = stationToTest.getSelfCheckoutStationHardware().banknoteDispensers.get(5).unload();
    	
    	assertTrue(returnBanknotes.contains(fiveDollarBanknote));
    	
    	NumOffiveDollarBanknote=0;
    	for(Banknote banknote : banknotes)
    		if (banknote.equals(fiveDollarBanknote)) NumOffiveDollarBanknote++;
    	
    	assertEquals(NumOffiveDollarBanknote,2);
    	returnBanknotes.clear();
    	
    	returnBanknotes = stationToTest.getSelfCheckoutStationHardware().banknoteDispensers.get(10).unload();
    	assertTrue(returnBanknotes.contains(tenDollarBanknote));
    	
    	NumOftenDollarBanknote=0;
    	for(Banknote banknote : banknotes)
    		if (banknote.equals(tenDollarBanknote)) NumOftenDollarBanknote++;
    	
    	assertEquals(NumOftenDollarBanknote,1);
    	returnBanknotes.clear();
    	
    	returnBanknotes = stationToTest.getSelfCheckoutStationHardware().banknoteDispensers.get(20).unload();
    	assertTrue(returnBanknotes.contains(twentyDollarBanknote));
    	
    	NumOftwentyDollarBanknote=0;
    	for(Banknote banknote : banknotes)
    		if (banknote.equals(twentyDollarBanknote)) NumOftwentyDollarBanknote++;
    	
    	assertEquals(NumOftwentyDollarBanknote,1);
    	returnBanknotes.clear();
    	
    	returnBanknotes = stationToTest.getSelfCheckoutStationHardware().banknoteDispensers.get(50).unload();
    	assertTrue(returnBanknotes.contains(fiftyDollarBanknote));
    	
    	NumOffiftyDollarBanknote=0;
    	for(Banknote banknote : banknotes)
    		if (banknote.equals(fiftyDollarBanknote)) NumOffiftyDollarBanknote++;
    	
    	assertEquals(NumOffiftyDollarBanknote,1);
    	returnBanknotes.clear();
    }
    
    /*
     * testing for Attendant adds ink to receipt printer
     * Author: Jacky Liang
     * -----------------------------------------------------------------------------------------------------------------------
     */
    
    @Test
    public void testAddTooMuchInkToPrinter()
    {
    	Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size()); 
		
		SelfCheckoutStationUnit stationToTest = checkoutStationUnits.get(randIndex);
		
		boolean OverloadExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterInk(stationToTest, stationToTest.getSelfCheckoutStationHardware().printer.MAXIMUM_INK+1);
		} catch (OverloadException e) {
			OverloadExceptionCaught=true;
		}
		
		assertTrue(OverloadExceptionCaught);
		
		OverloadExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterInk(stationToTest.getStationID(), stationToTest.getSelfCheckoutStationHardware().printer.MAXIMUM_INK+1);
		} catch (OverloadException e) {
			OverloadExceptionCaught=true;
		}
		
		assertTrue(OverloadExceptionCaught);
		
		OverloadExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterInk(Integer.toString(stationToTest.getStationID()), stationToTest.getSelfCheckoutStationHardware().printer.MAXIMUM_INK+1);
		} catch (OverloadException e) {
			OverloadExceptionCaught=true;
		}
		
		assertTrue(OverloadExceptionCaught);
    }
    
    @Test
    public void testAddNegativeAmountOfInkToPrinter()
    {
    	Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size()); 
		
		SelfCheckoutStationUnit stationToTest = checkoutStationUnits.get(randIndex);
		
		boolean InvalidArgumentSimulationExceptionCaught=false;
		
		try 
		{
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterInk(stationToTest,-1);
		} catch (Exception e) {
			InvalidArgumentSimulationExceptionCaught=true;
		}

		assertTrue(InvalidArgumentSimulationExceptionCaught);
		
		InvalidArgumentSimulationExceptionCaught=false;
		try 
		{
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterInk(stationToTest.getStationID(),-1);
		} catch (Exception e) {
			InvalidArgumentSimulationExceptionCaught=true;
		}

		assertTrue(InvalidArgumentSimulationExceptionCaught);
		
		InvalidArgumentSimulationExceptionCaught=false;
		try 
		{
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterInk(Integer.toString(stationToTest.getStationID()),-1);
		} catch (Exception e) {
			InvalidArgumentSimulationExceptionCaught=true;
		}

		assertTrue(InvalidArgumentSimulationExceptionCaught);
    }
    
    @Test
    public void testAddInkToPrinterSuccessfully()
    {
    	Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size()); 
		
		SelfCheckoutStationUnit stationToTest = checkoutStationUnits.get(randIndex);
		
		boolean ExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterInk(stationToTest, 20);
		} catch (Exception e) {
			ExceptionCaught=true;
		}
		
		assertFalse(ExceptionCaught);
		
		ExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterInk(stationToTest.getStationID(), 20);
		} catch (Exception e) {
			ExceptionCaught=true;
		}
		
		ExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterInk(Integer.toString(stationToTest.getStationID()), 20);
		} catch (Exception e) {
			ExceptionCaught=true;
		}
		
		assertFalse(ExceptionCaught);
    }
    
    /*
     * testing for Attendant adds paper to receipt printer
     * Author: Jacky Liang
     * -----------------------------------------------------------------------------------------------------------------------
     */
    
    @Test
    public void testAddTooMuchPaperToPrinter()
    {
    	Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size()); 
		
		SelfCheckoutStationUnit stationToTest = checkoutStationUnits.get(randIndex);
		
		boolean OverloadExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterPaper(stationToTest, stationToTest.getSelfCheckoutStationHardware().printer.MAXIMUM_PAPER+1);
		} catch (OverloadException e) {
			OverloadExceptionCaught=true;
		}
		
		assertTrue(OverloadExceptionCaught);
		
		OverloadExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterPaper(stationToTest.getStationID(), stationToTest.getSelfCheckoutStationHardware().printer.MAXIMUM_PAPER+1);
		} catch (OverloadException e) {
			OverloadExceptionCaught=true;
		}
		
		assertTrue(OverloadExceptionCaught);
		
		OverloadExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterPaper(Integer.toString(stationToTest.getStationID()), stationToTest.getSelfCheckoutStationHardware().printer.MAXIMUM_PAPER+1);
		} catch (OverloadException e) {
			OverloadExceptionCaught=true;
		}
		
		assertTrue(OverloadExceptionCaught);
    }
    
    @Test
    public void testAddNegativeAmountOfPaperToPrinter()
    {
    	Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size()); 
		
		SelfCheckoutStationUnit stationToTest = checkoutStationUnits.get(randIndex);
		
		boolean ExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterPaper(stationToTest, -1);
		} catch (Exception e) {
			ExceptionCaught=true;
		}
		
		assertTrue(ExceptionCaught);
		
		ExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterPaper(stationToTest.getStationID(), -1);
		} catch (Exception e) {
			ExceptionCaught=true;
		}
		
		assertTrue(ExceptionCaught);
		
		ExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterPaper(Integer.toString(stationToTest.getStationID()), -1);
		} catch (Exception e) {
			ExceptionCaught=true;
		}
		
		assertTrue(ExceptionCaught);
    }
    
    @Test
    public void testAddPaperToPrinterSuccessfully()
    {
    	Random rand = new Random();
		int randIndex = rand.nextInt(checkoutStationUnits.size()); 
		
		SelfCheckoutStationUnit stationToTest = checkoutStationUnits.get(randIndex);
		
		boolean ExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterPaper(stationToTest, 20);
		} catch (Exception e) {
			ExceptionCaught=true;
		}
		
		assertFalse(ExceptionCaught);
		
		ExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterPaper(stationToTest.getStationID(), 20);
		} catch (Exception e) {
			ExceptionCaught=true;
		}
		
		assertFalse(ExceptionCaught);
		
		ExceptionCaught=false;
		try {
			stationToTest.getAttendantUnit().getAttendantSoftware().updatePrinterPaper(Integer.toString(stationToTest.getStationID()), 20);
		} catch (Exception e) {
			ExceptionCaught=true;
		}
		
		assertFalse(ExceptionCaught);
    }
	//========================================Attendant Use Case Tests========================================	
	
	

}
