package com.agentecon.verification;

import com.agentecon.firm.decisions.CogsDividend;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.sim.Simulation;

public class ShockBenchmark {

	public ShockBenchmark() {

	}

	public void run() {
		String list = "";
		for (int type = 0; type < CogsDividend.TYPES; type++) {
			list += "\t" + new CogsDividend(0.5, type).toString();
		}
		System.out.println(list);
		for (double magnitude = 0.05; magnitude <= 5.0; magnitude += 0.05) {
			String line = Double.toString(magnitude);
			for (int type = 0; type < CogsDividend.TYPES; type++) {
				ShockTest test = new ShockTest(magnitude, new CogsDividend(0.5, type));
				Simulation sim = new Simulation(test.createNextConfig());
				final double[] totUtil = new double[1];
				sim.addListener(new SimulationListenerAdapter() {
					@Override
					public void notifyDayEnded(int day, double utility) {
						if (day >= 500) {
							totUtil[0] += utility;
						}
					}
				});
				sim.finish();
				line += "\t" + Double.toString(totUtil[0] / 500);
			}
			System.out.println(line);
		}
	}

	public static void main(String[] args) {
		new ShockBenchmark().run();
	}

}
