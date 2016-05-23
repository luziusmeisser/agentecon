package com.agentecon.verification;

import com.agentecon.price.EPrice;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.Simulation;
import com.agentecon.stats.Numbers;

/**
 * Produces output ParameterExploration2.out
 * 
 * @author Luzius
 */
public class ParameterExploration2 {

	private PriceConfig config;

	public ParameterExploration2() {
		this.config = new PriceConfig(true, EPrice.EXPSEARCH);
	}

	public String run() {
		String table = "delta\tdelta_high\talpha\tAgent-based Pp/Pf\tSolver Pp/Pf\tAgent-based Pp/Ws\tSolver Pp/Ws\tAgent-based Pizzas\tSolver Pizzas";
		System.out.println(table);
		for (double retToScale = 0.1; retToScale <= 0.701; retToScale += 0.05) {
			for (double share = 0.1; share <= 0.5001; share += 0.1) {
				for (double pref = 1.0; pref <= 9.001; pref += 0.5) {
					StolperSamuelson ss = new StolperSamuelson(pref, retToScale, new double[] { share, 1.0 - share });
					int steps = 2000;
					Simulation sim = new Simulation(ss.createConfiguration(config, 2000));
					sim.forward(steps / 2);
					PriceMetric prices = new PriceMetric(steps / 2);
					sim.addListener(prices);
					final EquilibriumTest[] tests = new EquilibriumTest[2];
					for (int i=0; i<tests.length; i++){
						tests[i] = new EquilibriumTest(i, sim.getFirms());
					}
					sim.finish();
					Result res = prices.getResult();
					String line = Numbers.toString(retToScale) + "\t" + Numbers.toString(retToScale * share) + "\t" + Numbers.toString(pref) + "\t" + Numbers.toString(res.getPrice(StolperSamuelson.PIZZA)) + "\t" + Numbers.toString(res.getAmount(StolperSamuelson.PIZZA)) + "\t" + Numbers.toString(res.getPrice(StolperSamuelson.FONDUE)) + "\t" + Numbers.toString(res.getAmount(StolperSamuelson.FONDUE)) + "\t" + Numbers.toString(res.getPrice(StolperSamuelson.IT_HOUR)) + "\t" + Numbers.toString(res.getAmount(StolperSamuelson.IT_HOUR)) + "\t" + Numbers.toString(res.getPrice(StolperSamuelson.CH_HOUR)) + "\t" + Numbers.toString(res.getAmount(StolperSamuelson.CH_HOUR));
					double deviation = 0.0;
					for (EquilibriumTest test: tests){
						deviation = Math.max(test.getDeviation(res), deviation);
					}
					line += "\t" + String.format("%.6f", deviation);
					System.out.println(line);
					table += "\n" + line;
				}
			}
		}
		return table;
	}

	public static void main(String[] args) {
		new ParameterExploration2().run();
	}

}
