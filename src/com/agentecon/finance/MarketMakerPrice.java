package com.agentecon.finance;

import com.agentecon.good.IStock;
import com.agentecon.price.ExpSearchPrice;
import com.agentecon.stats.Numbers;

public class MarketMakerPrice {
	
	private static final double INITIAL_PRICE_BELIEF = 100;

	public static final double MIN_SPREAD = 0.01;
	public static final double SPREAD_MULTIPLIER = 1.0 + MIN_SPREAD / 2;

	private FloorFactor floor;
	private CeilingFactor ceiling;

	public MarketMakerPrice(Position pos) {
		this.floor = new FloorFactor(pos, new ExpSearchPrice(0.1, INITIAL_PRICE_BELIEF / SPREAD_MULTIPLIER){
			@Override
			protected double getMax(){
				return 0.1;
			}
		});
		this.ceiling = new CeilingFactor(pos, new ExpSearchPrice(0.1, INITIAL_PRICE_BELIEF * SPREAD_MULTIPLIER){
			@Override
			protected double getMax(){
				return 0.1;
			}
		});
	}

	public void trade(IStockMarket dsm, IStock wallet, double budget) {
		double low = floor.getPrice();
		double high = ceiling.getPrice();
		double middle = (low + high) / 2;
		if (Numbers.isBigger(budget, 0.0)) {
			floor.adapt(middle / SPREAD_MULTIPLIER);
			floor.createOffers(dsm, wallet, budget / floor.getPrice());
		}
		ceiling.adapt(middle * SPREAD_MULTIPLIER);
		ceiling.createOffers(dsm, wallet, ceiling.getStock().getAmount() * 0.06); // offer a fraction of the present shares
	}

	public double getPrice() {
		double p1 = floor.getPrice();
		double p2 = ceiling.getPrice();
		return (p1 + p2) / 2;
	}

	@Override
	public String toString() {
		return floor + " to " + ceiling;
	}

	public String getSpread() {
		double p1 = floor.getPrice();
		double p2 = ceiling.getPrice();
		return Double.toString((p2 - p1)/p2);
	}

}
