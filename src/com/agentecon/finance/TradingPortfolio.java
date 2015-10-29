package com.agentecon.finance;

import java.util.ArrayList;
import java.util.Collections;

import com.agentecon.firm.production.IPriceProvider;
import com.agentecon.good.IStock;
import com.agentecon.stats.Numbers;

public class TradingPortfolio extends Portfolio {

	private static final boolean STICKY_STOCKS = true;

	public TradingPortfolio(IStock money) {
		super(money);
	}

	public double getCombinedValue(IPriceProvider prices, int timeHorizon) {
		return getSubstanceValue(prices) + getEarningsValue(timeHorizon);
	}

	public double getEarningsValue(int timeHorizon) {
		return getLatestDividendIncome() * timeHorizon;
	}

	public double getSubstanceValue(IPriceProvider prices) {
		double value = wallet.getAmount();
		for (Position p : inv.values()) {
			value += p.getAmount() * prices.getPrice(p.getTicker());
		}
		return value;
	}

	public void sell(IStockMarket stocks, double fraction) {
		for (Ticker ticker : new ArrayList<>(inv.keySet())) {
			Position pos = inv.get(ticker);
			stocks.sell(pos, wallet, pos.getAmount() * fraction);
			if (pos.isEmpty()) {
				disposePosition(ticker);
			}
		}
	}

	public void invest(IStockMarket stocks, double budget) {
		if (Numbers.isBigger(budget, 0.0)) {
			assert wallet.getAmount() >= budget;
			Ticker any = findStockToBuy(stocks);
			if (any != null) {
				double before = wallet.getAmount();
				Position pos = getPosition(any);
				addPosition(stocks.buy(any, pos, wallet, budget));
				double spent = before - wallet.getAmount();
				invest(stocks, budget - spent);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Ticker findStockToBuy(IStockMarket stocks) {
		return stocks.findAnyAsk(STICKY_STOCKS && inv.size() >= 7 ? new ArrayList<Ticker>(inv.keySet()) : Collections.EMPTY_LIST, false);
	}

	@Override
	public TradingPortfolio clone(IStock money) {
		return (TradingPortfolio) super.clone(money);
	}

}
