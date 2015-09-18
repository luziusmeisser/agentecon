package com.agentecon;

import com.agentecon.api.SimulationConfig;
import com.agentecon.price.EPrice;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.IConfiguration;
import com.agentecon.sim.SimConfig;
import com.agentecon.sim.Simulation;
import com.agentecon.verification.Result;
import com.agentecon.verification.StolperSamuelson;
import com.agentecon.verification.StolperSamuelsonParameterExploration;

/**
 * Run this to obtain the numbers presented in the paper. The resulting output is also provided in file CompEconCharts.output .
 */
public class CompEconCharts implements IConfiguration {

	public static boolean ENABLE_NORMALIZATION = true;

	private int figure = 3;

	@Override
	public SimulationConfig createNextConfig() {
		figure++;
		switch (figure) {
		default: {
			SimulationConfig sc = createChartConfig(PriceConfig.STANDARD_CONFIGS[figure], 2000, false);
			sc.setSeed(18);
			return sc;
		}
		case 8:
			return createChartConfig(new PriceConfig(true, EPrice.EXPSEARCH), 2500, true);
		case 9:
			SimulationConfig sc = createChartConfig(new PriceConfig(true, EPrice.CONSTANTPERCENTAGE), 2500, true);
			sc.setSeed(52);
			return sc;
		case 10:
			return createChartConfig(new PriceConfig(false, EPrice.EXPSEARCH), 2500, true);
		case 11:
			SimulationConfig config = createNonNormalizedConfig(new PriceConfig(true, EPrice.EXPSEARCH), 5000);
			config.setSeed(31);
			ENABLE_NORMALIZATION = false;
			return config;
		}
	}

	@Override
	public boolean shouldTryAgain() {
		return figure < 11;
	}

	@Override
	public String getComment() {
		return "figure " + figure;
	}

	public String createAccuracyBenchmark() {
		String table = "Method\tp_pizza / p_fondue\tx_pizza";
		final StolperSamuelson bm = new StolperSamuelson(3.0);
		Result hint = null;
		for (PriceConfig config : PriceConfig.STANDARD_CONFIGS) {
			if (config.isSensor()) {
				Result res = bm.runAgentBased(config, 2000);
				table += "\n" + config.getName() + "\t" + res.getRatio(bm.getPizza(), bm.getFondue()) + "\t" + res.getAmount(bm.getPizza());
				hint = res;
			}
		}
		Result resBenchmark = bm.runConstrainedOptimization(hint, 0.00001);
		table += "\nBenchmark\t" + resBenchmark.getRatio(bm.getPizza(), bm.getFondue()) + "\t" + resBenchmark.getAmount(bm.getPizza());
		return table;
	}

	public SimulationConfig createNonNormalizedConfig(PriceConfig priceConfig, int rounds) {
		StolperSamuelson ss = new StolperSamuelson(3.0);
		SimConfig config = ss.createConfiguration(priceConfig, rounds);
		for (int step = 500; step < 5000; step += 500) {
			ss.enableShock(config, step, 3.0);
			ss.enableShock(config, step + 2, 7.0);
		}
		return config;
	}

	public SimulationConfig createChartConfig(PriceConfig priceConfig, int rounds, boolean shock) {
		StolperSamuelson ss = new StolperSamuelson(3.0);
		SimConfig config = ss.createConfiguration(priceConfig, rounds);
		if (shock) {
			ss.enableShock(config, 1000, 7.0);
		}
		return config;
	}

	public String createNextChart() {
		Simulation sim = new Simulation(createNextConfig());
		ChartData data = new ChartData(StolperSamuelson.PIZZA, StolperSamuelson.FONDUE, StolperSamuelson.IT_HOUR, StolperSamuelson.CH_HOUR);
		sim.addListener(data);
		sim.finish();
		return data.getTable();
	}

	public static void main(String[] args) {
		CompEconCharts charts = new CompEconCharts();
		while (charts.shouldTryAgain()) {
			System.out.println("\n***************** " + charts.getComment() + " *****************");
			System.out.println(charts.createNextChart());
		}
//		System.out.println("\n***************** ACCURACY BENCHMARK *****************");
//		System.out.println(charts.createAccuracyBenchmark());
//		System.out.println("\n***************** PARAMETER EXPLORATION *****************");
//		System.out.println(new StolperSamuelsonParameterExploration(1.0, 5.0, 0.1).run());
	}

}
