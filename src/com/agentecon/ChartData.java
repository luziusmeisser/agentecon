package com.agentecon;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.agentecon.api.IMarket;
import com.agentecon.api.Price;
import com.agentecon.good.Good;
import com.agentecon.metric.IMarketListener;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.util.AccumulatingAverage;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;
import com.agentecon.verification.Result;

public class ChartData extends SimulationListenerAdapter implements IMarketListener {

	private String table;
	private Good[] goods;
	private HashMap<Good, Average> prices;

	public ChartData(Good... goods) {
		this.table = "Day";
		this.goods = goods;
		for (Good g: goods){
			this.table += "\t" + g.getName();
		}
		this.prices = new InstantiatingHashMap<Good, Average>() {

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
		this.prices.get(good).add(quantity, price.getPrice());
	}

	@Override
	public void notifyTradesCancelled() {
		this.prices.clear();
	}

	@Override
	public void notifyMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}

	@Override
	public void notifyDayEnded(int day, double utility) {
		String line = Integer.toString(day);
		for (Good good: goods){
			line += "\t" + prices.get(good).getAverage();
		}
		table += "\n" + line;
	}

	public String getTable() {
		return table;
	}

}
