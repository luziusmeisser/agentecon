package com.agentecon.world;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import com.agentecon.consumer.Consumer;
import com.agentecon.firm.Firm;
import com.agentecon.good.Inventory;
import com.agentecon.metric.ISimulationListener;
import com.agentecon.metric.SimulationListeners;
import com.agentecon.sim.SimConfig;

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
	public Agents getFirms() {
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
	public Agents getTraders() {
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
		if (inheritance > 0){
			consumers.iterator().next().getMoney().add(inheritance);
		}
		
		listeners.notifyDayEnded(day, util / consumers.size());
	}
	
	public void startTransaction() {
		this.backup = agents.duplicate();
	}
	
	public void commitTransaction() {
		this.backup = null;
	}

	public void abortTransaction() {
		assert backup != null;
		this.agents = backup;
	}

}
