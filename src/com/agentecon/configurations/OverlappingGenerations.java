package com.agentecon.configurations;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.Weight;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.LinearConsumerEvent;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.sim.config.ConsumptionWeights;
import com.agentecon.sim.config.SimConfig;

public class OverlappingGenerations extends SimConfig {
	
	private static final double RETURNS_TO_SCALE = 0.5;
	private static final int MONEY_SUPPLY_PER_FIRM = 1000;
	private static final int MAX_AGE = 1000;
	
	private Good input;
	private Good[] outputs;

	public OverlappingGenerations() {
		super(7000, 42, 0);
		this.input = new Good("hours");
		this.outputs = new Good[]{new Good("apples")};
		addConsumers(100);
		addFirms(10);
	}
	
	public void addConsumers(int count){
		Endowment end = new Endowment(new Stock(input, Endowment.HOURS_PER_DAY));
		ConsumptionWeights consWeights = new ConsumptionWeights(new Good[]{input}, outputs, 7.0, 3.0);
//		addEvent(new SinConsumerEvent(0, 50, count / 5, MAX_AGE, 200, "Consumer", end, consWeights.getFactory(0)));
		addEvent(new LinearConsumerEvent(10, 1, MAX_AGE, 10, "Consumer", end, consWeights.getFactory(0)));
		
	}
	
	public void addFirms(int count){
		for (int i=0; i<outputs.length; i++){
			Endowment end = new Endowment(new IStock[]{new Stock(MONEY, MONEY_SUPPLY_PER_FIRM)}, new IStock[]{});
			IProductionFunction prodFun = new CobbDouglasProduction(outputs[i], new Weight(input, 10)).scale(RETURNS_TO_SCALE);
			addEvent(new FirmEvent(10, outputs[i] + " firm", end, prodFun));
		}
	}
	
	@Override
	public boolean hasAging(){
		return true;
	}
	

}
