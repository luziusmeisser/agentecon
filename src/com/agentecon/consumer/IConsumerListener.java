package com.agentecon.consumer;

import com.agentecon.good.Inventory;

public interface IConsumerListener {
	
	public void notifyConsuming(int age, Inventory inv, double utility);

}
