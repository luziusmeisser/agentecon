package com.agentecon.sim.config;

import java.util.ArrayList;

import com.agentecon.agent.Endowment;
import com.agentecon.api.SimulationConfig;
import com.agentecon.consumer.LogUtil;
import com.agentecon.events.EvolvingEvent;
import com.agentecon.events.SavingConsumerEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.events.UpdatePreferencesEvent;
import com.agentecon.good.Stock;

public class SavingConsumerConfiguration extends CobbDougConfiguration {

	private static final double LOW = 7.0;
	private static final double HIGH = 13.0;
	
	public static final int SHOCK = ROUNDS / 2;

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
			LogUtil util = defaultPrefs.createUtilFun(i);
			newList.add(new SavingConsumerEvent(consumersPerType, name, end, util, outputs[0], savingsRate));
		}
	}

}
