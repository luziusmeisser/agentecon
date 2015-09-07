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
import com.agentecon.price.PriceFactory;
import com.agentecon.sim.ConsumptionWeights;
import com.agentecon.sim.ProductionWeights;
import com.agentecon.sim.SimConfig;
import com.agentecon.sim.Simulation;

public class StolperSamuelson {

	private static final int HOURS_PER_DAY = 24;
	private static final int CONSUMERS_PER_TYPE = 100;
	private static final int FIRMS_PER_TYPE = 10;

	private static final double RETURNS_TO_SCALE = 0.5;
	private static final double LOW = 3.0;
	private static final double HIGH = HOURS_PER_DAY - ConsumptionWeights.TIME_WEIGHT - LOW;
	private static final int ROUNDS = 3000;

	private Good[] inputs, outputs;
	private ProductionWeights prodWeights;
	private ConsumptionWeights consWeights;

	public StolperSamuelson() {
		this.inputs = new Good[]{new Good("Italian man-hours"), new Good("Swiss man-hours")};
		this.outputs = new Good[]{new Good("Pizza"), new Good("Fondue")};
		this.prodWeights = new ProductionWeights(inputs, outputs);
		this.consWeights = new ConsumptionWeights(inputs, outputs, HIGH, LOW);
//		PriceFactory.NORMALIZED_GOOD = outputs[0];
	}

	public StolperSamuelson(int size) {
		this.inputs = createGoods("input", size);
		this.outputs = createGoods("output", size);
		this.prodWeights = new ProductionWeights(inputs, outputs);
		this.consWeights = new ConsumptionWeights(inputs, outputs, HIGH, LOW);
	}

	private Good[] createGoods(String string, int size) {
		Good[] goods = new Good[size];
		for (int i = 0; i < size; i++) {
			goods[i] = new Good(string + "_" + (i + 1));
		}
		return goods;
	}

	public Result runAgentBased(PriceConfig pconfig) {
		SimConfig config = createConfiguration(pconfig);
		Simulation sim = new Simulation(config);
		PriceMetric prices = new PriceMetric(config.getRounds() / 5);
		sim.addListener(prices);
		sim.finish();
		prices.printResult(System.out);
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
		world.solve();
		return null;
	}

	public SimConfig createConfiguration(PriceConfig pricing) {
		SimConfig config = new SimConfig(ROUNDS, 25, 0);
		for (int i = 0; i < outputs.length; i++) {
			config.addEvent(new FirmEvent(FIRMS_PER_TYPE, "firm_" + i, new Endowment(new IStock[] { new Stock(SimConfig.MONEY, 1000) }, new IStock[] {}),
					prodWeights.createProdFun(i, RETURNS_TO_SCALE),pricing));
		}
		for (int i = 0; i < inputs.length; i++) {
			config.addEvent(new ConsumerEvent(CONSUMERS_PER_TYPE, "cons_" + i, new Endowment(new Stock(inputs[i], HOURS_PER_DAY)), consWeights.createUtilFun(i, 0)));
		}
		config.addEvent(new UpdatePreferencesEvent(1000) {

			@Override
			protected void update(com.agentecon.consumer.Consumer c) {
				LogUtil util = (LogUtil) c.getUtilityFunction();
				util = consWeights.createDeviation(util, outputs[0], LOW);
				util = consWeights.createDeviation(util, outputs[1], HIGH);
				c.setUtilityFunction(util);
			}

		});
		return config;
	}

	public static void main(String[] args) throws InterruptedException {
		// for (int i = 1; i <= 10; i++) {
		// System.out.println("Going for size " + i);
		HashMap<String, Result> results = new HashMap<>();
		final StolperSamuelson bm = new StolperSamuelson();
		
		long t0 = System.nanoTime();
		for (PriceConfig config: PriceConfig.STANDARD_CONFIGS){
			results.put(config.getName(), bm.runAgentBased(config));
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
		bm.runConstrainedOptimization(null, 0.000001);
		// }
		// }).waitForEnd(MAX_TIME);
		long t3 = System.nanoTime();
		System.out.println("Agent-based took " + (t1 - t0) / 1000000 + "ms");
		// System.out.println("Jacop with hints took " + (t2 - t1) / 1000000 + "ms");
		System.out.println("Jacop without hints took " + (t3 - t2) / 1000000 + "ms");
		// }
		
		for (Map.Entry<String, Result> e: results.entrySet()){
			System.out.println(e.getKey() + "\t" + e.getValue().getPrice(bm.outputs[1]) / e.getValue().getPrice(bm.outputs[0]) + "\t" + e.getValue().getAmount(bm.outputs[0]));
		}
	}

	// class Runner extends Thread {
	//
	// private Runnable r;
	//
	// public Runner(Runnable r) {
	// this.r = r;
	// this.start();
	// }
	//
	// public void run() {
	// r.run();
	// }
	//
	// public boolean waitForEnd(long patience) throws InterruptedException {
	// long end = System.currentTimeMillis() + patience;
	// while (System.currentTimeMillis() < end && isAlive()) {
	// Thread.sleep(1000);
	// }
	// if (isAlive()) {
	// this.stop();
	// System.out.println("Had to abort task");
	// return false;
	// } else {
	// return true;
	// }
	// }
	//
	// }

}
