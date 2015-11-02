package com.agentecon.price;

import java.util.Random;

public class RandomizedFactorPrice extends AdaptablePrice {

	private Random rand;
	private double maxRandDelta;

	public RandomizedFactorPrice(Random rand, double delta) {
		this.rand = rand;
		this.maxRandDelta = 2 * delta; // so E[randomized delta] = delta
	}

	@Override
	protected double getFactor(boolean increase) {
		return 1.0 + maxRandDelta * rand.nextDouble();
	}

}
