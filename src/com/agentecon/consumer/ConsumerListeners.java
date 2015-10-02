package com.agentecon.consumer;

import com.agentecon.api.IConsumer;
import com.agentecon.good.Inventory;
import com.agentecon.metric.AbstractListenerList;

public class ConsumerListeners extends AbstractListenerList<IConsumerListener> implements IConsumerListener {

	@Override
	public void notifyConsuming(IConsumer inst, int age, Inventory inv, double utility) {
		for (IConsumerListener l: list){
			l.notifyConsuming(inst, age, inv, utility);
		}
	}

	@Override
	public void notifyRetiring(IConsumer inst, int age) {
		for (IConsumerListener l: list){
			l.notifyRetiring(inst, age);
		}		
	}

}
