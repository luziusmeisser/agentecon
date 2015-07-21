package com.agentecon.trader;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.api.Price;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.Market;
import com.agentecon.metric.IMarketListener;
import com.agentecon.util.Average;

public class Arbitrageur extends Agent implements IMarketListener {

	private Good good;
	private Average today;
	private PeakHistory prevLow, prevHigh;
	private PeakHistory low, high;
	
	public Arbitrageur(Endowment end, Good good) {
		super("Arbitrage Trader", end);
		this.good = good;
		this.low = new PeakHistory(false);
		this.high = new PeakHistory(true);
	}

	public Arbitrageur(Endowment end, Good good, PeakHistory low, PeakHistory high) {
		this(end, good);
		this.prevLow = low;
		this.prevHigh = high;
	}

	public void offer(Market market, int day) {
		today = new Average();
		market.addMarketListener(this);
		if (prevHigh != null && prevLow != null) {
			IStock money = getMoney();
			IStock stock = getStock(good);
			
			double bid = prevLow.findNextPeak(0) * 1.02;
			double ask = prevHigh.findNextPeak(0) / 1.02;
			double diff = ask - bid;
			if (diff > 0 && !money.isEmpty()){
				market.offer(new Bid(money, stock, new Price(good, bid), money.getAmount() / bid));
			}
			// asd
			if (!stock.isEmpty()) {
				market.offer(new Ask(money, stock, new Price(good, ask), stock.getAmount()));
			}
			
//			double max = prevHigh.findNextPeak(day);
//			double min = prevLow.findNextPeak(day);
//			double diff = max - min;
//			assert diff >= 0.0;
//			if (!money.isEmpty() && diff >= 4 * Numbers.EPSILON) {
//				double bid = min + diff / 4;
//				market.offer(new Bid(money, stock, new Price(good, bid), money.getAmount() / bid));
//			}
//			if (!stock.isEmpty()) {
//				double ask;
//				if (diff <= Numbers.EPSILON){
//					ask = max * 0.98;
//				} else {
//					ask = max - diff / 4;
//				}
//				market.offer(new Ask(money, stock, new Price(good, ask), stock.getAmount()));
//			}
		}
	}

	@Override
	public void notifyOffered(Good good, double quantity, Price price) {
	}

	@Override
	public void notifySold(Good good, double quantity, Price price) {
		if (good.equals(this.good)) {
			today.add(quantity, price.getPrice());
		}
	}

	public void notifyDayEnded(int day) {
		low.report(day, today.getAverage());
		high.report(day, today.getAverage());
	}

	public Arbitrageur createNextGeneration(Endowment end) {
		return new Arbitrageur(end, good, low, high);
	}

}
