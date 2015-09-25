package com.agentecon.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import com.agentecon.api.IAgent;
import com.agentecon.consumer.Consumer;
import com.agentecon.finance.IPublicCompany;
import com.agentecon.finance.MarketMaker;
import com.agentecon.firm.Producer;
import com.agentecon.metric.ISimulationListener;

public class Agents implements IConsumers, IFirms, ITraders {

	private long seed;
	private Random rand;

	private ArrayList<IAgent> all;
	private ArrayList<IPublicCompany> publicCompanies;

	private ArrayList<Producer> firms;
	private ArrayList<Consumer> consumers;
	private ArrayList<Trader> traders;
	private ArrayList<MarketMaker> marketMakers;

	private ISimulationListener listeners;

	public Agents(ISimulationListener listeners, long seed) {
		this(listeners, seed, new ArrayList<Producer>(), new ArrayList<Consumer>(), new ArrayList<Trader>(), new ArrayList<MarketMaker>());
	}

	public Agents(ISimulationListener listeners, long seed, ArrayList<Producer> firms, ArrayList<Consumer> cons, ArrayList<Trader> trad, ArrayList<MarketMaker> mms) {
		this.publicCompanies = new ArrayList<>();
		this.all = new ArrayList<>();
		this.consumers = new ArrayList<>();
		for (Consumer con : cons) {
			Consumer clon = con.clone();
			this.consumers.add(clon);
			addAgent(clon);
		}
		this.firms = new ArrayList<>();
		for (Producer firm : firms) {
			Producer klon = firm.clone();
			this.firms.add(klon);
			addAgent(klon);
		}
		this.traders = new ArrayList<>();
		for (Trader t : trad) {
			Trader klon = (Trader) t.clone();
			traders.add(klon);
			addAgent(klon);
		}
		this.marketMakers = new ArrayList<>();
		for (MarketMaker mm : mms) {
			MarketMaker klon = (MarketMaker) mm.clone();
			marketMakers.add(klon);
			addAgent(klon);
		}
		this.listeners = listeners; // must be at the end to avoid unnecessary notifications
		this.seed = seed;
	}

	public Collection<Producer> getAllFirms() {
		return firms;
	}

	public Collection<Consumer> getAllConsumers() {
		return consumers;
	}

	public Collection<MarketMaker> getAllMarketMakers() {
		return marketMakers;
	}

	public void add(MarketMaker marketMaker) {
		marketMakers.add(marketMaker);
		addAgent(marketMaker);
	}

	@Override
	public void add(Producer firm) {
		firms.add(firm);
		addAgent(firm);
	}

	@Override
	public void add(Consumer consumer) {
		consumers.add(consumer);
		addAgent(consumer);
	}

	@Override
	public void addTrader(Trader t) {
		addAgent(t);
		traders.add(t);
	}

	private void addAgent(IAgent agent) {
		all.add(agent);
		if (agent instanceof IPublicCompany) {
			publicCompanies.add((IPublicCompany) agent);
		}
		if (listeners != null) {
			listeners.notifyAgentCreated(agent);
		}
	}

	public Collection<Consumer> getRandomConsumers() {
		return getRandomConsumers(-1);
	}

	public Collection<? extends IAgent> getAll() {
		return all;
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

	public Collection<Producer> getRandomFirms() {
		return getRandomFirms(-1);
	}

	public Collection<Producer> getRandomFirms(int cardinality) {
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

	public Collection<IPublicCompany> getPublicCompanies() {
		return publicCompanies;
	}

	public Agents duplicate() {
		preserveRand();
		assert rand == null;
		return new Agents(listeners, seed, firms, consumers, traders, marketMakers);
	}

	private void preserveRand() {
		if (rand != null) {
			seed = rand.nextLong();
			rand = null;
		}
	}

	@Override
	public String toString() {
		return consumers.size() + " consumers, " + firms.size() + " firms, " + traders.size() + " traders";
	}

}
