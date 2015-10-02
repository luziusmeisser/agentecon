package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtil;
import com.agentecon.sim.config.IUtilityFactory;
import com.agentecon.world.IWorld;

public class ConsumerEvent extends SimEvent {

	protected int count;
	protected String type;
	protected Endowment end;
	protected IUtilityFactory utilFun;

	public ConsumerEvent(int card, String name, Endowment end, IUtilityFactory utility) {
		this(0, card, 0, name, end, utility);
	}

	public ConsumerEvent(int time, int card, int interval, String name, Endowment end, IUtilityFactory utility) {
		super(time, interval, card);
		this.type = name;
		this.end = end;
		this.count = 0;
		this.utilFun = utility;
	}

	public ConsumerEvent(int card, String name, Endowment end, final LogUtil util) {
		this(card, name, end, new IUtilityFactory(){

			@Override
			public IUtility create(int number) {
				return util;
			}
			
		});
	}

	@Override
	public void execute(IWorld sim) {
		for (int i = 0; i < cardinality; i++) {
			sim.add(createConsumer());
		}
	}

	protected Consumer createConsumer() {
		return new Consumer(type, end, utilFun.create(count++));
	}

	public String toString() {
		return "Add " + getCardinalityString() + " consumers";
	}

}
