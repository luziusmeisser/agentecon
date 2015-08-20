package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.sim.IUtilityFactory;
import com.agentecon.world.IWorld;

public class SinConsumerEvent extends ConsumerEvent {
	
	private int start;
	private int cycle;
	private double births;

	public SinConsumerEvent(int start, int initialPopulation, int birthsPerCycle, int interval, String name, Endowment end, IUtilityFactory utility) {
		super(start, birthsPerCycle, 1, name, end, utility);
		this.start = start;
		this.cycle = interval;
		this.births = initialPopulation;
	}
	
	@Override
	public void execute(IWorld sim) {
		int day = sim.getDay() - start;
		assert day >= 0;
		double period = (day % cycle) * 2 * Math.PI / cycle;
		this.births += (Math.sin(period) + 1.0) * getCardinality() / cycle;
		while (births >= 1.0){
			births -= 1.0;
			addConsumer(sim);
		}
	}
	
}
