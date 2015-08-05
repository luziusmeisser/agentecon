package com.agentecon.price;

public class RotatingPrice implements IPrice {
	
	private Rotation rotation;
	private int pos;

	public RotatingPrice(Rotation rotation, int pos) {
		this.rotation = rotation;
		this.pos = pos;
	}
	
	private IPrice getCurrent(){
		return rotation.getPrice(pos);
	}

	@Override
	public double getPrice() {
		return getCurrent().getPrice();
	}

	@Override
	public void adapt(boolean increasePrice) {
		getCurrent().adapt(increasePrice);
	}

	@Override
	public boolean isProbablyUnobtainable() {
		return getCurrent().isProbablyUnobtainable();
	}

	@Override
	public IPrice clone() {
		try {
			return (IPrice) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

}
