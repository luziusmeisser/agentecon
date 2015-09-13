package com.agentecon.price;

import com.agentecon.stats.Numbers;

public class ExpSearchPrice extends AdaptablePrice {

	public static final double MAX_ADAPTION_FACTOR = 0.5;
	public static final double MIN_ADAPTION_FACTOR = Numbers.EPSILON * 1000;

	private double speed;
	private double delta, delta2;
	private boolean direction;
	private int sameDirectionInARow;
	private boolean increasetm2, increasetm1;

	public ExpSearchPrice(double initialDelta, double initialPrice) {
		super(initialPrice);
		this.delta = initialDelta;
		this.delta2 = delta;
		this.sameDirectionInARow = 0;
		this.increasetm2 = true;
		this.increasetm1 = false;
		this.speed = 1.1;
	}

	public ExpSearchPrice(double initialDelta) {
		this.delta = initialDelta;
		this.delta2 = delta;
		this.sameDirectionInARow = 0;
		this.increasetm2 = true;
		this.increasetm1 = false;
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
		double f = 1.0 + delta;
//		double f2 = getFactor2(increase);
//		assert f == f2;
		return f;
	}
	
//	protected double getFactor2(boolean increasetm0) {
//		if (increasetm0 == increasetm1 && increasetm1 == increasetm2) {
//			delta2 = Math.min(MAX_ADAPTION_FACTOR, delta2 * speed);
//		} else if (increasetm0 != increasetm1) {
//			delta2 = Math.max(MIN_ADAPTION_FACTOR, delta2 / speed);
//		}
//		increasetm2 = increasetm1;
//		increasetm1 = increasetm0;
//		return 1.0 + delta2;
//	}

	@Override
	public String toString() {
		return super.toString(); // + " at factor " + factor;
	}

}
