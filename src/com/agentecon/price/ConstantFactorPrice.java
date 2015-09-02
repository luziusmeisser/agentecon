package com.agentecon.price;


public class ConstantFactorPrice extends AdaptablePrice {

	private double factor;
	
	public ConstantFactorPrice(double delta) {
		super();
		this.factor = 1.0 + delta;
	}
	
	@Override
	protected double getFactor(boolean increase) {
		return factor;
	}
	
}
