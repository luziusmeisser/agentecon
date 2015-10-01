package com.agentecon.finance;

import java.util.Collection;
import java.util.HashMap;

import com.agentecon.agent.Endowment;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.util.Average;

public class MarketMaker extends PublicCompany implements IShareholder, Cloneable {

	private static final int MARKET_MAKER_CASH = 1000;

	private double reserve;
	private Portfolio portfolio;
	private HashMap<Ticker, MarketMakerPrice> priceBeliefs;

	public MarketMaker(Collection<IPublicCompany> comps) {
		super("Market Maker", new Endowment(new IStock[] { new Stock(SimConfig.MONEY, MARKET_MAKER_CASH) }, new IStock[] {}));
		this.portfolio = new Portfolio(getMoney());
		this.reserve = 0.0;
		this.priceBeliefs = new HashMap<Ticker, MarketMakerPrice>();
		for (IPublicCompany pc : comps) {
			addPosition(pc.getShareRegister().createPosition());
		}
	}

	public void postOffers(IStockMarket dsm) {
		portfolio.collectDividends();
		IStock money = getMoney().hide(reserve);
		double budgetPerPosition = money.getAmount() / priceBeliefs.size();
		for (MarketMakerPrice e : priceBeliefs.values()) {
			e.trade(dsm, money, budgetPerPosition);
		}
	}

	public void addPosition(Position pos) {
		if (pos.getTicker().equals(getTicker())) {
			pos.dispose(); // do not trade own shares
		} else {
			portfolio.addPosition(pos);
			priceBeliefs.put(pos.getTicker(), new MarketMakerPrice(pos));
		}
	}

	public double getPrice(Good output) {
		return priceBeliefs.get(output).getPrice();
	}

	public Average getAvgHoldings() {
		Average avg = new Average();
		for (Ticker t : priceBeliefs.keySet()) {
			avg.add(portfolio.getPosition(t).getAmount());
		}
		return avg;
	}

	private Average getIndex() {
		Average avg = new Average();
		for (MarketMakerPrice mmp : priceBeliefs.values()) {
			avg.add(mmp.getPrice());
		}
		return avg;
	}

	@Override
	protected double calculateDividends(int day) {
		double excessCash = getMoney().getAmount() - MARKET_MAKER_CASH;
		if (excessCash > 0) {
			double dividend = excessCash / 7;
			this.reserve = excessCash - dividend;
			return dividend;
		} else {
			this.reserve = 0.0;
			return 0.0;
		}
		// return excessCash; // excessCash / 5 would lead to market makers eventually owning everything
	}

	@Override
	public MarketMaker clone() {
		return this; // TEMP todo
	}

	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}

	@Override
	public String toString() {
		return getMoney() + ", holding " + getAvgHoldings() + ", price index: " + getIndex() + ", dividend " + getShareRegister().getAverageDividend();
	}

}
