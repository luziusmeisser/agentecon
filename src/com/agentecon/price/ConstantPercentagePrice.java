package com.agentecon.price;


public class ConstantPercentagePrice extends AdaptablePrice {

	private double delta;
	
	public ConstantPercentagePrice(double delta) {
		super();
		this.delta = delta;
	}
	
	@Override
	protected double getFactor(boolean increase) {
		return increase ? 1.0 + delta : 1/(1.0 - delta);
	}
	
}
