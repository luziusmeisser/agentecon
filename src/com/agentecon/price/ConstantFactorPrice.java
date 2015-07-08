package com.agentecon.price;


public class ConstantFactorPrice extends AdaptablePrice {

	private double factor;
	
	public ConstantFactorPrice(double factor) {
		super();
		this.factor = factor;
	}

	@Override
	protected double getFactor(boolean increase) {
		return factor;
	}
	
}
