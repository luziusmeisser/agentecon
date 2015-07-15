package com.agentecon.price;

import java.util.Random;

import com.agentecon.stats.Numbers;

public class ExpSearchPrice extends AdaptablePrice {

	public static final double MAX_ADAPTION_FACTOR = 0.5;
	public static final double MIN_ADAPTION_FACTOR = Numbers.EPSILON * 1000;

	private double factor;
	private boolean direction;
	private double adaptionSpeed;
	private int sameDirectionInARow;

	public ExpSearchPrice(double initialFactor, Random rand) {
		this.factor = initialFactor;
		this.adaptionSpeed = rand.nextDouble() + 1.5;
		this.sameDirectionInARow = 0;
	}

	@Override
	protected double getFactor(boolean increase) {
		if (increase == direction) {
			sameDirectionInARow++;
			if (sameDirectionInARow >= 2) {
				factor = Math.min(MAX_ADAPTION_FACTOR, factor * adaptionSpeed);
				sameDirectionInARow = 0;
			}
		} else {
			sameDirectionInARow = 0;
			direction = increase;
			factor = Math.max(MIN_ADAPTION_FACTOR, factor / adaptionSpeed);
		}
		return factor;
	}
	
	public String toString() {
		return super.toString(); // + " at factor " + factor;
	}

}
