package com.agentecon.consumer;

import com.agentecon.good.Inventory;
import com.agentecon.metric.AbstractListenerList;

public class ConsumerListeners extends AbstractListenerList<IConsumerListener> implements IConsumerListener {

	@Override
	public void notifyConsuming(int age, Inventory inv, double utility) {
		for (IConsumerListener l: list){
			l.notifyConsuming(age, inv, utility);
		}
	}

}
