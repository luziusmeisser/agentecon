package com.agentecon.sim;

import java.util.ArrayList;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.LogUtil;
import com.agentecon.consumer.Weight;
import com.agentecon.events.ConstantTraderEvent;
import com.agentecon.events.EvolvingEvent;
import com.agentecon.events.SavingConsumerEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.good.Stock;

public class SavingConsumerConfiguration extends TaxShockConfiguration {

	public SavingConsumerConfiguration(int seed) {
		super(seed);
		
		constantEvents.add(new ConstantTraderEvent(SimConfig.MONEY, 0.23, outputs[0]));
	}

	protected void addConsumers(ArrayList<SimEvent> config, ArrayList<EvolvingEvent> newList, Weight[] defaultPrefs) {
		for (int i = 0; i < consumerTypes; i++) {
			String name = "Consumer " + i;
			Endowment end = new Endowment(new Stock(inputs[i], Endowment.HOURS_PER_DAY));
			LogUtil util = new LogUtil(defaultPrefs, new Weight(inputs[i], 14));
			newList.add(new SavingConsumerEvent(consumersPerType, name, end, util, outputs[0]));
		}
	}
	
}
