package com.agentecon.verification;

import com.agentecon.good.Good;
import com.agentecon.price.EPrice;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.SimConfig;
import com.agentecon.stats.Numbers;

public class StolperSamuelsonParameterExploration {

	private PriceConfig config;
	private double current;
	private double end, increment;

	public StolperSamuelsonParameterExploration(double start, double end, double increment) {
		this.current = start;
		this.end = end;
		this.increment = increment;
		this.config = new PriceConfig(true, EPrice.EXPSEARCH);
	}

	public String run() {
		String table = "alpha\tAgent-based result\tSolver result";
		while (current <= end) {
			StolperSamuelson ss = new StolperSamuelson(current);
			Result resA = ss.runAgentBased(config, 2000);
			Result resC = ss.runConstrainedOptimization(resA, 0.000001);
			Good i = ss.getPizza();
			Good s = ss.getFondue();
			String line = Numbers.toString(current) + "\t" + resA.getRatio(i, s) + "\t" + resC.getRatio(i, s);
			table += "\n" + line;
			current += increment;
		}
		return table;
	}
	
	public SimConfig createConfiguration(int rounds) {
		return new StolperSamuelson(current).createConfiguration(config, rounds);
	}

	public static void main(String[] args) {
		System.out.println(new StolperSamuelsonParameterExploration(3.0, 3.0, 0.1).run());
	}

}
