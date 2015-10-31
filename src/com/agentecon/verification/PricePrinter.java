package com.agentecon.verification;

import java.util.HashMap;
import java.util.Map.Entry;

import com.agentecon.api.IMarket;
import com.agentecon.api.Price;
import com.agentecon.good.Good;
import com.agentecon.metric.IMarketListener;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;

public class PricePrinter extends SimulationListenerAdapter implements IMarketListener {

	private HashMap<Good, Average> prices;

	private int startRecordingDate;
	private int endRecordingDate;

	public PricePrinter(int startRecordingDate) {
		this(startRecordingDate, Integer.MAX_VALUE);
	}

	public PricePrinter(int start, int end) {
		this.startRecordingDate = start;
		this.endRecordingDate = end;
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
	public void notifySold(Good good, double quantity, Price price, boolean uptick) {
		notifySold(good, quantity, price);
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
		this.prices.clear();
		market.addMarketListener(this);
	}

	@Override
	public void notifyDayEnded(int day, double utility) {
		if (day >= startRecordingDate && day < endRecordingDate) {
			String line = Integer.toString(day);
			for (Entry<Good, Average> e : prices.entrySet()) {
				Average avg = e.getValue();
				line += "\t" + e.getKey() + "\t" + avg.getAverage() + "\t" + avg.getTotWeight();
			}
			System.out.println(line);
		}
	}

}
