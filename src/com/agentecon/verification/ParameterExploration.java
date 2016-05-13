package com.agentecon.verification;

import com.agentecon.good.Good;
import com.agentecon.price.EPrice;
import com.agentecon.price.PriceConfig;
import com.agentecon.stats.Numbers;

public class ParameterExploration {

	private PriceConfig config;
	private double current;
	private double end, increment;

	public ParameterExploration(double start, double end, double increment) {
		this.current = start;
		this.end = end;
		this.increment = increment;
		this.config = new PriceConfig(true, EPrice.EXPSEARCH);
	}

	public String run() {
		String table = "alpha\tAgent-based result\tSolver result";
		System.out.println(table);
		while (current <= end) {
			StolperSamuelson ss = new StolperSamuelson(current);
			Result resA = ss.runAgentBased(config, 2000);
			Result resC = ss.runConstrainedOptimization(resA, 0.0001);
			Good i = ss.getPizza();
			Good s = ss.getFondue();
			Good ws = ss.getSwissHours();
			String line = Numbers.toString(current) + "\t" + resA.getRatio(i, s) + "\t" + resC.getRatio(i, s) + "\t" + resA.getRatio(i, ws) + "\t" + resC.getRatio(i, ws) + "\t" + resA.getAmount(i) + "\t" + resC.getAmount(i);
			table += "\n" + line;
			System.out.println(line);
			current += increment;
		}
		return table;
	}
	
	public static void main(String[] args) {
		new ParameterExploration(1.0, 5.0, 0.1).run();
	}

}
