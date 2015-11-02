package com.agentecon.price;


public class ConstantPrecentagePrice extends AdaptablePrice {

	private double delta;
	
	public ConstantPrecentagePrice(double delta) {
		super();
		this.delta = delta;
	}
	
	@Override
	protected double getFactor(boolean increase) {
		return increase ? 1.0 + delta : 1/(1.0 - delta);
	}
	
}
