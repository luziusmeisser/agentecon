package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.LogUtil;
import com.agentecon.world.IWorld;

public class ConsumerEvent extends SimEvent {

	private String type;
	private Endowment end;
	private LogUtil utilFun;

	public ConsumerEvent(int card, String name, Endowment end, LogUtil utility) {
		this(0, 0, card, name, end, utility);
	}

	public ConsumerEvent(int time, int card, int interval, String name, Endowment end, LogUtil utility) {
		super(time, interval, card);
		this.type = name;
		this.end = end;
		this.utilFun = utility;
	}

	@Override
	public void execute(IWorld sim) {
		for (int i = 0; i < cardinality; i++) {
			addConsumer(sim);
		}
	}

	protected void addConsumer(IWorld sim) {
		sim.getConsumers().add(new Consumer(type, end, utilFun));
	}

	public String toString() {
		return "Add " + getCardinalityString() + " consumers";
	}

}
