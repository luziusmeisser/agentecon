package com.agentecon.events;

import java.util.ArrayList;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.LogUtil;
import com.agentecon.consumer.SavingConsumer;
import com.agentecon.good.Good;
import com.agentecon.util.Average;
import com.agentecon.world.IWorld;

public class SavingConsumerEvent extends EvolvingEvent {

	private Endowment end;
	private ArrayList<SavingConsumer> consumers;

	public SavingConsumerEvent(int card, String name, Endowment end, LogUtil utility, Good goodToSave, double savingsRate) {
		super(0, card);
		this.end = end;
		this.consumers = new ArrayList<>();
		for (int i = 0; i < card; i++) {
			consumers.add(new SavingConsumer(name, end, utility, goodToSave, savingsRate));
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
			sim.add(sc);
		}
	}

	public EvolvingEvent createNextGeneration() {
		ArrayList<SavingConsumer> next = new ArrayList<>();
		for (SavingConsumer sc : consumers) {
			next.add(sc.getNextGeneration(end));
		}
		return new SavingConsumerEvent(next, end);
	}
	
	public double getDailySavings(){
		Average avg = new Average();
		for (SavingConsumer sc: consumers){
			avg.add(1.0, sc.getDailySavings());
		}
		return avg.getAverage();
	}
	
	public double getDailyConsumption(){
		Average avg = new Average();
		for (SavingConsumer sc: consumers){
			avg.add(1.0, sc.getSmoothConsumption());
		}
		return avg.getAverage();
	}
	
	private double getDailyLeisure() {
		Average avg = new Average();
		for (SavingConsumer sc: consumers){
			avg.add(1.0, sc.getAverageLeisure());
		}
		return avg.getAverage();
	}
	
	@Override
	public double getScore() {
		Average avg = new Average();
		for (SavingConsumer sc: consumers){
			avg.add(1.0, sc.getTotalExperiencedUtility());
		}
		return avg.getAverage();
	}
	
	public String toString(){
		return "Consumers with daily savings of " + getDailySavings() + " and daily consumption of " + getDailyConsumption() + " output and " + getDailyLeisure() + " leisure";
	}

}
