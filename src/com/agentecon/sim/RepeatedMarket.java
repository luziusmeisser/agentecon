package com.agentecon.sim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.agentecon.api.Price;
import com.agentecon.consumer.Consumer;
import com.agentecon.firm.Firm;
import com.agentecon.good.Good;
import com.agentecon.market.Market;
import com.agentecon.metric.IMarketListener;
import com.agentecon.metric.SimulationListeners;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;
import com.agentecon.world.Trader;
import com.agentecon.world.World;

public class RepeatedMarket {

	private final World world;
	private final SimulationListeners listeners;

	public RepeatedMarket(World world, SimulationListeners listeners) {
		this.world = world;
		this.listeners = listeners; 
	}

	public void iterate(int day, int iterations) {
		MarketObserver observer = new MarketObserver(iterations);
		while (true) {
			world.startTransaction();
			Collection<Firm> firms = world.getFirms().getRandomFirms();
			Collection<Consumer> cons = world.getConsumers().getRandomConsumers();
			distributeDividends(day, firms, cons);
			listeners.notifyMarketClosed(null, false);
			Market market = new Market(world.getRand());
			market.addMarketListener(observer);
			listeners.notifyMarketOpened(market);
			for (Trader trader : world.getTraders().getAllTraders()) {
				trader.offer(market, day);
			}
			for (Firm firm : firms) {
				firm.offer(market);
			}
			for (Consumer c : cons) {
				c.maximizeUtility(market);
			}
			for (Firm firm: firms) {
				firm.adaptOutputPrice();
			}
			for (Firm firm: firms) {
				firm.adaptInputPrices();
			}
			if (observer.shouldTryAgain()){
				market.notifyCancelled();
				world.abortTransaction();
				listeners.notifyMarketClosed(market, false);
			} else {
				world.commitTransaction();
				listeners.notifyMarketClosed(market, true);
				break;
			}
		}
	}

	private void distributeDividends(int day, Collection<Firm> firms, Collection<Consumer> cons) {
		double dividends = 0.0;
		for (Firm firm : firms) {
			dividends += firm.payDividends(day);
		}
//		for (Trader trader : agents.getAllTraders()) {
//			if (trader instanceof VolumeTrader){
//				dividends = ((VolumeTrader)trader).refillWallet(dividends);
//			}
//			trader.notifyDayEnded(day);
//		}
		double perConsumer = dividends / cons.size();
		for (Consumer c : cons) {
			c.collectDividend(perConsumer);
			dividends -= perConsumer;
		}
		if (dividends != 0.0) {
			// ensure no money lost due to rounding
			cons.iterator().next().collectDividend(dividends);
		}
	}

	static class MarketObserver implements IMarketListener {
		
		static int count = 0;
		
		private int iters;
		private double sensitivity;
		private HashMap<Good, Average> current;
		private HashMap<Good, Average> prev;

		public MarketObserver(int maxIters) {
			this.iters = maxIters;
			this.sensitivity = 0.0001;
			next();
		}

		protected void next() {
			this.prev = current;
			this.current = new InstantiatingHashMap<Good, Average>() {

				@Override
				protected Average create(Good key) {
					return new Average();
				}
			};
		}

		@Override
		public void notifyOffered(Good good, double quantity, Price price) {
		}

		@Override
		public void notifySold(Good good, double quantity, Price price) {
			current.get(good).add(quantity, price.getPrice());
		}

		@Override
		public void notifyTradesCancelled() {
			current.clear();
		}
		
		public boolean shouldTryAgain() {
			if (iters-- <= 0){
				return false;
			} else if (prev == null){
				next();
				return true;
			} else {
				Average change = new Average();
				for (Map.Entry<Good, Average> e: current.entrySet()){
					double p1 = e.getValue().getAverage();
					double p2 = prev.get(e.getKey()).getAverage();
					double diff = Math.abs(p1 - p2) / p1;
					change.add(diff);
				}
				next();
//				sensitivity *= 1.3;
//				System.out.println(count++);
				return change.getAverage() > sensitivity;
			}
		}
		
	}
	
}
