package com.agentecon.finance;

import com.agentecon.good.IStock;
import com.agentecon.price.ExpSearchPrice;

public class MarketMakerPrice {

	public static final double MIN_SPREAD = 0.01;
	public static final double SPREAD_MULTIPLIER = 1.0 + MIN_SPREAD / 2;

	private FloorFactor floor;
	private CeilingFactor ceiling;

	public MarketMakerPrice(Position pos) {
		this.floor = new FloorFactor(pos, new ExpSearchPrice(0.1, 10.0 / SPREAD_MULTIPLIER));
		this.ceiling = new CeilingFactor(pos, new ExpSearchPrice(0.1, 10.0 * SPREAD_MULTIPLIER));
	}

	public void trade(IStockMarket dsm, IStock wallet, double budget) {
		double low = floor.getPrice();
		double high = ceiling.getPrice();
		double middle = (low + high) / 2;
		floor.adapt(middle / SPREAD_MULTIPLIER);
		ceiling.adapt(middle * SPREAD_MULTIPLIER);
		floor.createOffers(dsm, wallet, budget / floor.getPrice());
		ceiling.createOffers(dsm, wallet, ceiling.getStock().getAmount() / 10); // offer a tenth of the present shares
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

}
