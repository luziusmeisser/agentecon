package com.agentecon.sim;

import java.util.Collection;

import com.agentecon.api.Price;
import com.agentecon.consumer.Consumer;
import com.agentecon.firm.Firm;
import com.agentecon.good.Good;
import com.agentecon.market.Market;
import com.agentecon.metric.IMarketListener;
import com.agentecon.metric.SimulationListeners;
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
		while (true) {
			world.startTransaction();
			Market market = new Market(world.getRand());
			final boolean[] trade = new boolean[]{false};
			market.addMarketListener(new IMarketListener() {
				
				@Override
				public void notifyTradesCancelled() {
				}
				
				@Override
				public void notifySold(Good good, double quantity, Price price) {
					trade[0] = true;
				}
				
				@Override
				public void notifyOffered(Good good, double quantity, Price price) {
				}
			});
			listeners.notifyMarketOpened(market);
			for (Trader trader : world.getTraders().getAllTraders()) {
				trader.offer(market, day);
			}
			Collection<Firm> firms = world.getFirms().getRandomFirms();
			for (Firm firm : firms) {
				firm.offer(market);
			}
			// System.out.println("Before open: " + market);
			for (Consumer c : world.getConsumers().getRandomConsumers()) {
				c.maximizeUtility(market);
			}
			for (Firm firm: firms) {
				firm.adaptPrices();
			}
//			if (trade[0] && shouldRetry(firms)){
//				market.notifyCancelled();
//				world.abortTransaction();
//			} else {
				world.commitTransaction();
				break;
//			}
		}
	}

	private boolean shouldRetry(Collection<Firm> firms) {
		int stable = 0;
		Firm f = firms.iterator().next();
		System.out.println(f.getOutputPrice());
		for (Firm firm: firms){
			if (firm.arePricesStable()){
				stable++;
			}
		}
		return stable < firms.size();
	}

}
