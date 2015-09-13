package com.agentecon.verification;

import java.util.Random;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.LogUtil;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.world.IWorld;

public class HeterogenousScenario extends StolperSamuelson {

	private Random rand;

	public HeterogenousScenario() {
		this.rand = new Random(1313);
	}
	
	protected void addConsumers(int scale, SimConfig config) {
		for (int i = 0; i < inputs.length; i++) {
			Endowment end = new Endowment(new Stock(inputs[i], HOURS_PER_DAY));
			LogUtil util = consWeights.createUtilFun(i, 0).wiggle(rand);
			config.addEvent(new ConsumerEvent(scale * CONSUMERS_PER_TYPE, "cons_" + i, end, util));
		}
		config.addEvent(new SimEvent(0, 1, -1){

			@Override
			public void execute(IWorld sim) {
				for (com.agentecon.consumer.Consumer c: sim.getConsumers().getRandomConsumers()){
					c.setUtilityFunction(((LogUtil)c.getUtilityFunction()).wiggle(rand));
				}
			}
			
		});
	}

	@Override
	protected void addFirms(PriceConfig pricing, int scale, SimConfig config) {
		for (int i = 0; i < outputs.length; i++) {
			Endowment end = new Endowment(new IStock[] { new Stock(SimConfig.MONEY, 1000) }, new IStock[] {});
			for (int f = 0; f < scale * FIRMS_PER_TYPE; f++) {
				IProductionFunction prodfun = prodWeights.createProdFun(i, RETURNS_TO_SCALE - 0.05 + rand.nextDouble() / 10);
				config.addEvent(new FirmEvent(1, "firm_" + i, end, prodfun, pricing));
			}
		}
	}

}
