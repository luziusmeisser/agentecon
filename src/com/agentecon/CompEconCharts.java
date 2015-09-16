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

public class CompEconCharts implements IConfiguration {
	
	private int figure = 7;

	@Override
	public SimulationConfig createNextConfig() {
		figure++;
		switch(figure){
		default:
		case 8:
			return createChartConfig(new PriceConfig(true, EPrice.EXPSEARCH), 5000);
		case 9:
			return createChartConfig(new PriceConfig(true, EPrice.CONSTANTFACTOR), 5000);
		case 10:
			return createChartConfig(new PriceConfig(true, EPrice.CONSTANTPERCENTAGE), 5000);
		case 11:
			return createChartConfig(new PriceConfig(true, EPrice.RANDOMIZED), 5000);
		}
	}

	@Override
	public boolean shouldTryAgain() {
		return figure <= 11;
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
		Result resBenchmark = bm.runConstrainedOptimization(hint, 0.0001);
		table += "\nBenchmark\t" + resBenchmark.getRatio(bm.getPizza(), bm.getFondue()) + "\t" + resBenchmark.getAmount(bm.getPizza());
		return table;
	}
	
	public SimulationConfig createChartConfig(PriceConfig priceConfig, int rounds){
		StolperSamuelson ss = new StolperSamuelson(3.0);
		SimConfig config = ss.createConfiguration(priceConfig, rounds);
		for (int i = 0; i < StolperSamuelson.CONSUMERS_PER_TYPE * 2; i++) {
			ss.enableShock(config, 1200 + i, 3.0);
		}
		return config;
	}

	public String createChartData(PriceConfig priceConfig) {
		Simulation sim = new Simulation(createChartConfig(priceConfig, 5000));
		ChartData data = new ChartData(StolperSamuelson.PIZZA, StolperSamuelson.FONDUE, StolperSamuelson.IT_HOUR, StolperSamuelson.CH_HOUR);
		sim.addListener(data);
		sim.finish();
		return data.getTable();
	}

	public static void main(String[] args) {
		CompEconCharts charts = new CompEconCharts();
		System.out.println("\n***************** FIGURE 8 *****************");
		System.out.println(charts.createChartData(new PriceConfig(true, EPrice.EXPSEARCH)));
		System.out.println("\n***************** FIGURE 9 *****************");
		System.out.println(charts.createChartData(new PriceConfig(true, EPrice.CONSTANTFACTOR)));
		System.out.println("\n***************** ACCURACY BENCHMARK *****************");
		System.out.println(charts.createAccuracyBenchmark());
		System.out.println("\n***************** PARAMETER EXPLORATION *****************");
		System.out.println(new StolperSamuelsonParameterExploration(1.0, 5.0, 0.1).run());
	}

}
