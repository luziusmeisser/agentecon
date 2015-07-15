package com.agentecon.price;

import com.agentecon.stats.Numbers;

public class ExpSearchPrice extends AdaptablePrice {

	public static final double ADAPTION_SPEED = 1.1;
	public static final double MAX_ADAPTION_FACTOR = 0.5;
	public static final double MIN_ADAPTION_FACTOR = Numbers.EPSILON * 1000;

	private double factor;
	private boolean direction;
	private int sameDirectionInARow;

	public ExpSearchPrice(double initialFactor) {
		this.factor = initialFactor;
		this.sameDirectionInARow = 0;
	}

	@Override
	protected double getFactor(boolean increase) {
		if (increase == direction) {
			sameDirectionInARow++;
			if (sameDirectionInARow >= 2) {
				factor = Math.min(MAX_ADAPTION_FACTOR, factor * ADAPTION_SPEED);
				sameDirectionInARow = 0;
			}
		} else {
			sameDirectionInARow = 0;
			direction = increase;
			factor = Math.max(MIN_ADAPTION_FACTOR, factor / ADAPTION_SPEED);
		}
		return factor;
	}
	
	public String toString() {
		return super.toString(); // + " at factor " + factor;
	}

}
