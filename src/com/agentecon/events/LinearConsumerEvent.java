package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtil;
import com.agentecon.sim.config.IUtilityFactory;
import com.agentecon.world.IWorld;

public class LinearConsumerEvent extends ConsumerEvent {
	
	private int maxAge;

	public LinearConsumerEvent(int initialPopulation, int birthPerInterval, int maxAge, int interval, String name, Endowment end, IUtilityFactory utility) {
		super(0, birthPerInterval, interval, name, end, utility);
		this.maxAge = maxAge;
	}
	
	public LinearConsumerEvent(int initialPopulation, int birthPerInterval, int maxAge, int interval, String name, Endowment end, IUtility logUtil) {
		this(initialPopulation, birthPerInterval, maxAge, interval, name, end, new IUtilityFactory() {
			
			@Override
			public IUtility create(int number) {
				return new LogUtil();
			}
		});
	}

	@Override
	protected void addConsumer(IWorld sim) {
		sim.getConsumers().add(new Consumer(type, maxAge, end, utilFun.create(count++)));
	}
	
}
