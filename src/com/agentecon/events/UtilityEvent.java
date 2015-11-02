package com.agentecon.events;

import java.util.Collection;

import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.LogUtil;
import com.agentecon.world.IWorld;

public class UtilityEvent extends SimEvent {
	
	private LogUtil util;

	public UtilityEvent(int step, int card, LogUtil newUtility){
		super(step, card);
		this.util = newUtility;
	}

	@Override
	public void execute(IWorld sim) {
		Collection<Consumer> cons = sim.getConsumers().getRandomConsumers(cardinality);
		for (Consumer con: cons){
			con.setUtilityFunction(util);
		}
	}

}
