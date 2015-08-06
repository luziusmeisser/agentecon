// Created by Luzius on Apr 22, 2014

package com.agentecon.sim;

import java.util.Collection;
import java.util.Queue;

import com.agentecon.agent.Endowment;
import com.agentecon.api.IConsumer;
import com.agentecon.api.IFirm;
import com.agentecon.api.ISimulation;
import com.agentecon.api.ITrader;
import com.agentecon.api.SimulationConfig;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.LogUtil;
import com.agentecon.consumer.Weight;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.events.UpdatePreferencesEvent;
import com.agentecon.firm.LogProdFun;
import com.agentecon.firm.SensorFirm;
import com.agentecon.good.Stock;
import com.agentecon.metric.ISimulationListener;
import com.agentecon.metric.SimulationListeners;
import com.agentecon.price.PriceFactory;
import com.agentecon.world.IWorld;
import com.agentecon.world.World;

// The world
public class Simulation implements ISimulation {

	private TaxShockConfiguration metaConfig;

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
		this(new TaxShockConfiguration(13));
//		this(new VolumeTraderConfiguration(133, 18.55));
//		this(new VolumeTraderConfiguration(133, 20.0));
//		this(new SavingConsumerConfiguration(133, 0.20));
	}

	public Simulation(TaxShockConfiguration metaConfig) {
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

	public ISimulation getNext() {
		if (metaConfig.shouldTryAgain()) {
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

	public void step(int days) {
		int target = this.day + days;
		for (; day < target; day++) {
			processEvents(day); // must happen before daily endowments
			world.prepareDay(day);
			
			RepeatedMarket market = new RepeatedMarket(world, listeners);
			market.iterate(day, 3);
			
			world.finishDay(day);
		}
	}

	private void processEvents(int day) {
		while (!events.isEmpty() && events.peek().getDay() <= day) {
			SimEvent event = events.poll();
			event.execute(world);
			if (event.reschedule()) {
				events.add(event);
			}
		}
	}

	private static SimulationConfig createTestConfig() {
		SimulationConfig config = new SimConfig(10000, 27);
		Weight w1 = new Weight(SimConfig.PIZZA, 3.0);
		Weight w2 = new Weight(SimConfig.FONDUE, 2.0);
		Weight w3 = new Weight(SimConfig.BEER, 5.0);

		// config.addEvent(new SinConsumerEvent(0, 50, 250, "Italian", new Endowment(new Stock(SimConfig.ITALTIME, Endowment.HOURS_PER_DAY)),
		// new LogUtil(w1, w2, w3, new Weight(SimConfig.ITALTIME, 14))));
		// config.addEvent(new SinConsumerEvent(0, 50, 250, "Swiss", new Endowment(new Stock(SimConfig.SWISSTIME, Endowment.HOURS_PER_DAY)),
		// new LogUtil(w1, w2, w3, new Weight(SimConfig.SWISSTIME, 14))));
		// config.addEvent(new SinConsumerEvent(0, 50, 250, "German", new Endowment(new Stock(SimConfig.GERTIME, Endowment.HOURS_PER_DAY)),
		// new LogUtil(w1, w2, w3, new Weight(SimConfig.GERTIME, 14))));

		config.addEvent(new ConsumerEvent(100, "Italian", new Endowment(new Stock(SimConfig.ITALTIME, Endowment.HOURS_PER_DAY)), new LogUtil(w1, w2, w3, new Weight(SimConfig.ITALTIME, 14))));
		// config.addEvent(new ConsumerEvent(0, 1, 5, "Swiss", new Endowment(new Stock(SimConfig.SWISSTIME, Endowment.HOURS_PER_DAY)),
		// new LogUtil(w1, w2, w3, new Weight(SimConfig.SWISSTIME, 14))));
		// config.addEvent(new ConsumerEvent(0, 1, 5, "German", new Endowment(new Stock(SimConfig.GERTIME, Endowment.HOURS_PER_DAY)),
		// new LogUtil(w1, w2, w3, new Weight(SimConfig.GERTIME, 14))));

		double p1 = 6.0;
		double p2 = 4.0;
		double p3 = 8.0;

		config.addEvent(new FirmEvent(10, "Pizzeria", new Endowment(new Stock[] { new Stock(SimConfig.MONEY, 1000), new Stock(SimConfig.PIZZA, 10) }, new Stock[] {}),
				new LogProdFun(SimConfig.PIZZA, new Weight(SimConfig.ITALTIME, p3), new Weight(SimConfig.SWISSTIME, p1), new Weight(SimConfig.GERTIME, p2)),
				new String[] { PriceFactory.EXPSEARCH, "0.05" }) {
			@Override
			public void execute(IWorld sim) {
				for (int i = 0; i < getCardinality(); i++) {
					sim.getFirms().add(new SensorFirm("Sensor Pizzeria", end, prodFun, new PriceFactory(sim.getRand(), priceParams)));
				}
			}
		});
		// config.addEvent(new FirmEvent(10, "Chalet", new Endowment(new Stock[] { new Stock(SimConfig.MONEY, 1000), new Stock(SimConfig.FONDUE, 10) }, new Stock[] {}),
		// new LogProdFun(SimConfig.FONDUE, new Weight(SimConfig.ITALTIME, p1), new Weight(SimConfig.SWISSTIME, p3), new Weight(SimConfig.GERTIME, p2)), new String[] { PriceFactory.EXPSEARCH, "0.05"
		// }) {
		// @Override
		// public void execute(IWorld sim) {
		// for (int i = 0; i < getCardinality(); i++) {
		// sim.getFirms().add(new SensorFirm("Sensor Chalet", end, prodFun, new PriceFactory(sim.getRand(), priceParams)));
		// }
		// }
		// });
		// config.addEvent(new FirmEvent(10, "Biergarten", new Endowment(new Stock[] { new Stock(SimConfig.MONEY, 1000), new Stock(SimConfig.BEER, 10) }, new Stock[] {}),
		// new LogProdFun(SimConfig.BEER, new Weight(SimConfig.ITALTIME, p1), new Weight(SimConfig.SWISSTIME, p2), new Weight(SimConfig.GERTIME, p3)), new String[] { PriceFactory.EXPSEARCH, "0.05" })
		// {
		// @Override
		// public void execute(IWorld sim) {
		// for (int i = 0; i < getCardinality(); i++) {
		// sim.getFirms().add(new SensorFirm("Sensor Biergarten", end, prodFun, new PriceFactory(sim.getRand(), priceParams)));
		// }
		// }
		// });
		config.addEvent(new UpdatePreferencesEvent(1200) {

			@Override
			protected void update(Consumer c) {
				c.getUtilityFunction().updateWeight(new Weight(SimConfig.PIZZA, 2.0));
				c.getUtilityFunction().updateWeight(new Weight(SimConfig.FONDUE, 8.0));
			}

		});
		return config;
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
	public Collection<? extends ITrader> getTraders() {
		return world.getTraders().getAllTraders();
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

}
