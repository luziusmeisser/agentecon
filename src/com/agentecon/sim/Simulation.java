// Created by Luzius on Apr 22, 2014

package com.agentecon.sim;

import java.util.Collection;
import java.util.Queue;

import com.agentecon.api.IAgent;
import com.agentecon.api.IConsumer;
import com.agentecon.api.IFirm;
import com.agentecon.api.IIteratedSimulation;
import com.agentecon.api.IMarket;
import com.agentecon.api.ISimulation;
import com.agentecon.api.SimulationConfig;
import com.agentecon.events.SimEvent;
import com.agentecon.finance.IPublicCompany;
import com.agentecon.finance.IShareholder;
import com.agentecon.finance.StockMarket;
import com.agentecon.finance.Ticker;
import com.agentecon.firm.Producer;
import com.agentecon.government.Government;
import com.agentecon.metric.ISimulationListener;
import com.agentecon.metric.SimulationListeners;
import com.agentecon.sim.config.IConfiguration;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.verification.ExplorationScenario;
import com.agentecon.world.World;

// The world
public class Simulation implements ISimulation, IIteratedSimulation {

	private IConfiguration metaConfig;

	private SimConfig config;

	private int day;
	private Queue<SimEvent> events;
	private SimulationListeners listeners;
	private World world;
	private StockMarket stocks;

	static {
		// Disabled because too slow on app engine
		// Simulation.class.getClassLoader().setDefaultAssertionStatus(true);
	}

//	public Simulation() {
//		this(new IncreasingWiggle() ); 
//	}
	
	public Simulation() {
		this(new ExplorationScenario());
	}

	public Simulation(IConfiguration metaConfig) {
		this(metaConfig.createNextConfig());
		this.metaConfig = metaConfig;
	}

	public Simulation(SimulationConfig config) {
		this.config = (SimConfig) config;
		this.events = this.config.createEventQueue();
		this.listeners = new SimulationListeners();
		this.world = new World(config.getSeed(), listeners);
		this.world.add(new Government());
		this.stocks = new StockMarket(world);
		this.day = 0;
	}
	
	@Override
	public boolean hasNext() {
		return metaConfig != null && metaConfig.shouldTryAgain();
	}

	public ISimulation getNext() {
		if (hasNext()) {
			return new Simulation(metaConfig);
		} else {
			return null;
		}
	}

	public String getComment() {
		return metaConfig == null ? null : metaConfig.getComment();
	}

	public void run() {
		if (!isFinished()) {
			step(config.getRounds());
		}
	}

	@Override
	public void forward(int steps) {
		step(steps);
	}

	public int getDay() {
		return day;
	}

	@Override
	public boolean isFinished() {
		return day >= config.getRounds();
	}
	
	public void finish() {
		step(config.getRounds() - day);
	}

	public void step(int days) {
		int target = this.day + days;
		for (; day < target; day++) {
			processEvents(day); //  must happen before daily endowments
			world.prepareDay(day);
			stocks.trade(day);
			RepeatedMarket market = new RepeatedMarket(world, listeners);
			market.iterate(day, config.getIntradayIterations());
			for (Producer firm : world.getFirms().getAllFirms()) {
				firm.produce(day);
			}
			world.finishDay(day);
		}
	}

	private void processEvents(int day) {
		while (!events.isEmpty() && events.peek().getDay() <= day) {
			SimEvent event = events.poll();
			event.execute(world);
			listeners.notifyEvent(event);
			if (event.reschedule()) {
				events.add(event);
			}
		}
	}

	@Override
	public SimulationConfig getConfig() {
		return config;
	}

	@Override
	public Collection<? extends IConsumer> getConsumers() {
		return world.getConsumers().getAllConsumers();
	}

	@Override
	public Collection<? extends IFirm> getFirms() {
		return world.getFirms().getAllFirms();
	}
	
	@Override
	public Collection<? extends IAgent> getAgents() {
		return world.getAgents().getAll();
	}
	
	@Override
	public Collection<? extends IPublicCompany> getListedCompanies() {
		return world.getAgents().getPublicCompanies();
	}

	@Override
	public void addListener(ISimulationListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	@Override
	public String getName() {
		return "Name";
	}

	@Override
	public String getDescription() {
		return "Description";
	}

	@Override
	public IMarket getStockMarket() {
		return stocks;
	}

	@Override
	public Collection<? extends IShareholder> getShareHolders() {
		return world.getAgents().getShareHolders();
	}

	@Override
	public IPublicCompany getListedCompany(Ticker ticker) {
		return world.getAgents().getCompany(ticker);
	}

}
