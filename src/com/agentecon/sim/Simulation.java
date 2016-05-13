// Created by Luzius on Apr 22, 2014

package com.agentecon.sim;

import java.util.Collection;
import java.util.Queue;

import com.agentecon.CompEconCharts;
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
import com.agentecon.finance.Ticker;
import com.agentecon.firm.Firm;
import com.agentecon.metric.ISimulationListener;
import com.agentecon.metric.SimulationListeners;
import com.agentecon.price.EPrice;
import com.agentecon.price.PriceConfig;
import com.agentecon.verification.StolperSamuelson;
import com.agentecon.world.World;

// The world
public class Simulation implements ISimulation, IIteratedSimulation {

	private IConfiguration metaConfig;

	private SimConfig config;

	private int day;
	private Queue<SimEvent> events;
	private SimulationListeners listeners;
	private World world;

	static {
		// Disabled because too slow on app engine
		// Simulation.class.getClassLoader().setDefaultAssertionStatus(true);
	}
	
	public Simulation() {
		this(new CompEconCharts());
	}

	protected static SimConfig createConfig() {
		StolperSamuelson ss = new StolperSamuelson();
		SimConfig sc = ss.createConfiguration(new PriceConfig(true, EPrice.EXPSEARCH), 5000);
		return ss.enableShock(sc, 1000, 3.0);
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
			processEvents(day); // must happen before daily endowments
			world.prepareDay(day);
			RepeatedMarket market = new RepeatedMarket(world, listeners);
			market.iterate(day, config.getIntradayIterations());
			for (Firm firm : world.getFirms().getAllFirms()) {
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
	public Collection<? extends IAgent> getAgents() {
		throw new AbstractMethodError();
	}

	@Override
	public Collection<? extends IPublicCompany> getListedCompanies() {
		throw new AbstractMethodError();
	}

	@Override
	public IPublicCompany getListedCompany(Ticker arg0) {
		throw new AbstractMethodError();
	}

	@Override
	public Collection<? extends IShareholder> getShareHolders() {
		throw new AbstractMethodError();
	}

	@Override
	public IMarket getStockMarket() {
		throw new AbstractMethodError();
	}

}
