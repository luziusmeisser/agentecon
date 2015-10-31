package com.agentecon.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import com.agentecon.agent.Agent;
import com.agentecon.api.IAgent;
import com.agentecon.consumer.Consumer;
import com.agentecon.finance.Fundamentalist;
import com.agentecon.finance.IPublicCompany;
import com.agentecon.finance.IShareholder;
import com.agentecon.finance.IStockMarketParticipant;
import com.agentecon.finance.MarketMaker;
import com.agentecon.finance.Ticker;
import com.agentecon.firm.Producer;
import com.agentecon.metric.ISimulationListener;

public class Agents implements IConsumers, IFirms {

	private long seed;
	private Random rand;

	private ArrayList<IAgent> all;
	private HashMap<Ticker, IPublicCompany> publicCompanies;

	private ArrayList<Producer> firms;
	private ArrayList<Consumer> consumers;
	private ArrayList<Fundamentalist> fundies;
	private ArrayList<MarketMaker> marketMakers;
	private ArrayList<IShareholder> shareholders;

	private ISimulationListener listeners;

	public Agents(ISimulationListener listeners, long seed) {
		this(listeners, seed, new ArrayList<IAgent>());
	}

	public Agents(ISimulationListener listeners, long seed, ArrayList<IAgent> all) {
		this.publicCompanies = new HashMap<>();
		this.all = new ArrayList<>();
		this.consumers = new ArrayList<>();
		this.shareholders = new ArrayList<>();
		this.firms = new ArrayList<>();
		this.marketMakers = new ArrayList<>();
		this.fundies = new ArrayList<>();
		for (IAgent a : all) {
			if (a.isAlive()) {
				add(a);
			} else {
				listeners.notifyAgentDied(a);
			}
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

	public Collection<IStockMarketParticipant> getRandomStockMarketParticipants() {
		ArrayList<IStockMarketParticipant> list = new ArrayList<>();
		list.addAll(consumers);
		list.addAll(fundies);
		Collections.shuffle(list, getRand()); // OPTIMIZABLE in case of cardinality < size
		return list;
	}

	public Collection<MarketMaker> getRandomizedMarketMakers() {
		Collections.shuffle(marketMakers, getRand());
		return marketMakers;
	}

	public IPublicCompany getCompany(Ticker ticker) {
		return publicCompanies.get(ticker);
	}

	public void add(IAgent agent) {
		all.add(agent);
		if (agent instanceof IPublicCompany) {
			IPublicCompany pc = (IPublicCompany) agent;
			publicCompanies.put(pc.getTicker(), pc);
		}
		if (agent instanceof IShareholder) {
			shareholders.add((IShareholder) agent);
		}
		if (agent instanceof Fundamentalist) {
			fundies.add((Fundamentalist) agent);
		}
		if (agent instanceof MarketMaker) {
			marketMakers.add((MarketMaker) agent);
		}
		if (agent instanceof Producer) {
			firms.add((Producer) agent);
		}
		if (agent instanceof Consumer) {
			consumers.add((Consumer) agent);
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

	private Random getRand() {
		if (rand == null) {
			rand = new Random(seed);
		}
		return rand;
	}

	public Collection<IPublicCompany> getPublicCompanies() {
		return publicCompanies.values();
	}

	public Collection<? extends IShareholder> getShareHolders() {
		return shareholders;
	}
	
	public Agents renew(long seed){
		return new Agents(listeners, seed, all);
	}

	public Agents duplicate() {
		preserveRand();
		assert rand == null;
		ArrayList<IAgent> allDup = new ArrayList<>(all.size());
		for (IAgent a: all){
			allDup.add(a.clone());
		}
		return new Agents(listeners, seed, allDup);
	}

	private void preserveRand() {
		if (rand != null) {
			seed = rand.nextLong();
			rand = null;
		}
	}

	@Override
	public String toString() {
		return consumers.size() + " consumers, " + firms.size() + " firms";
	}

	public void refreshReferences() {
		for (IAgent a: all){
			((Agent)a).refreshRef();
		}
	}

	public Collection<MarketMaker> getAllMarketMakers() {
		return marketMakers;
	}

}
