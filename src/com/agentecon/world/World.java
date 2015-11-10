package com.agentecon.world;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import com.agentecon.api.IAgent;
import com.agentecon.consumer.Consumer;
import com.agentecon.finance.Portfolio;
import com.agentecon.finance.Position;
import com.agentecon.firm.Producer;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.metric.ISimulationListener;
import com.agentecon.metric.SimulationListeners;
import com.agentecon.sim.config.SimConfig;

public class World implements IWorld {

	private int day;
	private Random rand;
	private Agents agents, backup;
	private long randomBaseSeed;
	private SimulationListeners listeners;

	public World(long randomSeed, SimulationListeners listeners) {
		this.listeners = listeners;
		this.randomBaseSeed = randomSeed + 123123453;
		this.rand = new Random(randomSeed);
		this.agents = new Agents(listeners, rand.nextLong());
	}

	@Override
	public IConsumers getConsumers() {
		return agents;
	}

	public void handoutEndowments() {
		for (Consumer c : agents.getAllConsumers()) {
			c.collectDailyEndowment();
		}
		for (Producer f : agents.getAllFirms()) {
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
		this.agents = this.agents.renew(rand.nextLong());
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
	public void addListener(ISimulationListener listener) {
		listeners.add(listener);
	}

	public void finishDay(int day) {
		IStock inheritedMoney = new Stock(SimConfig.MONEY);
		Portfolio inheritance = new Portfolio(inheritedMoney);
		Collection<Consumer> consumers = agents.getAllConsumers();
		Iterator<Consumer> iter = consumers.iterator();
		double util = 0.0;
		while (iter.hasNext()) {
			Consumer c = iter.next();
			assert c.isAlive();
			util += c.consume();
			c.age(inheritance);
		}
		for (Position pos: inheritance.getPositions()){
			agents.getCompany(pos.getTicker()).inherit(pos);
		}
		if (inheritedMoney.getAmount() > 0) {
			agents.getRandomConsumer().getMoney().absorb(inheritedMoney);
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

	public Agents getAgents() {
		return agents;
	}

	@Override
	public void add(IAgent agent) {
		agents.add(agent);
	}

}
