package com.agentecon.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import com.agentecon.consumer.Consumer;
import com.agentecon.firm.Firm;
import com.agentecon.metric.ISimulationListener;

public class Agents implements IConsumers, IFirms, ITraders {

	private long seed;
	private Random rand;

	private ArrayList<Firm> firms;
	private ArrayList<Consumer> consumers;
	private ArrayList<Trader> traders;
	private ISimulationListener listeners;

	public Agents(ISimulationListener listeners, long seed) {
		this(listeners, seed, new ArrayList<Firm>(), new ArrayList<Consumer>(), new ArrayList<Trader>());
	}

	public Agents(ISimulationListener listeners, long seed, ArrayList<Firm> firms, ArrayList<Consumer> cons, ArrayList<Trader> trad) {
		this.consumers = new ArrayList<>();
		for (Consumer con: cons){
			this.consumers.add(con.clone());
		}
		this.firms = new ArrayList<>();
		for (Firm firm: firms){
			this.firms.add(firm.clone());
		}
		this.traders = new ArrayList<>();
		for (Trader t: trad){
			traders.add(t);
		}
		this.listeners = listeners;
		this.seed = seed;
	}

	public Collection<Firm> getAllFirms() {
		return firms;
	}

	public Collection<Consumer> getAllConsumers() {
		return consumers;
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

	@Override
	public void addTrader(Trader t) {
		traders.add(t);
	}

	public Collection<Consumer> getRandomConsumers() {
		return getRandomConsumers(-1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Consumer> getRandomConsumers(int cardinality) {
		Collections.shuffle(consumers, getRand()); // OPTIMIZABLE in case of cardinality < size
		if (cardinality == -1 || cardinality >= consumers.size()) {
			return (Collection<Consumer>) consumers.clone();
		} else {
			return consumers.subList(0, cardinality);
		}
	}

	public Collection<Trader> getAllTraders() {
		return traders;
	}

	public Collection<Firm> getRandomFirms() {
		return getRandomFirms(-1);
	}

	public Collection<Firm> getRandomFirms(int cardinality) {
		Collections.shuffle(firms, getRand());
		if (cardinality < 0 || cardinality >= firms.size()) {
			return firms;
		} else {
			return firms.subList(0, cardinality);
		}
	}

	@Override
	public Consumer getRandomConsumer() {
		return consumers.get(getRand().nextInt(consumers.size()));
	}

	public Collection<Trader> getRandomTraders(int cardinality) {
		Collections.shuffle(traders, getRand());
		if (cardinality < 0 || cardinality >= traders.size()) {
			return traders;
		} else {
			return traders.subList(0, cardinality);
		}
	}

	private Random getRand() {
		if (rand == null) {
			rand = new Random(seed);
		}
		return rand;
	}

	public void notifyDayStarted(long seed) {
		this.seed = seed;
		this.rand = null;
	}

	public Agents duplicate() {
		assert rand == null;
		return new Agents(listeners, seed, firms, consumers, traders);
	}
	
	@Override
	public String toString() {
		return consumers.size() + " consumers, " + firms.size() + " firms, " + traders.size() + " traders"; 
	}

}
