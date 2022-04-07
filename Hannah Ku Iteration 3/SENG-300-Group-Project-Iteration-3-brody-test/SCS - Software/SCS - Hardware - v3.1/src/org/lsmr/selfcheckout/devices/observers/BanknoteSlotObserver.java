package org.lsmr.selfcheckout.devices.observers;

import org.lsmr.selfcheckout.devices.BanknoteSlot;

/**
 * Observes events emanating from a banknote slot.
 */
public interface BanknoteSlotObserver extends AbstractDeviceObserver {
	/**
	 * An event announcing that a banknote has been inserted.
	 * 
	 * @param slot
	 *            The device on which the event occurred.
	 */
	void banknoteInserted(BanknoteSlot slot);

	/**
	 * An event announcing that one or more banknotes have been returned to the
	 * user, dangling from the slot.
	 * 
	 * @param slot
	 *            The device on which the event occurred.
	 */
	void banknotesEjected(BanknoteSlot slot);

	/**
	 * An event announcing that a dangling banknote has been removed by the user.
	 * 
	 * @param slot
	 *            The device on which the event occurred.
	 */
	void banknoteRemoved(BanknoteSlot slot);
}
