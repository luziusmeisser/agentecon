package com.agentecon.consumer;

import com.agentecon.api.IConsumer;
import com.agentecon.good.Inventory;

public interface IConsumerListener {
	
	public void notifyConsuming(IConsumer inst, int age, Inventory inv, double utility);
	
	public void notifyRetiring(IConsumer inst, int age);

}
