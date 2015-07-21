package com.agentecon.events;

import java.util.ArrayList;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.LogUtil;
import com.agentecon.consumer.SavingConsumer;
import com.agentecon.good.Good;
import com.agentecon.world.IWorld;

public class SavingConsumerEvent extends EvolvingEvent {

	private Endowment end;
	private ArrayList<SavingConsumer> consumers;

	public SavingConsumerEvent(int card, String name, Endowment end, LogUtil utility, Good goodToSave) {
		super(0, card);
		this.end = end;
		this.consumers = new ArrayList<>();
		for (int i = 0; i < card; i++) {
			consumers.add(new SavingConsumer(name, end, utility, goodToSave));
		}
	}

	public SavingConsumerEvent(ArrayList<SavingConsumer> next, Endowment end) {
		super(0, next.size());
		this.end = end;
		this.consumers = next;
	}

	@Override
	public void execute(IWorld sim) {
		for (SavingConsumer sc : consumers) {
			sim.getConsumers().add(sc);
		}
	}

	public EvolvingEvent createNextGeneration() {
		ArrayList<SavingConsumer> next = new ArrayList<>();
		for (SavingConsumer sc : consumers) {
			next.add(sc.getNextGeneration(end));
		}
		return new SavingConsumerEvent(next, end);
	}

	@Override
	public double getScore() {
		double tot = 0.0;
		for (SavingConsumer sc: consumers){
			tot += sc.getTotalExperiencedUtility();
		}
		return tot;
	}

}