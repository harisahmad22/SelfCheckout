package org.iter3Testing;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.controlSoftware.GUI.SelfCheckoutGUIMaster;
import org.controlSoftware.deviceHandlers.membership.ScansMembershipCard;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutSoftware;
import org.driver.SelfCheckoutStationUnit;
import org.driver.databases.MembershipDatabase;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.ChipFailureException;
import org.lsmr.selfcheckout.TapFailureException;

public class MembershipCardTest {
	
	private SelfCheckoutStationUnit stationUnit;
	private SelfCheckoutSoftware checkoutSoftware;
	private SelfCheckoutData checkoutData;
	private int stationId = 1;
	
	private SelfCheckoutGUIMaster guiMaster;
	
	private MembershipDatabase memberData;
	private ScansMembershipCard membershipHandler;
	
	private Card memberCard1;
	private Card memberCard2;
	
	private String member1Num = "55555555";
	private String member2Num = "11111111";
	
	@Before
	public void setUp() {
		this.stationUnit = new SelfCheckoutStationUnit(stationId);
		this.checkoutData = new SelfCheckoutData(stationUnit.getSelfCheckoutStationHardware());
		this.checkoutSoftware = new SelfCheckoutSoftware(stationUnit, checkoutData);
		this.membershipHandler = new ScansMembershipCard(checkoutData, checkoutSoftware);
		
		this.memberData = new MembershipDatabase();
		membershipHandler.setMembershipCards(memberData.getDatabase());
		
		this.guiMaster = new SelfCheckoutGUIMaster(stationUnit.getSelfCheckoutStationHardware(), checkoutData);
		
		this.memberCard1 = new Card("Member", member1Num, "tester", null, null, false, false);
		this.memberCard2 = new Card("Member", member2Num, "tester", null, null, false, false);
		
		checkoutSoftware.attachObservers();
		checkoutData.enablePaymentDevices();
		
	}
	
	
	@Test
	public void TestAddNewMember() {
		
		memberData.addMember(member1Num, 0);
		memberData.addMember(member2Num, 1000);
		
		Map<String, Integer> database = memberData.getDatabase();
	
		
		assertTrue(database.containsKey(member1Num) && database.containsKey(member2Num));
	}
	
	//membership cards have no chip, insert should not work
	@Test 
	public void TestInsertCard(){
		boolean thrown = false;
		
		memberData.addMember(member1Num, 0);
		membershipHandler.setMembershipCards(memberData.getDatabase());
		
		try {
			stationUnit.getSelfCheckoutStationHardware().cardReader.insert(memberCard1, null);
		} catch (IOException e) {
			if(e instanceof ChipFailureException) {
				thrown = true;
			}
		}
		
		assertTrue(thrown);
	}
	
	//in this case, membership cards can only be swiped or customer number manually inputed
	@Test
	public void TestTapCard() throws IOException {
		
		memberData.addMember(member1Num, 0);
		membershipHandler.setMembershipCards(memberData.getDatabase());
		

		stationUnit.getSelfCheckoutStationHardware().cardReader.tap(memberCard1);
	
		
		assertTrue(memberData.getDatabase().containsKey(member1Num));
	}
	
	@Test
	public void TestSwipeCard() throws IOException {
		memberData.addMember(member1Num, 0);
		//membershipHandler.setMembershipCards(memberData.getDatabase());
		
		stationUnit.getSelfCheckoutStationHardware().cardReader.swipe(memberCard1);
		
		assertTrue(checkoutData.getMembershipID() == member1Num);
	}
		
	
	
	@Test
	public void TestSwipeCardDisabled() {
		
	}
	

}
