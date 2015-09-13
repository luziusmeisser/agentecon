package com.agentecon.price;

import com.agentecon.stats.Numbers;

public class ExpSearchPrice extends AdaptablePrice {

	public static final double MAX_ADAPTION_FACTOR = 0.5;
	public static final double MIN_ADAPTION_FACTOR = Numbers.EPSILON * 1000;

	private double speed;
	private double delta;
	private boolean increasetm2, increasetm1;

	public ExpSearchPrice(double initialDelta, double initialPrice) {
		super(initialPrice);
		this.delta = initialDelta;
		this.increasetm1 = true;
		this.increasetm2 = true;
		this.speed = 1.1;
	}

	public ExpSearchPrice(double initialDelta) {
		this.delta = initialDelta;
		this.increasetm1 = true;
		this.increasetm2 = true;
		this.speed = 1.1;
	}

	@Override
	protected double getFactor(boolean increasetm0) {
		if (increasetm0 == increasetm1 && increasetm1 == increasetm2) {
			delta = Math.min(MAX_ADAPTION_FACTOR, delta * speed);
		} else if (increasetm0 != increasetm1) {
			delta = Math.max(MIN_ADAPTION_FACTOR, delta / speed);
		}
		increasetm2 = increasetm1;
		increasetm1 = increasetm0;
		return 1.0 + delta;
	}

	@Override
	public String toString() {
		return super.toString(); // + " at factor " + factor;
	}

}
