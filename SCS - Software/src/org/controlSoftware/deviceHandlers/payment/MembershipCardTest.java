package org.controlSoftware.deviceHandlers.payment;


import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import org.controlSoftware.GUI.SelfCheckoutGUIMaster;
import org.controlSoftware.deviceHandlers.membership.ScansMembershipCard;
import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutSoftware;
import org.driver.SelfCheckoutStationUnit;
import org.driver.SelfCheckoutData.StationState;
import org.driver.databases.MembershipDatabase;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.ChipFailureException;
import org.lsmr.selfcheckout.MagneticStripeFailureException;
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
	
	private boolean readSuccessful = false;
	
	@Before
	public void setUp() {
		this.stationUnit = new SelfCheckoutStationUnit(stationId);
		this.checkoutData = new SelfCheckoutData(stationUnit.getSelfCheckoutStationHardware());
		this.checkoutSoftware = new SelfCheckoutSoftware(stationUnit, checkoutData);
		this.membershipHandler = checkoutSoftware.getMembershipCardHandler();
		
		this.memberData = membershipHandler.getMemberData();
		
		this.guiMaster = new SelfCheckoutGUIMaster(stationUnit.getSelfCheckoutStationHardware(), checkoutData);
		
		this.memberCard1 = new Card("Member", member1Num, "tester", null, null, false, false);
		this.memberCard2 = new Card("Member", member2Num, "tester", null, null, false, false);
		
		checkoutSoftware.attachObservers();
		checkoutData.enablePaymentDevices();
		
		readSuccessful = false;
		
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
		checkoutData.changeState(StationState.SWIPE_MEMBERSHIP);
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
		
		checkoutData.changeState(StationState.SWIPE_MEMBERSHIP);
		stationUnit.getSelfCheckoutStationHardware().cardReader.tap(memberCard1);
	
		
		assertTrue(checkoutData.getMembershipID() == null);
	}
	
	@Test
	public void TestSwipeCard() throws IOException {
		memberData.addMember(member1Num, 0);
		
		checkoutData.changeState(StationState.SWIPE_MEMBERSHIP);
		implementSwipe(memberCard1);
		
		assertTrue(checkoutData.getMembershipID() == member1Num);
	}
		
	
	
	@Test
	public void TestLoyaltyPointsAfterPurchase() {
		memberData.addMember(member2Num, 1000); 
		
		checkoutData.setTotalMoneyPaid(new BigDecimal(100));
		
		membershipHandler.setPointsPerDollarSpent(5);
		membershipHandler.applyMembershipBenefits(member2Num, checkoutData.getTotalMoneyPaid());
		
		int expectedPoints = membershipHandler.getLoyaltyPoints(member2Num) + (100 * 5);
		
		assertTrue(memberData.getDatabase().get(member2Num) == expectedPoints);
		
	}
	
	@Test 
	public void TestDiscountSet(){
		membershipHandler.setPercentDiscount(0.05);
		assertTrue(membershipHandler.getPercentDiscount() == 0.05);
	}
	
	@Test
	public void TestNonExistentMember() {
		 Card testCard = new Card("Member", "008452", "tester", null, null, false, false);
		 checkoutData.changeState(StationState.SWIPE_MEMBERSHIP);
		 implementSwipe(testCard);
		 
		 assertTrue(checkoutData.getCurrentState() == StationState.BAD_MEMBERSHIP);
		 
	}


	//if swipe does not read data, simulates customer trying again
	public void implementSwipe(Card card) {

		while(!readSuccessful) {
			try {
				stationUnit.getSelfCheckoutStationHardware().cardReader.swipe(card);
				readSuccessful = true;
			} catch (IOException e) {
				if(e instanceof MagneticStripeFailureException) {
					continue;
				}
				else {
					e.printStackTrace();
					break;
				}
			}
		}
	}

}


