// Created by Luzius on Apr 22, 2014

package com.agentecon.market;

import java.util.Collection;

import com.agentecon.good.Good;


public interface IPriceTakerMarket {
	
//	public double buy(Wallet wallet, Stock stock, double amount);
//	
//	public double sell(Wallet wallet, Stock stock, double amount);
//
//	/**
//	 * Convenience method to either buy (if amount positive) or sell (if amount negative)
//	 * at the current best market price
//	 */
//	public double trade(Wallet wallet, Stock stock, double amount);

	/**
	 * Convenience method for getPrices
	 */
	public Collection<IOffer> getBids();
	
	/**
	 * Convenience method for getPrices
	 */
	public Collection<IOffer> getAsks();
	
	public Collection<IOffer> getOffers(IPriceFilter bidAskFilter);

	public IOffer getOffer(Good good, boolean bid);
	
}
