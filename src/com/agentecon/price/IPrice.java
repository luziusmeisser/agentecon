// Created by Luzius on Jun 22, 2014

package com.agentecon.price;

public interface IPrice extends Cloneable {

	public double getPrice();
	
	public void adapt(boolean increasePrice);
	
	public void adaptWithCeiling(boolean increasePrice, double max);
	
	public void adaptWithFloor(boolean increasePrice, double min);

	/**
	 * The price has reached its upper plausible limit. Probably there is none of that good in the market at all.
	 */
	public boolean isProbablyUnobtainable();
	
	public IPrice clone();

}
