package com.agentecon.sim.config;

import java.util.ArrayList;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.LogUtil;
import com.agentecon.consumer.Weight;
import com.agentecon.events.EvolvingEvent;
import com.agentecon.events.SavingConsumerEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.good.Stock;

public class SavingConsumerConfiguration extends TaxShockConfiguration {
	
	private double savingsRate;
	
	public SavingConsumerConfiguration(int seed) {
		this(seed, 0.0);
	}
	
	public SavingConsumerConfiguration(int seed, double rate) {
		super(seed);
		this.savingsRate = rate;
	}

	@Override
	protected void addConsumers(ArrayList<SimEvent> config, ArrayList<EvolvingEvent> newList, Weight[] defaultPrefs) {
		for (int i = 0; i < consumerTypes; i++) {
			String name = "Consumer " + i;
			Endowment end = new Endowment(new Stock(inputs[i], Endowment.HOURS_PER_DAY));
			LogUtil util = new LogUtil(defaultPrefs, new Weight(inputs[i], 14));
			newList.add(new SavingConsumerEvent(consumersPerType, name, end, util, outputs[0], savingsRate));
		}
	}
	
}
