package com.agentecon.verification;

import java.util.HashMap;
import java.util.Map;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtil;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.UpdatePreferencesEvent;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.ConsumptionWeights;
import com.agentecon.sim.ProductionWeights;
import com.agentecon.sim.SimConfig;
import com.agentecon.sim.Simulation;

public class StolperSamuelson {

	private static final int HOURS_PER_DAY = 24;
	private static final int CONSUMERS_PER_TYPE = 100;
	private static final int FIRMS_PER_TYPE = 10;

	public static final double RETURNS_TO_SCALE = 0.5;

	private Good[] inputs, outputs;
	private ProductionWeights prodWeights;
	private ConsumptionWeights consWeights;

	public StolperSamuelson(double low) {
		this.inputs = new Good[] { new Good("Italian man-hours"), new Good("Swiss man-hours") };
		this.outputs = new Good[] { new Good("Pizza"), new Good("Fondue") };
		this.prodWeights = new ProductionWeights(inputs, outputs);
		this.consWeights = new ConsumptionWeights(inputs, outputs, HOURS_PER_DAY - ConsumptionWeights.TIME_WEIGHT - low, low);
		// PriceFactory.NORMALIZED_GOOD = outputs[0];
	}

	public Result runAgentBased(PriceConfig pconfig, int rounds) {
		System.out.println("Running agent-based simulation with " + pconfig);
		SimConfig config = createConfiguration(pconfig, rounds);
		Simulation sim = new Simulation(config);
		PriceMetric prices = new PriceMetric(rounds / 2);
		sim.addListener(prices);
		sim.finish();
//		prices.printResult(System.out);
		return prices.getResult();
	}

	public Result runConstrainedOptimization(Result hint, double accuracy) {
		ConfigurableWorld world = new ConfigurableWorld(inputs, outputs, hint, accuracy);
		for (int i = 0; i < outputs.length; i++) {
			IProductionFunction pf = prodWeights.createProdFun(i, RETURNS_TO_SCALE);
			world.addFirmType("firm_" + i, FIRMS_PER_TYPE, outputs[i], CobbDouglasProduction.PRODUCTIVITY, pf.getInput(), pf.getWeights());
		}
		for (int i = 0; i < inputs.length; i++) {
			IUtility util = consWeights.createUtilFun(i, 0);
			world.addConsumerType("cons_" + i, CONSUMERS_PER_TYPE, inputs[i], HOURS_PER_DAY, util.getGoods(), util.getWeights());
		}
		world.imposeConstraints();
		return world.solve();
	}

	public SimConfig createConfiguration(PriceConfig pricing, int rounds) {
		SimConfig config = new SimConfig(rounds, 25, 0);
		for (int i = 0; i < outputs.length; i++) {
			config.addEvent(new FirmEvent(FIRMS_PER_TYPE, "firm_" + i, new Endowment(new IStock[] { new Stock(SimConfig.MONEY, 1000) }, new IStock[] {}),
					prodWeights.createProdFun(i, RETURNS_TO_SCALE), pricing));
		}
		for (int i = 0; i < inputs.length; i++) {
			config.addEvent(new ConsumerEvent(CONSUMERS_PER_TYPE, "cons_" + i, new Endowment(new Stock(inputs[i], HOURS_PER_DAY)), consWeights.createUtilFun(i, 0)));
		}
		return config;
	}
	
	public SimConfig enableShock(SimConfig config, int day, final double pizzaPref){
		config.addEvent(new UpdatePreferencesEvent(day) {

			@Override
			protected void update(com.agentecon.consumer.Consumer c) {
				LogUtil util = (LogUtil) c.getUtilityFunction();
				util = consWeights.createDeviation(util, outputs[0], pizzaPref);
				util = consWeights.createDeviation(util, outputs[1], HOURS_PER_DAY - ConsumptionWeights.TIME_WEIGHT - pizzaPref);
				c.setUtilityFunction(util);
			}

		});
		return config;
	}

	public static void main(String[] args) throws InterruptedException {
		// for (int i = 1; i <= 10; i++) {
		// System.out.println("Going for size " + i);
		HashMap<String, Result> results = new HashMap<>();
		final StolperSamuelson bm = new StolperSamuelson(3.0);

		long t0 = System.nanoTime();
		for (PriceConfig config : PriceConfig.STANDARD_CONFIGS) {
			results.put(config.getName(), bm.runAgentBased(config, 10000));
		}
		long t1 = System.nanoTime();
		// bm.new Runner(new Runnable() {
		//
		// @Override
		// public void run() {
		// bm.runConstrainedOptimization(res);
		// }
		// }).waitForEnd(MAX_TIME);
		long t2 = System.nanoTime();
		// bm.new Runner(new Runnable() {
		//
		// @Override
		// public void run() {
		// bm.runConstrainedOptimization(null, 0.0000001);
		// }
		// }).waitForEnd(MAX_TIME);
		long t3 = System.nanoTime();
		System.out.println("Agent-based took " + (t1 - t0) / 1000000 + "ms");
		// System.out.println("Jacop with hints took " + (t2 - t1) / 1000000 + "ms");
		System.out.println("Jacop without hints took " + (t3 - t2) / 1000000 + "ms");
		// }

		for (Map.Entry<String, Result> e : results.entrySet()) {
			System.out.println(e.getKey() + "\t" + e.getValue().getPrice(bm.outputs[1]) / e.getValue().getPrice(bm.outputs[0]) + "\t" + e.getValue().getAmount(bm.outputs[0]));
		}
		double benchmark = 639.311;
		double actual = results.get("sensor prices with exponential adjustments").getAmount(bm.outputs[0]);
		System.out.println("Production error: " + (benchmark - actual) / benchmark);
	}

	public Good[] getInputs() {
		return outputs;
	}

	public Good getFondue() {
		return outputs[1];
	}

	public Good getPizza() {
		return outputs[0];
	}

	public Good getSwissHours() {
		return inputs[1];
	}

	public Good getItalianHours() {
		return inputs[0];
	}

}
