package com.agentecon.configurations;

import com.agentecon.agent.Endowment;
import com.agentecon.api.IConsumer;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumerListener;
import com.agentecon.consumer.Weight;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.events.SinConsumerEvent;
import com.agentecon.finance.MarketMaker;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.good.Stock;
import com.agentecon.sim.config.ConsumptionWeights;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.world.IWorld;

public class OverlappingGenerations extends SimConfig {

	private static final double RETURNS_TO_SCALE = 0.7;
	private static final int MONEY_SUPPLY_PER_FIRM = 1000;
	private static final int MAX_AGE = 1000;
	protected static final int MARKET_MAKERS = 10;
	private static final int FUNDAMENTALISTS = 5;

	private Good input;
	private Good[] outputs;

	public OverlappingGenerations() {
		super(10000, 41, 7);
		this.input = new Good("hours", 0.0);
		this.outputs = new Good[] { new Good("food", 0.0), new Good("medicine", 0.0) };
		addConsumers(100);
		addFirms(outputs[0], 15);
		addFirms(outputs[1], 5);
		addEvent(new SimEvent(0, MARKET_MAKERS) {

			@Override
			public void execute(IWorld sim) {
				for (int i = 0; i < getCardinality(); i++) {
					sim.add(new MarketMaker(sim.getAgents().getPublicCompanies()));
				}
			}
		});
		
		// for (int i = 0; i < MARKET_MAKERS; i++) {
		// addEvent(new SimEvent(5000 + i * 500, 1) {
		//
		// @Override
		// public void execute(IWorld sim) {
		// sim.add(new MarketMaker(sim.getAgents().getPublicCompanies()));
		// }
		// });
		// }
//		addEvent(new SimEvent(500, FUNDAMENTALISTS) {
//
//			@Override
//			public void execute(IWorld sim) {
//				for (int i = 0; i < getCardinality(); i++) {
//					sim.add(new Fundamentalist(sim));
//				}
//			}
//		});
	}

	public void addConsumers(int count) {
		Endowment end = new Endowment(new Stock(input, Endowment.HOURS_PER_DAY));
		ConsumptionWeights youngWeights = new ConsumptionWeights(new Good[] { input }, outputs, 9.0, 1.0);
		final ConsumptionWeights oldWeights = new ConsumptionWeights(new Good[] { input }, outputs, 5.0, 5.0);
		double generationsPerCycle = 1.5;
		addEvent(new SinConsumerEvent(0, 50, (int)(count * generationsPerCycle), MAX_AGE, (int)(MAX_AGE * generationsPerCycle), "Consumer", end, youngWeights.getFactory(0)) {

			@Override
			protected Consumer createConsumer() {
				Consumer c = super.createConsumer();
				c.addListener(new IConsumerListener() {

					@Override
					public void notifyConsuming(IConsumer inst, int age, Inventory inv, double utility) {
					}

					@Override
					public void notifyRetiring(IConsumer inst, int age) {
						((Consumer) inst).setUtilityFunction(oldWeights.getFactory(0).create(0));
					}
				});
				return c;
			}

		});
		// addEvent(new LinearConsumerEvent(100, 1, MAX_AGE, 10 * 1000 / MAX_AGE, "Consumer", end, consWeights.getFactory(0)));

	}

	public void addFirms(Good good, int count) {
		Endowment end = new Endowment(new IStock[] { new Stock(MONEY, MONEY_SUPPLY_PER_FIRM) }, new IStock[] {});
		IProductionFunction prodFun = new CobbDouglasProduction(good, new Weight(input, 10)).scale(RETURNS_TO_SCALE);
		addEvent(new FirmEvent(10, good + " firm", end, prodFun));
	}

	@Override
	public boolean hasAging() {
		return true;
	}

}
