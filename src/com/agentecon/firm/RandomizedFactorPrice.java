package com.agentecon.firm;

import java.util.Random;

import com.agentecon.price.AdaptablePrice;

public class RandomizedFactorPrice extends AdaptablePrice {
	
	private Random rand;
	private double factor;

	public RandomizedFactorPrice(Random rand, double factor) {
		this.rand = rand;
		this.factor = factor;
	}

	@Override
	protected double getFactor(boolean increase) {
		return factor * 2 * rand.nextDouble();
	}

}
