package com.agentecon;

import java.util.HashMap;
import java.util.Map;

import com.agentecon.api.SimulationConfig;
import com.agentecon.price.EPrice;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.IConfiguration;
import com.agentecon.sim.SimConfig;
import com.agentecon.sim.Simulation;
import com.agentecon.util.InstantiatingHashMap;
import com.agentecon.verification.Result;
import com.agentecon.verification.StolperSamuelson;

/**
 * Run this to obtain the numbers presented in the paper. The resulting output is also provided in file CompEconCharts.output .
 */
public class CompEconCharts implements IConfiguration {

	public static boolean ENABLE_NORMALIZATION = true;

	private int figure = 3;

	@Override
	public SimulationConfig createNextConfig() {
		return createConfig(++figure);
	}

	public SimulationConfig createConfig(int index) {
		switch (index) {
		default:
			return createChartConfig(PriceConfig.STANDARD_CONFIGS[figure], 2000, false);
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
		switch (figure) {
		default:
			return PriceConfig.STANDARD_CONFIGS[figure].getType().getName() + " adjustments";
		case 8:
			return EPrice.EXPSEARCH.getName() + ", shock at 1000";
		case 9:
			return EPrice.CONSTANTPERCENTAGE.getName() + ", shock at 1000";
		case 10:
			return EPrice.EXPSEARCH.getName() + ", shock at 1000, no sensor pricing";
		case 11:
			return "no normalization, many shocks";
		}
	}

	public String createAccuracyBenchmark() {
		String table = "Method\tp_pizza / p_fondue\tx_pizza\tw_Italian/w_Swiss";
		final StolperSamuelson bm = new StolperSamuelson();
		Result hint = null;
		HashMap<String, Result> results = new InstantiatingHashMap<String, Result>() {

			@Override
			protected Result create(String key) {
				return new Result();
			}
		};
		for (int i = 0; i < 1; i++) {
			for (PriceConfig config : PriceConfig.STANDARD_CONFIGS) {
				if (config.isSensor()) {
					SimulationConfig simConfig = createChartConfig(config, 2000, false);
					simConfig.setSeed(10 + i);
					Result res = bm.runAgentBased(simConfig);
					results.get(config.getName()).absorb(res);
					hint = res;
				}
			}
		}
		for (Map.Entry<String, Result> e : results.entrySet()) {
			Result res = e.getValue();
			table += "\n" + e.getKey() + "\t" + res.getRatio(bm.getPizza(), bm.getFondue()) + "\t" + res.getRatio(bm.getPizza(), bm.getSwissHours()) + "\t" + res.getAmount(bm.getPizza());
		}

		Result resBenchmark = bm.runConstrainedOptimization(hint, 0.000001);
		table += "\nBenchmark\t" + resBenchmark.getRatio(bm.getPizza(), bm.getFondue()) + "\t" + resBenchmark.getRatio(bm.getPizza(), bm.getSwissHours()) + "\t"
				+ resBenchmark.getAmount(bm.getPizza());
		return table;
	}

	public SimulationConfig createNonNormalizedConfig(PriceConfig priceConfig, int rounds) {
		StolperSamuelson ss = new StolperSamuelson();
		SimConfig config = ss.createConfiguration(priceConfig, rounds);
		for (int step = 500; step < 5000; step += 500) {
			ss.enableShock(config, step, 3.0);
			ss.enableShock(config, step + 2, 7.0);
		}
		return config;
	}

	public SimulationConfig createChartConfig(PriceConfig priceConfig, int rounds, boolean shock) {
		StolperSamuelson ss = new StolperSamuelson();
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
			String result = charts.createNextChart();
			System.out.println("\n***************** " + charts.getComment() + " *****************");
			System.out.println(result);
		}
		System.out.println("\n***************** ACCURACY BENCHMARK *****************");
		System.out.println(charts.createAccuracyBenchmark());
		// System.out.println("\n***************** PARAMETER EXPLORATION *****************");
		// System.out.println(new ParameterExploration(1.0, 5.0, 0.1).run());
	}

}
