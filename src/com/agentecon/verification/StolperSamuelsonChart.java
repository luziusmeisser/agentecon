package com.agentecon.verification;

import com.agentecon.good.Good;
import com.agentecon.price.EPrice;
import com.agentecon.price.PriceConfig;
import com.agentecon.stats.Numbers;

public class StolperSamuelsonChart {

	private PriceConfig config;
	private double current;
	private double end, increment;

	public StolperSamuelsonChart(double start, double end, double increment) {
		this.current = start;
		this.end = end;
		this.increment = increment;
		this.config = new PriceConfig(true, EPrice.EXPSEARCH);
	}

	public void run() {
		String table = "";
		while (current <= end) {
			StolperSamuelson ss = new StolperSamuelson(current);
			Result resA = ss.runAgentBased(config, 2000);
			Result resC = ss.runConstrainedOptimization(resA, 0.00000001);
			Good i = ss.getInputs()[0];
			Good s = ss.getOutputs()[1];
			String line = Numbers.toString(current) + "\t" + resA.getRatio(i, s) + "\t" + resC.getRatio(i, s);
			System.out.println(line);
			table += line + "\n";
			current += increment;
		}
		System.out.println(table);
	}

	public static void main(String[] args) {
		new StolperSamuelsonChart(1.0, 5.0, 0.1).run();
	}
}
