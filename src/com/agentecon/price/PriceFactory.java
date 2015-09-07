// Created on May 21, 2015 by Luzius Meisser

package com.agentecon.price;

import java.util.HashMap;
import java.util.Random;

import com.agentecon.good.Good;

/**
 * Creates prices according to settings. A separate instance is required for each firm. PriceFactory must be carried over through generations for evolving firms and "createPrice" only called once per
 * generation for each good.
 */
public class PriceFactory implements IPriceFactory {

	public static Good NORMALIZED_GOOD = null;

	private PriceConfig type;
	private Random rand;

	private HashMap<Good, IEvolvable> evolvablePrices;

	public PriceFactory(Random rand, PriceConfig config) {
		this.type = config;
		this.rand = rand;
		this.evolvablePrices = new HashMap<>();
	}

	public IPrice createPrice(Good good) {
		if (NORMALIZED_GOOD != null && good.equals(NORMALIZED_GOOD)) {
			return new HardcodedPrice(10.0);
		} else if (evolvablePrices.containsKey(good)) {
			IEvolvable ev = evolvablePrices.get(good).createNextGeneration();
			evolvablePrices.put(good, ev);
			return (IPrice) ev;
		} else {
			IPrice price = instantiatePrice(good);
			if (price instanceof IEvolvable) {
				evolvablePrices.put(good, (IEvolvable) price);
			}
			return price;
		}
	}

	private IPrice instantiatePrice(Good good) {
		return type.createPrice(rand);
	}

}
