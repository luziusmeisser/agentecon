// Created by Luzius on Apr 22, 2014

package com.agentecon.sim;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

import com.agentecon.agent.Endowment;
import com.agentecon.api.Event;
import com.agentecon.api.IAgent;
import com.agentecon.api.IConsumer;
import com.agentecon.api.IFirm;
import com.agentecon.api.IMarket;
import com.agentecon.api.ISimulation;
import com.agentecon.api.Price;
import com.agentecon.api.SimulationConfig;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.LogUtil;
import com.agentecon.consumer.Weight;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.events.UpdatePreferencesEvent;
import com.agentecon.finance.IPublicCompany;
import com.agentecon.finance.IShareholder;
import com.agentecon.finance.Ticker;
import com.agentecon.firm.Firm;
import com.agentecon.firm.LogProdFun;
import com.agentecon.firm.SensorFirm;
import com.agentecon.good.Good;
import com.agentecon.good.Inventory;
import com.agentecon.good.Stock;
import com.agentecon.market.Market;
import com.agentecon.metric.IMarketListener;
import com.agentecon.metric.ISimulationListener;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.metric.SimulationListeners;
import com.agentecon.price.PriceFactory;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;
import com.agentecon.world.IWorld;
import com.agentecon.world.World;

// The world
public class Simulation implements ISimulation {

	public static final int ROUNDS = 1000;

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
		this(new ComparisonConfiguration(10, 100).createConfig(3, 3));
	}

	public Simulation(SimulationConfig config) {
		this.config = (SimConfig) config;
		this.events = this.config.createEventQueue();
		this.listeners = new SimulationListeners();
		this.world = new World(config.getSeed(), listeners);
		this.day = 0;
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

	public void run() {
		forward(config.getRounds() - day);
	}

	public void step(int days) {
		int target = this.day + days;
		for (; day < target; day++) {
			double util = 0.0;
			world.notifyDayStarted(day);
			listeners.notifyDayStarted(day);
			processEvents(day);
			world.handoutEndowments();

			Market market = new Market(world.getRand());
			listeners.notifyMarketOpened(market);
			for (Firm firm : world.getRandomFirms()) {
				firm.offer(market);
			}
			// System.out.println("Before open: " + market);
			for (Consumer c : world.getRandomConsumers()) {
				c.maximizeUtility(market);
			}
			// System.out.println("After close: " + market);

			double inheritance = 0.0;
			Iterator<Consumer> iter = world.getAllConsumers().iterator();
			while (iter.hasNext()) {
				Consumer c = iter.next();
				util += c.consume();
				if (c.age()) {
					iter.remove();
					Inventory inv = c.notifyDied();
					inheritance += inv.getStock(SimConfig.MONEY).consume();
					world.notifyConsumerDied(c);
				}
			}
			if (inheritance != 0.0) {
				// ensure no money lost due to rounding
				world.getRandomConsumer().getMoney().add(inheritance);
			}
			double dividends = 0.0;
			for (Firm firm : world.getAllFirms()) {
				firm.produce();
				dividends += firm.payDividends(day);
			}
			distributeDividends(dividends, world.getAllConsumers());
			listeners.notifyDayEnded(day, util);
		}
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
		SimulationConfig config = new SimConfig(ROUNDS, 27);
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
		return world.getAllConsumers();
	}

	@Override
	public Collection<? extends IFirm> getFirms() {
		return world.getAllFirms();
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
		throw new AbstractMethodError();
	}

	@Override
	public Collection<? extends IAgent> getAgents() {
		throw new AbstractMethodError();
	}

	@Override
	public Collection<? extends IShareholder> getShareHolders() {
		throw new AbstractMethodError();
	}

	@Override
	public Collection<? extends IPublicCompany> getListedCompanies() {
		throw new AbstractMethodError();
	}

	@Override
	public IPublicCompany getListedCompany(Ticker ticker) {
		throw new AbstractMethodError();
	}

	public static void main(String[] args) {
		final Simulation sim = new Simulation();
		final InstantiatingHashMap<Good, Average> averages = new InstantiatingHashMap<Good, Average>() {
			
			@Override
			protected Average create(Good key) {
				return new Average();
			}
		};
		sim.addListener(new SimulationListenerAdapter() {
			
			// extract whatever data you like
			@Override
			public void notifyMarketOpened(IMarket market) {
				market.addMarketListener(new IMarketListener() {
					
					@Override
					public void notifyTradesCancelled() {
					}
					
					@Override
					public void notifySold(Good good, double quantity, Price price) {
						averages.get(good).add(quantity, price.getPrice());
					}
					
					@Override
					public void notifyOffered(Good good, double quantity, Price price) {
					}
				});
			}
			
		});
		sim.run();
		System.out.println("Good\tPrice\tVolume");
		for (Map.Entry<Good, Average> e: averages.entrySet()){
			System.out.println(e.getKey() + "\t" + e.getValue().getAverage() + "\t" + e.getValue().getTotWeight());
		}
	}

}
