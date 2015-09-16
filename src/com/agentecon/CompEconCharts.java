package com.agentecon;

import com.agentecon.price.EPrice;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.IConfiguration;
import com.agentecon.sim.SimConfig;
import com.agentecon.sim.Simulation;
import com.agentecon.verification.Result;
import com.agentecon.verification.StolperSamuelson;
import com.agentecon.verification.StolperSamuelsonParameterExploration;

public class CompEconCharts {

	public String createAccuracyBenchmark(){
		String table = "Method\tp_fondue / p_pizza\tx_pizza";
		final StolperSamuelson bm = new StolperSamuelson(3.0);
		Result hint = null;
		for (PriceConfig config : PriceConfig.STANDARD_CONFIGS) {
			if (config.isSensor()){
				Result res = bm.runAgentBased(config, 2000);
				table += "\n" + config.getName() + "\t" + res.getRatio(bm.getPizza(), bm.getFondue()) + "\t" + res.getAmount(bm.getPizza());
				hint = res;
			}
		}
//		Result resBenchmark = bm.runConstrainedOptimization(hint, 0.00001);
//		table += "\nBenchmark\t" + resBenchmark.getRatio(bm.getPizza(), bm.getFondue()) + "\t" + resBenchmark.getAmount(bm.getPizza());
		return table;
	}
	
	public String createChartData(){
		StolperSamuelson ss = new StolperSamuelson(3.0);
		SimConfig config = ss.createConfiguration(new PriceConfig(true, EPrice.EXPSEARCH), 2000);
		ss.enableShock(config, 1000, 3.0);
		Simulation sim = new Simulation(config);
		ChartData data = new ChartData(ss.getPizza(), ss.getFondue(), ss.getItalianHours(), ss.getSwissHours());
		sim.addListener(data);
		sim.finish();
		return data.getTable();
	}
	
	public static void main(String[] args) {
		CompEconCharts charts = new CompEconCharts();
		System.out.println("\n***************** FIGURE 8 *****************");
		System.out.println(charts.createChartData());
		System.out.println("\n***************** ACCURACY BENCHMARK *****************");
		System.out.println(charts.createAccuracyBenchmark());
		System.out.println("\n***************** PARAMETER EXPLORATION *****************");
		System.out.println(new StolperSamuelsonParameterExploration(1.0, 5.0, 0.1).run());
	}
}
