package com.agentecon.verification;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.agentecon.api.IMarket;
import com.agentecon.api.Price;
import com.agentecon.good.Good;
import com.agentecon.metric.IMarketListener;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.util.InstantiatingHashMap;
import com.agentecon.util.WeightingMovingAverage;

public class PriceMetric extends SimulationListenerAdapter implements IMarketListener {

	private static final double MEMORY = 0.95;

	private HashMap<Good, WeightingMovingAverage> prices;
	private HashMap<Good, WeightingMovingAverage> volume;
	private ArrayList<WeightingMovingAverage> all;

	public PriceMetric() {
		this.all = new ArrayList<>();
		this.prices = new InstantiatingHashMap<Good, WeightingMovingAverage>() {

			@Override
			protected WeightingMovingAverage create(Good key) {
				WeightingMovingAverage wma = new WeightingMovingAverage(MEMORY);
				all.add(wma);
				return wma;
			}
		};
		this.volume = new InstantiatingHashMap<Good, WeightingMovingAverage>() {

			@Override
			protected WeightingMovingAverage create(Good key) {
				WeightingMovingAverage ama = new WeightingMovingAverage(MEMORY);
				all.add(ama);
				return ama;
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
		for (WeightingMovingAverage avg : all) {
			avg.reset();
		}
	}

	@Override
	public void notifyMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}

	public boolean isStable() {
		for (WeightingMovingAverage ma : all) {
			if (!ma.isStable()) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void notifyDayEnded(int day, double utility) {
		for (WeightingMovingAverage avg : all) {
			avg.flush();
		}
	}

	public void printResult(PrintStream ps) {
		for (Map.Entry<Good, WeightingMovingAverage> e : prices.entrySet()) {
			ps.println(e.getKey() + " price: " + e.getValue());
			if (volume.containsKey(e.getKey())) {
				ps.println(e.getKey() + " production: " + volume.get(e.getKey()));
			}
		}

		if (prices.containsKey(new Good("output 0"))) {
			WeightingMovingAverage pizzaPrice = prices.get(new Good("output 0"));
			double priceNormalization = pizzaPrice.getWrapped().getAverage();
			System.out.println("\nNormalized prices:");
			for (Map.Entry<Good, WeightingMovingAverage> e : prices.entrySet()) {
				ps.println(e.getKey() + " price: " + e.getValue().getWrapped().normalize(priceNormalization));
				if (volume.containsKey(e.getKey())) {
					ps.println(e.getKey() + " production: " + volume.get(e.getKey()).getWrapped().normalize(priceNormalization));
				}
			}
		}
	}

}
