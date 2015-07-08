package com.agentecon.world;

import java.util.Collection;

import com.agentecon.consumer.Consumer;

public interface IConsumers {

	public void add(Consumer consumer);
	
	public Consumer getRandomConsumer();

	public Collection<Consumer> getRandomConsumers();
	
	/**
	 * @param number of randomly chosen agents, or all if -1
	 * @return
	 */
	public Collection<Consumer> getRandomConsumers(int cardinality);

}
