package com.agentecon.sim.config;

import java.util.ArrayList;

import com.agentecon.agent.Endowment;
import com.agentecon.api.SimulationConfig;
import com.agentecon.consumer.LogUtil;
import com.agentecon.consumer.ReincarnatingConsumer;
import com.agentecon.events.EvolvingEvent;
import com.agentecon.events.SavingConsumerEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.events.UpdatePreferencesEvent;
import com.agentecon.good.Stock;
import com.agentecon.sim.Simulation;
import com.agentecon.verification.PriceMetric;
import com.agentecon.verification.PricePrinter;

public class SavingConsumerConfiguration extends CobbDougConfiguration {
	
	public static final int SCALE = 1;

	private static final double LOW = 6.0;
	private static final double HIGH = 12.0;

	public static final int SHOCK = 500 * SCALE + ReincarnatingConsumer.START;

	private double savingsRate;

	public SavingConsumerConfiguration() {
		this(23, 0.0);
	}

	public SavingConsumerConfiguration(int seed, double rate) {
		super(10, 100, 1, 1, seed);
		this.savingsRate = rate;
	}

	@Override
	public SimulationConfig createNextConfig() {
		SimulationConfig config = super.createNextConfig();
		final ConsumptionWeights consWeights = new ConsumptionWeights(inputs, outputs, HIGH);
		config.addEvent(new UpdatePreferencesEvent(SHOCK) {

			@Override
			protected void update(com.agentecon.consumer.Consumer c) {
				LogUtil util = (LogUtil) c.getUtilityFunction();
				util = consWeights.createDeviation(util, outputs[0], HIGH);
				c.setUtilityFunction(util);
			}

		});
		return config;
	}

	@Override
	protected void addConsumers(ArrayList<SimEvent> config, ArrayList<EvolvingEvent> newList, ConsumptionWeights defaultPrefs) {
		for (int i = 0; i < outputs.length; i++) {
			String name = "Consumer " + i;
			Endowment end = new Endowment(new Stock(inputs[i], Endowment.HOURS_PER_DAY));
			defaultPrefs = new ConsumptionWeights(inputs, outputs, LOW);
//			 config.add(new ConsumerEvent(consumersPerType, name, end, defaultPrefs.createUtilFun(i)));
			newList.add(new SavingConsumerEvent(consumersPerType, name, end, defaultPrefs.getFactory(i), outputs[0], savingsRate));
		}
	}

	public static void main(String[] args) {
		SavingConsumerConfiguration config = new SavingConsumerConfiguration();
		Simulation sim = new Simulation(config);
		int iter = 0;
		while (sim != null) {
			System.out.println("******** ITERATION " + iter++ + " **********");
			PriceMetric metric1 = new PriceMetric(ReincarnatingConsumer.START, SHOCK);
			PriceMetric metric2 = new PriceMetric(SHOCK, ROUNDS);
			PricePrinter pp = new PricePrinter(ReincarnatingConsumer.START, ROUNDS);
			sim.addListener(metric1);
			sim.addListener(metric2);
			sim.addListener(pp);
			sim.finish();
//			metric1.printResult(System.out);
//			metric2.printResult(System.out);
			sim = (Simulation) sim.getNext();
		}
	}

}
