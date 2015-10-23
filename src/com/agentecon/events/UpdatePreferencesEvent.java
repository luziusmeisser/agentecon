// Created on May 21, 2015 by Luzius Meisser

package com.agentecon.events;

import com.agentecon.consumer.Consumer;
import com.agentecon.world.IWorld;

public abstract class UpdatePreferencesEvent extends SimEvent {

	public UpdatePreferencesEvent(int step) {
		super(step, -1);
	}

	@Override
	public void execute(IWorld sim) {
		for (Consumer c : sim.getConsumers().getRandomConsumers(getCardinality())) {
			update(c);
		}
	}

	protected abstract void update(Consumer c);

	@Override
	public String toString(){
		return "Update preference event";
	}
	
}
