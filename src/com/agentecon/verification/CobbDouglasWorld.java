package com.agentecon.verification;

import com.agentecon.equilibrium.CobbDougWorld;
import com.agentecon.sim.CobbDougConfiguration;
import com.agentecon.sim.ConsumptionWeights;
import com.agentecon.sim.ProductionWeights;

public class CobbDouglasWorld {
	
	public static void main(String[] args) {
		long t0 = System.nanoTime();
		try {
			int cons = CobbDougConfiguration.CONSUMERS_PER_TYPE;
			int firms = CobbDougConfiguration.FIRMS_PER_TYPE;
			double[] cweights = new double[]{ConsumptionWeights.WEIGHTS[0], ConsumptionWeights.WEIGHTS[1]};
			double[] pweights = new double[]{ProductionWeights.WEIGHTS[0], ProductionWeights.WEIGHTS[1]};
			new CobbDougWorld(ConsumptionWeights.TIME_WEIGHT, cweights, cons, pweights, firms).solve();
		} finally {
			long diff = (System.nanoTime() - t0)/1000000;
			System.out.println(diff + "ms");
		}
	}

}
