package com.agentecon.price;

import com.agentecon.stats.Numbers;
import com.agentecon.util.MovingAverage;

public class ExpSearchPrice2 extends AdaptablePrice {

	public static final double MEMORY = 0.5;
	public static final double THRESHOLD = 0.2;
	
	public static final double MAX_ADAPTION_FACTOR = 0.5;
	public static final double MIN_ADAPTION_FACTOR = Numbers.EPSILON * 1000;

	private double speed;
	private double delta;
	private boolean prevIncrease;
	private MovingAverage avg;

	public ExpSearchPrice2(double initialDelta, double initialPrice) {
		super(initialPrice);
		this.delta = initialDelta;
		this.speed = 1.1;
		this.prevIncrease = false;
		this.avg = new MovingAverage(0.5);
	}

	public ExpSearchPrice2(double initialDelta) {
		this.delta = initialDelta;
		this.speed = 1.1;
		this.prevIncrease = false;
		this.avg = new MovingAverage(0.5);
	}

	@Override
	protected double getFactor(boolean increase) {
		boolean same = prevIncrease == increase;
		avg.add(same ? 1.0 : 0.0);
		if (avg.getAverage() > 0.8){
			delta = Math.min(MAX_ADAPTION_FACTOR, delta * speed);
		} else if (avg.getAverage() < 0.2){
			delta = Math.max(MIN_ADAPTION_FACTOR, delta / speed);
		}
		prevIncrease = increase;
		return 1.0 + delta;
	}

	@Override
	public String toString() {
		return super.toString(); // + " at factor " + factor;
	}

}
