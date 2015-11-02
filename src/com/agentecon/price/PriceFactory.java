// Created on May 21, 2015 by Luzius Meisser

package com.agentecon.price;

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

	public PriceFactory(Random rand, PriceConfig config) {
		this.type = config;
		this.rand = rand;
	}

	public IPrice createPrice(Good good) {
		IPrice price = instantiatePrice(good);
		return price;
	}

	private IPrice instantiatePrice(Good good) {
		return type.createPrice(rand);
	}

}
