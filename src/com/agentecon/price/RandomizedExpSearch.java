package com.agentecon.price;

import java.util.Random;

import com.agentecon.stats.Numbers;

public class RandomizedExpSearch extends AdaptablePrice {

	public static final double MAX_ADAPTION_FACTOR = 0.5;
	public static final double MIN_ADAPTION_FACTOR = Numbers.EPSILON * 1000;

	private Random rand;
	private double speed;
	private double delta;
	private boolean direction;
	private int sameDirectionInARow;

	public RandomizedExpSearch(Random rand, double initialDelta) {
		this.delta = initialDelta;
		this.sameDirectionInARow = 0;
		this.rand = rand;
		this.speed = 1.1;
	}

	@Override
	protected double getFactor(boolean increase) {
		if (increase == direction) {
			sameDirectionInARow++;
			if (sameDirectionInARow > 1) {
				delta = Math.min(MAX_ADAPTION_FACTOR, delta * getRandomizedSpeed());
			}
		} else {
			sameDirectionInARow = 0;
			direction = increase;
			delta = Math.max(MIN_ADAPTION_FACTOR, delta / getRandomizedSpeed());
		}
		double f = 1.0 + delta;
		return f;
	}

	private double getRandomizedSpeed() {
		return rand.nextDouble() * speed * 2;
	}

	@Override
	public String toString() {
		return super.toString(); // + " at factor " + factor;
	}

}
