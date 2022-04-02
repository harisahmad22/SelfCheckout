package org.iter2Testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.controlSoftware.data.BankClientInfo;
import org.controlSoftware.deviceHandlers.payment.PayWithDebitCard;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

/**
 * @author Harrison Drew - 30115014
 * Test cases for PayWithDebitCard
 */

@RunWith(JUnit4.class)
public class PayWithDebitCardTest {
	
	private SelfCheckoutStation scs;
	private Card card;
	private CardData cardData;
	private String pin;
	private BankClientInfo bankInfo;
	private BigDecimal balance;
	private BigDecimal totalDue;
	private BigDecimal limit;
	private PayWithDebitCard pwdc;
	private CardReader reader;
	
	/**
	 * Initial Test Setup
	 */
	@Before
	public void setup() {
		this.balance = new BigDecimal(99999);
		this.limit = new BigDecimal(99999);
		this.totalDue = new BigDecimal(167);
		this.pin = "1234";
		this.scs = new DummySelfCheckoutStation();
		this.reader = new CardReader();
		this.card = new Card("Debit", "30115014", "Jane Doe", "064", pin, true, true);
		this.bankInfo = new BankClientInfo("Debit", "30115014", "Jane Doe", "064", pin, true, true, balance, limit);
		pwdc = new PayWithDebitCard(scs, card, cardData, pin, bankInfo);
//		reader.endConfigurationPhase(); // Can't call method as of Hardware v3.0.1
		pwdc.enabled(reader);
		
	}
	
	/**
	 * Tests the Enabled State
	 */
	@Test
	public void enabledTest() {		
		pwdc.enabled(reader);
	}
	
	/**
	 * Tests the Disabled State
	 */
	@Test
	public void disabledTest() {		
		pwdc.disabled(reader);
	}
	
	/**
	 * Tests a successful card insert
	 */
	@Test
	public void cardInsertedTest() {	
		pwdc.cardInserted(reader);
		cardData = pwdc.getCardData();
		assertEquals(cardData, pwdc.getCardData());
	}
	
	/**
	 * Tests a failed card insert
	 */
	@Test
	public void cardInsertedFailedTest() {	
		Card failCard = new Card("Debit", "30115014", "Jane Doe", "064", pin, true, false);
		PayWithDebitCard failpwdc = new PayWithDebitCard(scs, failCard, cardData, pin, bankInfo);
		failpwdc.cardInserted(reader);		
		cardData = pwdc.getCardData();
		assertEquals(cardData, pwdc.getCardData());
	}
	
	/**
	 * Tests a successful card swipe
	 */
	@Test
	public void cardSwipedTest() {
		pwdc.cardSwiped(reader);		
		cardData = pwdc.getCardData();
		assertEquals(cardData, pwdc.getCardData());
		
	}
	
	/**
	 * Tests a failed card swipe
	 */
	@Test
	public void cardSwipedErrorTest() {
		for(int i = 0; i < 100; i++) {
			pwdc.cardSwiped(reader);		
			cardData = pwdc.getCardData();
		}
		assertEquals(cardData, pwdc.getCardData());
	}
	
	/**
	 * Tests a successful card tap
	 */
	@Test
	public void cardTapTest() {
		pwdc.cardTapped(reader);		
		cardData = pwdc.getCardData();
		assertEquals(cardData, pwdc.getCardData());
	}
	
	/**
	 * Tests a failed card tap
	 */
	@Test
	public void cardTapErrorTest() {
		for(int i = 0; i < 100; i++) {
			pwdc.cardTapped(reader);		
			cardData = pwdc.getCardData();
		}
		assertEquals(cardData, pwdc.getCardData());
	}
	
	/**
	 * Tests a card removal
	 */
	@Test
	public void cardRemoveTest() {
		pwdc.cardRemoved(reader);		
		
	}
	
	/**
	 * Tests a read of the card data
	 */
	@Test
	public void cardDataTest() {
		pwdc.cardDataRead(reader, cardData);		
		
	}
	
	/**
	 * Tests a successful verification of a transaction with a bank
	 */
	@Test
	public void verifyBankingInfoTest() {
		boolean verified = true;
		pwdc.cardTapped(reader);		
		cardData = pwdc.getCardData();
		verified = pwdc.verifyBankingInfo(reader, totalDue);		
		assertTrue(verified);
	}
	
	/**
	 * Tests a successful verification of a transaction with a bank with a swipe
	 */
	@Test
	public void verifiedCardSwipedTest() {
		boolean verified = true;
		pwdc.cardSwiped(reader);		
		cardData = pwdc.getCardData();
		verified = pwdc.verifyBankingInfo(reader, totalDue);		
		assertTrue(verified);
	}
	
	/**
	 * Tests a failed verification of a transaction with a bank (not enough balance)
	 */
	@Test
	public void failBalanceTest() {
		boolean verified = true;
		BigDecimal failBalance = new BigDecimal(0);
		BankClientInfo fakeBankInfo = new BankClientInfo("Debit", "30115014", "Jane Doe", "064", pin, true, true, failBalance, limit);
		PayWithDebitCard failpwdc = new PayWithDebitCard(scs, card, cardData, pin, fakeBankInfo);
		failpwdc.cardTapped(reader);		
		cardData = failpwdc.getCardData();
		verified = failpwdc.verifyBankingInfo(reader, totalDue);		
		assertFalse(verified);
	}
	
	/**
	 * Tests a failed verification of a transaction with a bank (different cvv)
	 */
	@Test
	public void failCVVTest() {
		boolean verified = true;
		Card failCard = new Card("Debit", "30115014", "Jane Doe", "999", pin, true, false);
		PayWithDebitCard failpwdc = new PayWithDebitCard(scs, failCard, cardData, pin, bankInfo);
		failpwdc.cardTapped(reader);		
		cardData = failpwdc.getCardData();
		verified = failpwdc.verifyBankingInfo(reader, totalDue);		
		assertFalse(verified);
	}
	
	/**
	 * Tests a failed verification of a transaction with a bank (different holder)
	 */
	@Test
	public void failHolderTest() {
		boolean verified = true;
		Card failCard = new Card("Debit", "30115014", "Unknown", "999", pin, true, false);
		PayWithDebitCard failpwdc = new PayWithDebitCard(scs, failCard, cardData, pin, bankInfo);
		failpwdc.cardTapped(reader);		
		cardData = failpwdc.getCardData();
		verified = failpwdc.verifyBankingInfo(reader, totalDue);		
		assertFalse(verified);
	}
	
	/**
	 * Tests a failed verification of a transaction with a bank (different number)
	 */
	@Test
	public void failNumTest() {
		boolean verified = true;
		Card failCard = new Card("Debit", "0", "Jane Doe", "999", pin, true, false);
		PayWithDebitCard failpwdc = new PayWithDebitCard(scs, failCard, cardData, pin, bankInfo);
		failpwdc.cardTapped(reader);		
		cardData = failpwdc.getCardData();
		verified = failpwdc.verifyBankingInfo(reader, totalDue);		
		assertFalse(verified);
	}
	
}
