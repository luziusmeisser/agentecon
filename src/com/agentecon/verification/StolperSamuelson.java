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
import com.agentecon.sim.Simulation;
import com.agentecon.sim.config.ConsumptionWeights;
import com.agentecon.sim.config.ProductionWeights;
import com.agentecon.sim.config.SimConfig;

public class StolperSamuelson {

	protected static final int HOURS_PER_DAY = 24;
	protected static final int CONSUMERS_PER_TYPE = 100;
	protected static final int FIRMS_PER_TYPE = 10;

	protected static final double RETURNS_TO_SCALE = 0.5;
	protected static final double LOW = 3.0;
	protected static final double HIGH = HOURS_PER_DAY - ConsumptionWeights.TIME_WEIGHT - LOW;

	protected Good[] inputs, outputs;
	protected ProductionWeights prodWeights;
	protected ConsumptionWeights consWeights;

	public StolperSamuelson() {
		this(LOW);
	}

	public StolperSamuelson(double low) {
		this.inputs = new Good[] { new Good("Italian man-hours"), new Good("Swiss man-hours") };
		this.outputs = new Good[] { new Good("Pizza"), new Good("Fondue") };
		this.prodWeights = new ProductionWeights(inputs, outputs);
		this.consWeights = new ConsumptionWeights(inputs, outputs, HOURS_PER_DAY - ConsumptionWeights.TIME_WEIGHT - low, low);
		// PriceFactory.NORMALIZED_GOOD = outputs[0];
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

	public Result runAgentBased(PriceConfig pconfig, int rounds) {
		System.out.println("Running agent-based simulation with " + pconfig);
		SimConfig config = createConfiguration(pconfig, rounds);
		Simulation sim = new Simulation(config);
		PriceMetric prices = new PriceMetric(1000);
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
			IUtility util = consWeights.createUtilFun(i);
			world.addConsumerType("cons_" + i, CONSUMERS_PER_TYPE, inputs[i], HOURS_PER_DAY, util.getGoods(), util.getWeights());
		}
		world.imposeConstraints();
		return world.solve();
	}

	public SimConfig createConfiguration(PriceConfig pricing, int rounds) {
		return createConfiguration(pricing, 1, rounds);
	}

	public SimConfig createConfiguration(PriceConfig pricing, int scale, int rounds) {
		return createConfiguration(pricing, 0, scale, rounds);
	}

	public SimConfig createConfiguration(PriceConfig pricing, int wiggles, int scale, int rounds) {
		SimConfig config = new SimConfig(rounds, 25, wiggles);
		addFirms(pricing, scale, config);
		addConsumers(scale, config);
		addSpecialEvents(config);
		return config;
	}

	protected void addSpecialEvents(SimConfig config) {
		updatePrefs(config, 1000, LOW);
	}

	protected void updatePrefs(SimConfig config, int when, final double pizza) {
		config.addEvent(new UpdatePreferencesEvent(when) {

			@Override
			protected void update(com.agentecon.consumer.Consumer c) {
				LogUtil util = (LogUtil) c.getUtilityFunction();
				util = consWeights.createDeviation(util, outputs[0], pizza);
				util = consWeights.createDeviation(util, outputs[1], HIGH + LOW - pizza);
				c.setUtilityFunction(util);
			}

		});
	}

	protected void addConsumers(int scale, SimConfig config) {
		for (int i = 0; i < inputs.length; i++) {
			Endowment end = new Endowment(new Stock(inputs[i], HOURS_PER_DAY));
			config.addEvent(new ConsumerEvent(scale * CONSUMERS_PER_TYPE, "cons_" + i, end, consWeights.createUtilFun(i)));
		}
	}

	protected void addFirms(PriceConfig pricing, int scale, SimConfig config) {
		for (int i = 0; i < outputs.length; i++) {
			Endowment end = new Endowment(new IStock[] { new Stock(SimConfig.MONEY, 1000) }, new IStock[] {});
			IProductionFunction prodfun = prodWeights.createProdFun(i, RETURNS_TO_SCALE);
			config.addEvent(new FirmEvent(scale * FIRMS_PER_TYPE, "firm_" + i, end, prodfun, pricing));
		}
	}

	public static void main(String[] args) throws InterruptedException {
		// for (int i = 1; i <= 10; i++) {
		// System.out.println("Going for size " + i);
		HashMap<String, Result> results = new HashMap<>();
		final StolperSamuelson bm = new StolperSamuelson();

		long t0 = System.nanoTime();
		// PriceConfig config = PriceConfig.STANDARD_CONFIGS[7];
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

	public Good[] getOutputs() {
		return outputs;
	}

	public Good[] getInputs() {
		return outputs;
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
