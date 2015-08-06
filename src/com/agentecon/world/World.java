package com.agentecon.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import com.agentecon.consumer.Consumer;
import com.agentecon.firm.Firm;
import com.agentecon.good.Inventory;
import com.agentecon.metric.ISimulationListener;
import com.agentecon.metric.SimulationListeners;
import com.agentecon.sim.SimConfig;
import com.agentecon.trader.VolumeTrader;

public class World implements IWorld {

	private int day;
	private Random rand;
	private Agents agents, backup;
	private long randomBaseSeed;
	private SimulationListeners listeners;
	
	public World(long randomSeed, SimulationListeners listeners){
		this.randomBaseSeed = randomSeed + 123123453;
		this.rand = new Random(randomSeed);
		this.agents = new Agents(listeners, rand.nextLong());
		this.listeners = listeners;
	}
	
	@Override
	public IConsumers getConsumers() {
		return agents;
	}
	
	public void notifyConsumerDied(Consumer c){
		listeners.notfiyConsumerDied(c);
	}
	
	public void handoutEndowments() {
		for (Consumer c: agents.getAllConsumers()){
			c.collectDailyEndowment();
		}
		for (Firm f: agents.getAllFirms()){
			f.collectDailyEndowment();
		}
	}

	@Override
	public IFirms getFirms() {
		return agents;
	}
	
	public void prepareDay(int day) {
		this.day = day;
		// reset random every day to get more consistent results on small changes
		this.rand = new Random(day ^ randomBaseSeed);
		this.agents.notifyDayStarted(rand.nextLong());
		this.handoutEndowments();
		this.listeners.notifyDayStarted(day);
	}

	@Override
	public Random getRand() {
		return rand;
	}

	@Override
	public int getDay() {
		return day;
	}

	@Override
	public ITraders getTraders() {
		return agents;
	}

	@Override
	public void addListener(ISimulationListener listener) {
		listeners.add(listener);
	}

	public void finishDay(int day) {
		double inheritance = 0.0;
		Collection<Consumer> consumers = agents.getAllConsumers();
		Iterator<Consumer> iter = consumers.iterator();
		double util = 0.0;
		while (iter.hasNext()) {
			Consumer c = iter.next();
			util += c.consume();
			if (c.age()) {
				iter.remove();
				Inventory inv = c.notifyDied();
				inheritance += inv.getStock(SimConfig.MONEY).consume();
				notifyConsumerDied(c);
			}
		}
		
		double dividends = inheritance;
		for (Firm firm : agents.getAllFirms()) {
			firm.produce(day);
			dividends += firm.payDividends(day);
		}
		
		for (Trader trader : agents.getAllTraders()) {
			if (trader instanceof VolumeTrader){
				dividends = ((VolumeTrader)trader).refillWallet(dividends);
			}
			trader.notifyDayEnded(day);
		}
		
		distributeDividends(dividends, consumers);
		listeners.notifyDayEnded(day, util / consumers.size());
	}
	
	private void distributeDividends(double total, Collection<Consumer> allConsumers) {
		double perConsumer = total / allConsumers.size();
		for (Consumer c : allConsumers) {
			c.collectDividend(perConsumer);
			total -= perConsumer;
		}
		if (total != 0.0) {
			// ensure no money lost due to rounding
			allConsumers.iterator().next().collectDividend(total);
		}
	}

	public void startTransaction() {
		this.backup = agents.duplicate();
	}
	
	public void commitTransaction() {
		this.backup = null;
	}

	public void abortTransaction() {
		this.agents = backup;
	}

}
