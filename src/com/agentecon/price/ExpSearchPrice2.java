package com.agentecon.price;

import com.agentecon.stats.Numbers;

public class ExpSearchPrice2 extends AdaptablePrice {

	public static final double MAX_ADAPTION_FACTOR = 0.5;
	public static final double MIN_ADAPTION_FACTOR = Numbers.EPSILON * 1000;

	private double speed;
	private double delta;
	private boolean increasetm2, increasetm1;

	public ExpSearchPrice2(double initialDelta, double initialPrice) {
		super(initialPrice);
		this.delta = initialDelta;
		this.increasetm1 = true;
		this.increasetm2 = true;
		this.speed = 1.1;
	}

	public ExpSearchPrice2(double initialDelta) {
		this.delta = initialDelta;
		this.increasetm1 = true;
		this.increasetm2 = true;
		this.speed = 1.1;
	}
	
	static int increase;
	static int decrease;
	static int total;

	@Override
	protected double getFactor(boolean increasetm0) {
		total++;
		if (increasetm0 == increasetm1 && increasetm1 == increasetm2){
			if (increasetm0) {
				increase++;
				delta = Math.min(MAX_ADAPTION_FACTOR, delta * speed);
			} else {
				decrease++;
				delta = Math.max(MIN_ADAPTION_FACTOR, delta / speed);
			}
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
