// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.configurations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.agentecon.api.ISimulation;
import com.agentecon.api.Price;
import com.agentecon.finance.Ticker;
import com.agentecon.good.Good;
import com.agentecon.metric.IMarketListener;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;

public class StockMarketStats extends SimulationListenerAdapter implements IMarketListener {

	public static boolean PRINT_TICKER = true;

	private ISimulation world;
	private Good index = new Good("Index");
	private HashMap<Ticker, Average> averages;

	public StockMarketStats(ISimulation world) {
		this.world = world;
		this.averages = new InstantiatingHashMap<Ticker, Average>() {

			@Override
			protected Average create(Ticker key) {
				return new Average();
			}
		};
	}
	
	public double getPrice(Ticker ticker) {
		return averages.get(ticker).getAverage();
	}

	@Override
	public void notifyDayStarted(int day) {
		averages.clear();
	}

	@Override
	public void notifyOffered(Good good, double quantity, Price price) {
	}

	@Override
	public void notifySold(Good good, double quantity, Price price) {
		averages.get(good).add(quantity, price.getPrice());
	}

	@Override
	public void notifyTradesCancelled() {
		averages.clear();
	}

	private ArrayList<Good> toPrint = new ArrayList<>();

	@Override
	public void notifyDayEnded(int day) {
		Average indexPoints = new Average();
		HashMap<Good, Average> sectorIndices = new InstantiatingHashMap<Good, Average>() {

			@Override
			protected Average create(Good key) {
				return new Average();
			}
		};
		Average indexRatio = new Average();
		HashMap<Good, Average> sectorRatios = new InstantiatingHashMap<Good, Average>() {

			@Override
			protected Average create(Good key) {
				return new Average();
			}
		};
		for (Entry<Ticker, Average> e : averages.entrySet()) {
			Ticker firm = e.getKey();
			Good sector = new Good(firm.getType());
			Average avgPrice = e.getValue();
			indexPoints.add(avgPrice);
			sectorIndices.get(sector).add(avgPrice);

			double dividends = world.getListedCompany(firm).getShareRegister().getAverageDividend();
			if (dividends > 1) {
				double peratio = avgPrice.getAverage() / dividends;
				indexRatio.add(peratio);
				sectorRatios.get(sector).add(peratio);
			}
		}
		sectorIndices.put(index, indexPoints);
		HashMap<Good, Average> all = new HashMap<>();
		all.putAll(averages);
		all.putAll(sectorIndices);
		printTicker(all, day);
	}

	protected void printTicker(Map<Good, Average> map, int day) {
		if (PRINT_TICKER) {
			if (day == 1000) {
				toPrint.addAll(map.keySet());
				Collections.sort(toPrint);
				printLabels();
			} else if (day > 1000 && toPrint.size() < map.size()) {
				for (Good t : map.keySet()) {
					if (!toPrint.contains(t)) {
						toPrint.add(t);
					}
				}
				printLabels();
			}
			if (toPrint.size() > 0) {
				String line = Integer.toString(day);
				for (Good g : toPrint) {
					Average avg = map.get(g);
					if (avg == null) {
						line += "\t";
					} else {
						line += "\t" + avg.getAverage();
					}
				}
				System.out.println(line);
			}
		}
	}

	protected void printLabels() {
		String labels = "";
		for (Good g : toPrint) {
			labels += "\t" + g;
		}
		System.out.println(labels);
	}

	@Override
	public String toString() {
		return "Prices of " + averages.size() + " stocks";
	}

}
