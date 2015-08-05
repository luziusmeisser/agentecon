package com.agentecon.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import com.agentecon.consumer.Consumer;
import com.agentecon.firm.Firm;
import com.agentecon.metric.ISimulationListener;
import com.agentecon.metric.SimulationListeners;

public class World implements IWorld, IConsumers, IFirms, ITraders {

	private int day;
	private Random rand;
	private long randomBaseSeed;
	private ArrayList<Firm> firms;
	private ArrayList<Consumer> consumers;
	private SimulationListeners listeners;
	private ArrayList<Trader> traders;
	
	public World(long randomSeed, SimulationListeners listeners){
		this.rand = new Random(randomSeed);
		this.randomBaseSeed = randomSeed;
		this.consumers = new ArrayList<>();
		this.firms = new ArrayList<>();
		this.traders = new ArrayList<>();
		this.listeners = listeners;
	}
	
	@Override
	public IConsumers getConsumers() {
		return this;
	}
	
	@Override
	public void add(Firm firm) {
		firms.add(firm);
		listeners.notifyFirmCreated(firm);
	}

	@Override
	public void add(Consumer consumer) {
		consumers.add(consumer);
		listeners.notifyConsumerCreated(consumer);
	}
	
	public void notifyConsumerDied(Consumer c){
		listeners.notfiyConsumerDied(c);
	}
	
	public Collection<Consumer> getRandomConsumers() {
		return getRandomConsumers(-1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Consumer> getRandomConsumers(int cardinality) {
		Collections.shuffle(consumers, getRand()); // OPTIMIZABLE in case of cardinality < size
		if (cardinality == -1 || cardinality >= consumers.size()){
			return (Collection<Consumer>) consumers.clone();
		} else {
			return consumers.subList(0, cardinality);
		}
	}

	public Collection<Firm> getRandomFirms() {
		return getRandomFirms(-1);
	}
	
	public Collection<Firm> getRandomFirms(int cardinality) {
		Collections.shuffle(firms, getRand());
		if (cardinality < 0 || cardinality >= firms.size()){
			return firms;
		} else {
			return firms.subList(0, cardinality);
		}
	}

	public void handoutEndowments() {
		for (Consumer c: consumers){
			c.collectDailyEndowment();
		}
		for (Firm f: firms){
			f.collectDailyEndowment();
		}
	}

	public Collection<Firm> getAllFirms() {
		return firms;
	}
	
	public Collection<Consumer> getAllConsumers() {
		return consumers;
	}

	@Override
	public IFirms getFirms() {
		return this;
	}
	
	public void notifyDayStarted(int day) {
		this.day = day;
		// reset random every day to get more consistent results on small changes
		this.rand = new Random(day ^ randomBaseSeed);
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
	public Consumer getRandomConsumer() {
		return consumers.get(rand.nextInt(consumers.size()));
	}

	public Collection<Trader> getAllTraders() {
		return getRandomTraders(-1);
	}
	
	public Collection<Trader> getRandomTraders(int cardinality) {
		Collections.shuffle(traders, getRand());
		if (cardinality < 0 || cardinality >= traders.size()){
			return traders;
		} else {
			return traders.subList(0, cardinality);
		}
	}

	@Override
	public ITraders getTraders() {
		return this;
	}

	@Override
	public void addTrader(Trader t) {
		traders.add(t);
	}

	@Override
	public void addListener(ISimulationListener listener) {
		listeners.add(listener);
	}

}
