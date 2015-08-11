package com.agentecon.events;

import com.agentecon.consumer.Consumer;
import com.agentecon.world.IWorld;

public class MoneyPrintEvent extends SimEvent {

	private double amount;

	public MoneyPrintEvent(int step, int card, double amount) {
		super(step, card);
		this.amount = amount;
	}

	@Override
	public void execute(IWorld sim) {
		for (Consumer c: sim.getConsumers().getRandomConsumers(getCardinality())){
			c.getMoney().add(amount);
		}
	}

}
