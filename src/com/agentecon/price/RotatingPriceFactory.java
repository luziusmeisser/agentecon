// Created on May 21, 2015 by Luzius Meisser

package com.agentecon.price;

import java.util.HashMap;
import java.util.Random;

import com.agentecon.good.Good;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.util.InstantiatingHashMap;

/**
 * Creates prices according to settings. A separate instance is required for each firm. PriceFactory must be carried over through generations for evolving firms and "createPrice" only called once per
 * generation for each good.
 */
public class RotatingPriceFactory extends SimulationListenerAdapter implements IPriceFactory {

	private double factor;

	private HashMap<Good, Rotation> rotatingPrices;

	public RotatingPriceFactory(Random rand) {
		this.factor = 0.05;
		this.rotatingPrices = new InstantiatingHashMap<Good, Rotation>() {
			
			private int inc = 1;

			@Override
			protected Rotation create(Good key) {
				return new Rotation(inc++);
			}
		};
	}

	public IPrice createPrice(Good good) {
		return rotatingPrices.get(good).addPrice(new ExpSearchPrice(factor));
	}
	
	@Override
	public void notifyDayEnded(int day, double utility) {
		for (Rotation r: rotatingPrices.values()){
			r.rotate();
		}
	}

}
