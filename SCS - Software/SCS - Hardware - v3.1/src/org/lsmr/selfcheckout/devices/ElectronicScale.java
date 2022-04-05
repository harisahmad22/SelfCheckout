package org.lsmr.selfcheckout.devices;

import java.util.ArrayList;
import java.util.Random;

import org.lsmr.selfcheckout.IllegalConfigurationPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalErrorPhaseSimulationException;
import org.lsmr.selfcheckout.IllegalPhaseSimulationException;
import org.lsmr.selfcheckout.InvalidArgumentSimulationException;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.NullPointerSimulationException;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

/**
 * Represents a device for weighing items. This device has a weight limit, so
 * place more weight than this leads to an overload condition, and an imperfect
 * sensitivity, so that small changes might not be noticed.
 */
public class ElectronicScale extends AbstractDevice<ElectronicScaleObserver> {
	private ArrayList<Item> items = new ArrayList<>();

	private double weightLimitInGrams;
	private double currentWeightInGrams = 0;
	private double weightAtLastEvent = 0;
	private double sensitivity;

	/**
	 * Constructs an electronic scale with the indicated maximum weight that it can
	 * handle before going into overload. The constructed scale will initially be in
	 * the configuration phase.
	 * 
	 * @param weightLimitInGrams
	 *            The weight threshold beyond which the scale will overload.
	 * @param sensitivity
	 *            The number of grams that can be added or removed since the last
	 *            change event, without causing a new change event.
	 * @throws SimulationException
	 *             If either argument is &le;0.
	 */
	public ElectronicScale(int weightLimitInGrams, int sensitivity) {
		if(weightLimitInGrams <= 0)
			throw new InvalidArgumentSimulationException("The maximum weight cannot be zero or less.");

		if(sensitivity <= 0)
			throw new InvalidArgumentSimulationException("The sensitivity cannot be zero or less.");

		this.weightLimitInGrams = weightLimitInGrams;
		this.sensitivity = sensitivity;
	}

	/**
	 * Gets the weight limit for the scale. Weights greater than this will not be
	 * weighable by the scale, but will cause overload.
	 * 
	 * @return The weight limit.
	 */
	public double getWeightLimit() {
		return weightLimitInGrams;
	}

	/**
	 * Gets the current weight on the scale.
	 * 
	 * @return The current weight.
	 * @throws SimulationException
	 *             If this operation is called during the configuration or error
	 *             phases.
	 * @throws OverloadException
	 *             If the weight has overloaded the scale.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public double getCurrentWeight() throws OverloadException {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(currentWeightInGrams <= weightLimitInGrams)
			return currentWeightInGrams + new Random().nextDouble() / 10.0;

		throw new OverloadException();
	}

	/**
	 * Gets the sensitivity of the scale. Changes smaller than this limit are not
	 * noticed or announced.
	 * 
	 * @return The sensitivity.
	 */
	public double getSensitivity() {
		return sensitivity;
	}

	/**
	 * Adds an item to the scale. If the addition is successful, a weight changed
	 * event is announced. If the weight is greater than the weight limit, announces
	 * "overload" event.
	 * 
	 * @param item
	 *            The item to add.
	 * @throws SimulationException
	 *             If the same item is added more than once or is null.
	 * @throws IllegalPhaseSimulationException
	 *             If the device is not in the normal phase.
	 */
	public void add(Item item) {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(item == null)
			throw new NullPointerSimulationException("item");

		if(items.contains(item))
			throw new InvalidArgumentSimulationException("The same item cannot be added more than once to the scale.");

		currentWeightInGrams += item.getWeight();

		items.add(item);

		if(currentWeightInGrams > weightLimitInGrams)
			notifyOverload();

		if(Math.abs(currentWeightInGrams - weightAtLastEvent) > sensitivity)
			notifyWeightChanged();
	}

	/**
	 * Removes an item from the scale. If the operation is successful, announces
	 * "weightChanged" event. If the scale was overloaded and this removal causes it
	 * to no longer be overloaded, announces "outOfOverload" event.
	 * 
	 * @param item
	 *            The item to remove.
	 * @throws SimulationException
	 *             If the item is not on the scale (including if it is null).
	 * @throws SimulationException
	 *             If the device is not in the normal phase.
	 */
	public void remove(Item item) {
		if(phase == Phase.ERROR)
			throw new IllegalErrorPhaseSimulationException();
		if(phase == Phase.CONFIGURATION)
			throw new IllegalConfigurationPhaseSimulationException();

		if(!items.remove(item))
			throw new InvalidArgumentSimulationException("The item was not found amongst those on the scale.");

		// To avoid drift in the sum due to round-off error, recalculate the weight.
		double newWeightInGrams = 0.0;
		for(Item itemOnScale : items)
			newWeightInGrams += itemOnScale.getWeight();

		double original = currentWeightInGrams;
		currentWeightInGrams = newWeightInGrams;

		if(original > weightLimitInGrams && newWeightInGrams <= weightLimitInGrams)
			notifyOutOfOverload();

		if(currentWeightInGrams <= weightLimitInGrams && Math.abs(original - currentWeightInGrams) > sensitivity)
			notifyWeightChanged();
	}

	private void notifyOverload() {
		for(ElectronicScaleObserver l : observers)
			l.overload(this);
	}

	private void notifyOutOfOverload() {
		weightAtLastEvent = currentWeightInGrams;

		for(ElectronicScaleObserver l : observers)
			l.outOfOverload(this);
	}

	private void notifyWeightChanged() {
		weightAtLastEvent = currentWeightInGrams;

		for(ElectronicScaleObserver l : observers)
			l.weightChanged(this, currentWeightInGrams);
	}
}
