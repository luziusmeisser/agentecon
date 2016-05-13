package com.agentecon.verification;

import com.agentecon.good.Good;
import com.agentecon.price.EPrice;
import com.agentecon.price.PriceConfig;
import com.agentecon.stats.Numbers;

public class ParameterExploration2 {

	private PriceConfig config;

	public ParameterExploration2() {
		this.config = new PriceConfig(true, EPrice.EXPSEARCH);
	}

	public String run() {
		String table = "delta\tdelta_high\talpha\tAgent-based Pp/Pf\tSolver Pp/Pf\tAgent-based Pp/Ws\tSolver Pp/Ws\tAgent-based Pizzas\tSolver Pizzas";
		System.out.println(table);
		for (double retToScale = 0.3; retToScale <= 0.8001; retToScale += 0.05) {
			for (double share = 0.1; share <= 0.5001; share += 0.1) {
				for (double pref = 3.0; pref <= 5.001; pref += 0.5) {
					StolperSamuelson ss = new StolperSamuelson(pref, retToScale, new double[] { share, 1.0 - share });
					Result resA = ss.runAgentBased(config, 2000);
					Result resC = ss.runConstrainedOptimization(resA, 0.0001);
					Good i = ss.getPizza();
					Good s = ss.getFondue();
					Good ws = ss.getSwissHours();
					String line = Numbers.toString(retToScale) + "\t" + Numbers.toString(retToScale * share) + "\t" + Numbers.toString(pref) + "\t" + resA.getRatio(i, s) + "\t" + resC.getRatio(i, s)
							+ "\t" + resA.getRatio(i, ws) + "\t" + resC.getRatio(i, ws) + "\t" + resA.getAmount(i) + "\t" + resC.getAmount(i);;
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
