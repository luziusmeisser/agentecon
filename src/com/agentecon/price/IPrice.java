// Created by Luzius on Jun 22, 2014

package com.agentecon.price;

public interface IPrice extends Cloneable {

	public double getPrice();
	
	public void adapt(boolean increasePrice);

	/**
	 * The price has reached its upper plausible limit. Probably there is none of that good in the market at all.
	 */
	public boolean isProbablyUnobtainable();
	
	public IPrice clone();

}
