package org.lsmr.selfcheckout.devices;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioSystem;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.InvalidArgumentSimulationException;
import org.lsmr.selfcheckout.NullPointerSimulationException;
import org.lsmr.selfcheckout.SimulationException;

/**
 * Simulates the overall self-checkout station.
 * <p>
 * A self-checkout possesses the following units of hardware that the customer
 * can see and interact with:
 * <ul>
 * <li>two electronic scales, with a configurable maximum weight before it
 * overloads, one for the bagging area and one for the scanning area;</li>
 * <li>one touch screen;</li>
 * <li>one receipt printer;</li>
 * <li>one card reader;</li>
 * <li>two scanners (the main one and the handheld one);</li>
 * <li>one input slot for banknotes;</li>
 * <li>one output slot for banknotes;</li>
 * <li>one input slot for coins;</li>
 * <li>one output tray for coins; and,</li>
 * <li>one speaker for audio output (note: you should directly use the
 * {@link AudioSystem} class, if you want to produce sounds).</li>
 * </ul>
 * </p>
 * <p>
 * In addition, these units of hardware are accessible to personnel with a key
 * to unlock the front of the station:
 * <li>one banknote storage unit, with configurable capacity;</li>
 * <li>one or more banknote dispensers, one for each supported denomination of
 * banknote, as configured;</li>
 * <li>one coin storage unit, with configurable capacity; and,</li>
 * <li>one or more coin dispensers, one for each supported denomination of coin,
 * as configured.</li>
 * </ul>
 * </p>
 * <p>
 * And finally, there are certain, additional units of hardware that would only
 * be accessible to someone with the appropriate tools (like a screwdriver,
 * crowbar, or sledge hammer):
 * <ul>
 * <li>one banknote validator; and</li>
 * <li>one coin validator.</li>
 * </ul>
 * </p>
 * <p>
 * Many of these devices are interconnected, to permit coins or banknotes to
 * pass between them. Specifically:
 * <ul>
 * <li>the coin slot is connected to the coin validator (this is a
 * one-directional chain of devices);</li>
 * <li>the coin validator is connected to each of the coin dispensers (i.e., the
 * coin dispensers can be replenished with coins entered by customers), to the
 * coin storage unit (for any overflow coins that do not fit in the dispensers),
 * and to the coin tray for any rejected coins either because the coins are
 * invalid or because even the overflow storage unit is full (this is a
 * one-directional chain of devices);
 * <li>each coin dispenser is connected to the coin tray, to provide change
 * (this is a one-directional chain of devices);</li>
 * <li>the banknote input slot is connected to the banknote validator (this is a
 * <b>two</b>-directional chain of devices as an entered banknotes that are
 * rejected by the validator can be returned to the customer);</li>
 * <li>the banknote validator is connected to the banknote storage unit (this is
 * a one-directional chain of devices); and,</li>
 * <li>each banknote dispenser is connected to the output banknote slot; these
 * dispensers cannot be replenished by banknotes provided by customers (this is
 * a one-directional chain of devices).</li>
 * </ul>
 * </p>
 * <p>
 * All other functionality of the system must be performed in software,
 * installed on the self-checkout station through custom observer classes
 * implementing the various observer interfaces provided.
 * </p>
 * <p>
 * Note that banknote denominations are required to be positive integers, while
 * coin denominations are positive decimal values ({@link BigDecimal} is used
 * for the latter to avoid roundoff problems arising from floating-point
 * operations).
 */
public class SelfCheckoutStation {
	/**
	 * Represents the large scale where items are to be placed once they have been
	 * scanned or otherwise entered.
	 */
	public final ElectronicScale baggingArea;
	/**
	 * Represents the small scale used to weigh items that are sold by weight.
	 */
	public final ElectronicScale scanningArea;
	/**
	 * Represents a touch screen display on which is shown a graphical user
	 * interface.
	 */
	public final TouchScreen screen;
	/**
	 * Represents a printer for receipts.
	 */
	public final ReceiptPrinter printer;
	/**
	 * Represents a device that can read electronic cards, through one or more input
	 * modes according to the setup of the card.
	 */
	public final CardReader cardReader;
	/**
	 * Represents a large, central barcode scanner.
	 */
	public final BarcodeScanner mainScanner;
	/**
	 * Represents a handheld, secondary barcode scanner.
	 */
	public final BarcodeScanner handheldScanner;

	/**
	 * Represents a device that permits banknotes to be entered.
	 */
	public final BanknoteSlot banknoteInput;
	/**
	 * Represents a device that permits banknotes to be given to the customer.
	 */
	public final BanknoteSlot banknoteOutput;
	/**
	 * Represents a device that checks the validity of a banknote, and determines
	 * its denomination.
	 */
	public final BanknoteValidator banknoteValidator;
	/**
	 * Represents a device that stores banknotes.
	 */
	public final BanknoteStorageUnit banknoteStorage;
	/**
	 * Represents the value used to configure the maximum capacity of the banknote
	 * storage unit.
	 */
	public final static int BANKNOTE_STORAGE_CAPACITY = 1000;
	/**
	 * Represents the set of denominations supported by the self-checkout system.
	 */
	public final int[] banknoteDenominations;
	/**
	 * Represents the set of banknote dispensers, indexed by the denomination that
	 * each contains. Note that nothing prevents banknotes of the wrong denomination
	 * to be loaded into a given dispenser.
	 */
	public final Map<Integer, BanknoteDispenser> banknoteDispensers;
	/**
	 * Represents the value used to configure the maximum capacity of the banknote
	 * dispensers. All dispensers are assumed to have equal capacity.
	 */
	public final static int BANKNOTE_DISPENSER_CAPACITY = 100;

	/**
	 * Represents a device that permits coins to be entered.
	 */
	public final CoinSlot coinSlot;
	/**
	 * Represents a device that checks the validity of a coin, and determines its
	 * denomination.
	 */
	public final CoinValidator coinValidator;
	/**
	 * Represents a device that stores coins that have been entered by customers.
	 */
	public final CoinStorageUnit coinStorage;
	/**
	 * Represents the value used to configure the maximum capacity of the coin
	 * storage units. All units are assumed to have the same capacity.
	 */
	public static final int COIN_STORAGE_CAPACITY = 1000;
	/**
	 * Represents the set of denominations of coins supported by this self-checkout
	 * system.
	 */
	public final List<BigDecimal> coinDenominations;
	/**
	 * Represents the set of coin dispensers, indexed by the denomination of coins
	 * contained by each.
	 */
	public final Map<BigDecimal, CoinDispenser> coinDispensers;
	/**
	 * Represents the value used to configure the maximum capacity of the coin
	 * dispensers. All dispensers are assumed to have the same capacity.
	 */
	public static final int COIN_DISPENSER_CAPACITY = 200;
	/**
	 * Represents a device that receives coins to return to the customer.
	 */
	public final CoinTray coinTray;
	/**
	 * Represents the value used to configure the maximum capacity of the coin tray.
	 * This is an imperfect simulation, in that exceeding the capacity in the real
	 * world would cause coins to spill on the ground.
	 */
	public static final int COIN_TRAY_CAPACITY = 20;

	/**
	 * Creates a self-checkout station.
	 * 
	 * @param currency
	 *            The kind of currency permitted.
	 * @param banknoteDenominations
	 *            The set of denominations (i.e., $5, $10, etc.) to accept.
	 * @param coinDenominations
	 *            The set of denominations (i.e., $0.05, $0.10, etc.) to accept.
	 * @param scaleMaximumWeight
	 *            The most weight that can be placed on the scale before it
	 *            overloads.
	 * @param scaleSensitivity
	 *            Any weight changes smaller than this will not be detected or
	 *            announced.
	 * @throws SimulationException
	 *             If any argument is null or negative.
	 * @throws SimulationException
	 *             If the number of banknote or coin denominations is &lt;1.
	 */
	public SelfCheckoutStation(Currency currency, int[] banknoteDenominations, BigDecimal[] coinDenominations,
		int scaleMaximumWeight, int scaleSensitivity) {
		if(currency == null || banknoteDenominations == null || coinDenominations == null)
			throw new NullPointerSimulationException();

		if(scaleMaximumWeight <= 0)
			throw new InvalidArgumentSimulationException("The scale's maximum weight must be positive.");

		if(scaleSensitivity <= 0)
			throw new InvalidArgumentSimulationException("The scale's sensitivity must be positive.");

		if(banknoteDenominations.length == 0)
			throw new InvalidArgumentSimulationException(
				"There must be at least one allowable banknote denomination defined.");

		if(coinDenominations.length == 0)
			throw new InvalidArgumentSimulationException(
				"There must be at least one allowable coin denomination defined.");

		// Create the devices.
		baggingArea = new ElectronicScale(scaleMaximumWeight, scaleSensitivity);
		scanningArea = new ElectronicScale(scaleMaximumWeight / 10 + 1, scaleSensitivity);
		screen = new TouchScreen();
		printer = new ReceiptPrinter();
		cardReader = new CardReader();
		mainScanner = new BarcodeScanner();
		handheldScanner = new BarcodeScanner();

		this.banknoteDenominations = banknoteDenominations;
		banknoteInput = new BanknoteSlot(false);
		banknoteValidator = new BanknoteValidator(currency, banknoteDenominations);
		banknoteStorage = new BanknoteStorageUnit(BANKNOTE_STORAGE_CAPACITY);
		banknoteOutput = new BanknoteSlot(true);

		banknoteDispensers = new HashMap<>();

		for(int i = 0; i < banknoteDenominations.length; i++)
			banknoteDispensers.put(banknoteDenominations[i], new BanknoteDispenser(BANKNOTE_DISPENSER_CAPACITY));

		this.coinDenominations = Arrays.asList(coinDenominations);
		coinSlot = new CoinSlot();
		coinValidator = new CoinValidator(currency, this.coinDenominations);
		coinStorage = new CoinStorageUnit(COIN_STORAGE_CAPACITY);
		coinTray = new CoinTray(COIN_TRAY_CAPACITY);

		coinDispensers = new HashMap<>();

		for(int i = 0; i < coinDenominations.length; i++)
			coinDispensers.put(coinDenominations[i], new CoinDispenser(COIN_DISPENSER_CAPACITY));

		// Hook up everything.
		interconnect(banknoteInput, banknoteValidator);
		interconnect(banknoteValidator, banknoteStorage);

		for(BanknoteDispenser dispenser : banknoteDispensers.values())
			interconnect(dispenser, banknoteOutput);

		interconnect(coinSlot, coinValidator);
		interconnect(coinValidator, coinTray, coinDispensers, coinStorage);

		for(CoinDispenser coinDispenser : coinDispensers.values())
			interconnect(coinDispenser, coinTray);

		baggingArea.endConfigurationPhase();
		scanningArea.endConfigurationPhase();
		screen.endConfigurationPhase();
		printer.endConfigurationPhase();
		cardReader.endConfigurationPhase();
		mainScanner.endConfigurationPhase();
		handheldScanner.endConfigurationPhase();

		banknoteInput.endConfigurationPhase();
		banknoteValidator.endConfigurationPhase();
		banknoteStorage.endConfigurationPhase();
		banknoteOutput.endConfigurationPhase();

		for(BanknoteDispenser bd : banknoteDispensers.values())
			bd.endConfigurationPhase();

		coinSlot.endConfigurationPhase();
		coinValidator.endConfigurationPhase();
		// coinStorage.enable();
		coinStorage.endConfigurationPhase();
		coinTray.endConfigurationPhase();

		for(CoinDispenser cd : coinDispensers.values())
			cd.endConfigurationPhase();
	}

	private BidirectionalChannel<Banknote> validatorSource;

	private SupervisionStation supervisor = null;

	boolean isSupervised() {
		return supervisor != null;
	}

	void setSupervisor(SupervisionStation supervisor) {
		this.supervisor = supervisor;
	}

	private void interconnect(BanknoteSlot slot, BanknoteValidator validator) {
		validatorSource = new BidirectionalChannel<Banknote>(slot, validator);
		slot.connect(validatorSource);
	}

	private void interconnect(BanknoteValidator validator, BanknoteStorageUnit storage) {
		UnidirectionalChannel<Banknote> bc = new UnidirectionalChannel<Banknote>(storage);
		validator.connect(validatorSource, bc);
	}

	private void interconnect(BanknoteDispenser dispenser, BanknoteSlot slot) {
		UnidirectionalChannel<Banknote> bc = new UnidirectionalChannel<Banknote>(slot);
		dispenser.connect(bc);
	}

	private void interconnect(CoinSlot slot, CoinValidator validator) {
		UnidirectionalChannel<Coin> cc = new UnidirectionalChannel<Coin>(validator);
		slot.connect(cc);
	}

	private void interconnect(CoinValidator validator, CoinTray tray, Map<BigDecimal, CoinDispenser> dispensers,
		CoinStorageUnit storage) {
		UnidirectionalChannel<Coin> rejectChannel = new UnidirectionalChannel<Coin>(tray);
		Map<BigDecimal, UnidirectionalChannel<Coin>> dispenserChannels = new HashMap<BigDecimal, UnidirectionalChannel<Coin>>();

		for(BigDecimal denomination : dispensers.keySet()) {
			CoinDispenser dispenser = dispensers.get(denomination);
			dispenserChannels.put(denomination, new UnidirectionalChannel<Coin>(dispenser));
		}

		UnidirectionalChannel<Coin> overflowChannel = new UnidirectionalChannel<Coin>(storage);

		validator.connect(rejectChannel, dispenserChannels, overflowChannel);
	}

	private void interconnect(CoinDispenser dispenser, CoinTray tray) {
		UnidirectionalChannel<Coin> cc = new UnidirectionalChannel<Coin>(tray);
		dispenser.connect(cc);
	}
}
