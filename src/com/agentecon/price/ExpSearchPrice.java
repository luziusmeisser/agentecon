package com.agentecon.price;

import com.agentecon.stats.Numbers;

public class ExpSearchPrice extends AdaptablePrice {

	public static final double ADAPTION_SPEED = 1.1;
	public static final double MAX_ADAPTION_FACTOR = 0.5;
	public static final double MIN_ADAPTION_FACTOR = Numbers.EPSILON * 1000;

	private double speed;
	private double factor;
	private boolean direction;
	private int sameDirectionInARow;

	public ExpSearchPrice(double initialFactor, double initialPrice) {
		super(initialPrice);
		this.factor = initialFactor;
		this.sameDirectionInARow = 0;
		this.speed = 1.1;
	}

	public ExpSearchPrice(double initialFactor) {
		this.factor = initialFactor;
		this.sameDirectionInARow = 0;
		this.speed = 1.1;
	}

	@Override
	public boolean isStable() {
		return (sameDirectionInARow == 0 && factor < 10 * MIN_ADAPTION_FACTOR) || isProbablyUnobtainable();
	}

	@Override
	protected double getFactor(boolean increase) {
		if (increase == direction) {
			sameDirectionInARow++;
			if (sameDirectionInARow > 0 && sameDirectionInARow % 2 == 0) {
				factor = Math.min(MAX_ADAPTION_FACTOR, factor * speed);
			}
		} else {
			sameDirectionInARow = 0;
			direction = increase;
			factor = Math.max(MIN_ADAPTION_FACTOR, factor / speed);
		}
		return factor;
	}

	@Override
	public String toString() {
		return super.toString(); // + " at factor " + factor;
	}

}
