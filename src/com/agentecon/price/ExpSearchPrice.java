package com.agentecon.price;

import com.agentecon.stats.Numbers;

public class ExpSearchPrice extends AdaptablePrice {

	public static final double MAX_ADAPTION_FACTOR = 0.5;
	public static final double MIN_ADAPTION_FACTOR = Numbers.EPSILON * 10000;

	private double speed;
	private double delta;
	private boolean direction;
	private int sameDirectionInARow;

	public ExpSearchPrice(double initialDelta, double initialPrice) {
		super(initialPrice);
		this.delta = initialDelta;
		this.sameDirectionInARow = 0;
		this.speed = 1.1;
	}

	public ExpSearchPrice(double initialDelta) {
		this.delta = initialDelta;
		this.sameDirectionInARow = 0;
		this.speed = 1.1;
	}

	@Override
	protected double getFactor(boolean increase) {
		if (increase == direction) {
			sameDirectionInARow++;
			if (sameDirectionInARow > 0 && sameDirectionInARow % 2 == 0) {
				delta = Math.min(MAX_ADAPTION_FACTOR, delta * speed);
			}
		} else {
			sameDirectionInARow = 0;
			direction = increase;
			delta = Math.max(MIN_ADAPTION_FACTOR, delta / speed);
		}
		return 1.0 + delta;
	}

	@Override
	public String toString() {
		return super.toString(); // + " at factor " + factor;
	}

}
