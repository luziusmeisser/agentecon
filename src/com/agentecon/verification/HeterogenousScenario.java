package com.agentecon.verification;

import java.util.Random;

import com.agentecon.agent.Endowment;
import com.agentecon.events.FirmEvent;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.config.SimConfig;

public class HeterogenousScenario extends StolperSamuelson {

	private Random rand;

	public HeterogenousScenario() {
		this.rand = new Random(1313);
	}

	@Override
	protected void addFirms(PriceConfig pricing, int scale, SimConfig config) {
		for (int i = 0; i < outputs.length; i++) {
			Endowment end = new Endowment(new IStock[] { new Stock(SimConfig.MONEY, 1000) }, new IStock[] {});
			for (int f = 0; f < scale * FIRMS_PER_TYPE; f++) {
				pricing = PriceConfig.STANDARD_CONFIGS[6];
				IProductionFunction prodfun = prodWeights.createProdFun(i, RETURNS_TO_SCALE - 0.05 + rand.nextDouble() / 10);
				config.addEvent(new FirmEvent(1, "firm_" + i, end, prodfun, pricing));
			}
		}
	}

}
