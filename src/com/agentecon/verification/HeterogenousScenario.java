package com.agentecon.verification;

import java.util.Random;

import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.LogUtil;
import com.agentecon.events.SimEvent;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.world.IWorld;

public class HeterogenousScenario extends StolperSamuelson {

	private Random rand;

	public HeterogenousScenario() {
		this.rand = new Random(1313);
	}

	// @Override
	// protected void addConsumers(int scale, SimConfig config) {
	// for (int i = 0; i < inputs.length; i++) {
	// Endowment end = new Endowment(new Stock(inputs[i], HOURS_PER_DAY));
	// LogUtil util = consWeights.createUtilFun(i, 0).wiggle(rand);
	// config.addEvent(new ConsumerEvent(scale * CONSUMERS_PER_TYPE, "cons_" + i, end, util));
	// }
	// }

	@Override
	protected void addSpecialEvents(SimConfig config) {
		int time = 4000;
		for (int i = 0; i < 1000; i++) {
			// for (double current = LOW; current < HIGH; current += 0.01) {
			// final double c2 = current;
			config.addEvent(new SimEvent(time / 2, -1) {

				@Override
				public void execute(IWorld sim) {
					for (Consumer c : sim.getConsumers().getRandomConsumers()) {
						if (update(c)) {
							return;
						}
					}
					// for (Consumer c : sim.getConsumers().getRandomConsumers(getCardinality())) {
					// update(c);
					// }
				}

				protected boolean update(com.agentecon.consumer.Consumer c) {
					LogUtil util = (LogUtil) c.getUtilityFunction();
					if (util.getWeight(outputs[0]) == HIGH) {
						util = consWeights.createDeviation(util, outputs[0], LOW);
						util = consWeights.createDeviation(util, outputs[1], HIGH);
						c.setUtilityFunction(util);
						return true;
					} else {
						return false;
					}
				}

			});
			time += 1;
		}
	}

	// @Override
	// protected void addFirms(PriceConfig pricing, int scale, SimConfig config) {
	// for (int i = 0; i < outputs.length; i++) {
	// Endowment end = new Endowment(new IStock[] { new Stock(SimConfig.MONEY, 1000) }, new IStock[] {});
	// for (int f = 0; f < scale * FIRMS_PER_TYPE; f++) {
	// IProductionFunction prodfun = prodWeights.createProdFun(i, RETURNS_TO_SCALE - 0.05 + rand.nextDouble() / 10);
	// config.addEvent(new FirmEvent(1, "firm_" + i, end, prodfun, pricing));
	// }
	// }
	// }

}
