package com.agentecon.price;

import java.util.Random;

import com.agentecon.stats.Numbers;

public class ExpSearchPrice extends AdaptablePrice {

	public static final double ADAPTION_SPEED = 1.1;
	public static final double MAX_ADAPTION_FACTOR = 0.5;
	public static final double MIN_ADAPTION_FACTOR = Numbers.EPSILON * 1000;

	private double speed;
	private double factor;
	private boolean direction;
	private int sameDirectionInARow;

	public ExpSearchPrice(Random rand, double initialFactor) {
		this.factor = initialFactor;
		this.sameDirectionInARow = 0;
		this.speed = 1.33; //01 + rand.nextDouble() / 3;
	}

	@Override
	protected double getFactor(boolean increase) {
		if (increase == direction) {
			sameDirectionInARow++;
			if (sameDirectionInARow >= 2) {
				factor = Math.min(MAX_ADAPTION_FACTOR, factor * speed);
				sameDirectionInARow = 0;
			}
		} else {
			sameDirectionInARow = 0;
			direction = increase;
			factor = Math.max(MIN_ADAPTION_FACTOR, factor / speed);
		}
		return factor;
	}

	public String toString() {
		return super.toString(); // + " at factor " + factor;
	}

}
