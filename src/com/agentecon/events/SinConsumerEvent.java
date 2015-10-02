package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtil;
import com.agentecon.sim.config.IUtilityFactory;
import com.agentecon.world.IWorld;

public class SinConsumerEvent extends ConsumerEvent {
	
	private int start;
	private int cycle;
	private double births;
	private int maxAge;

	public SinConsumerEvent(int start, int initialPopulation, int birthsPerCycle, int maxAge, int interval, String name, Endowment end, IUtilityFactory utility) {
		super(start, birthsPerCycle, 1, name, end, utility);
		this.start = start;
		this.maxAge = maxAge;
		this.cycle = interval;
		this.births = initialPopulation;
	}
	
	public SinConsumerEvent(int start, int initialPopulation, int birthsPerCycle, int maxAge, int interval, String name, Endowment end, IUtility logUtil) {
		this(start, initialPopulation, birthsPerCycle, maxAge, interval, name, end, new IUtilityFactory() {
			
			@Override
			public IUtility create(int number) {
				return new LogUtil();
			}
		});
	}

	@Override
	public void execute(IWorld sim) {
		int day = sim.getDay() - start;
		assert day >= 0;
		double period = (day % cycle) * 2 * Math.PI / cycle;
		this.births += (Math.sin(period) + 1.0) * getCardinality() / cycle;
		while (births >= 1.0){
			births -= 1.0;
			sim.add(createConsumer());
		}
	}
	
	@Override
	protected Consumer createConsumer() {
		return new Consumer(type, maxAge, end, utilFun.create(count++));
	}
	
}
