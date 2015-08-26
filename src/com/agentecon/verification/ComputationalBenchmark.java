package com.agentecon.verification;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.IUtility;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.sim.ConsumptionWeights;
import com.agentecon.sim.ProductionWeights;
import com.agentecon.sim.SimConfig;
import com.agentecon.sim.Simulation;

public class ComputationalBenchmark {

	private static final int HOURS_PER_DAY = 24;
	private static final int CONSUMERS_PER_TYPE = 100;
	private static final int FIRMS_PER_TYPE = 10;

	private static final double RETURNS_TO_SCALE = 0.5;

	private Good[] inputs, outputs;
	private ProductionWeights prodWeights;
	private ConsumptionWeights consWeights;

	public ComputationalBenchmark(int size) {
		this.inputs = createGoods("input", size);
		this.outputs = createGoods("output", size);
		this.prodWeights = new ProductionWeights(inputs, outputs);
		this.consWeights = new ConsumptionWeights(inputs, outputs);
	}

	private Good[] createGoods(String string, int size) {
		Good[] goods = new Good[size];
		for (int i = 0; i < size; i++) {
			goods[i] = new Good(string + "_" + (i + 1));
		}
		return goods;
	}

	public Result runAgentBased() {
		SimConfig config = createConfiguration();
		Simulation sim = new Simulation(config);
		PriceMetric prices = new PriceMetric();
		sim.addListener(prices);
		sim.finish();
		prices.printResult(System.out);
		return prices.getResult();
	}
	
	public Result runConstrainedOptimization(Result hint){
		ConfigurableWorld world = new ConfigurableWorld(inputs, outputs, hint);
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

	public SimConfig createConfiguration() {
		SimConfig config = new SimConfig(1000, 23, 0);
		for (int i = 0; i < outputs.length; i++) {
			config.addEvent(new FirmEvent(FIRMS_PER_TYPE, "firm_" + i, new Endowment(new IStock[]{new Stock(SimConfig.MONEY, 1000)}, new IStock[]{}), prodWeights.createProdFun(i, RETURNS_TO_SCALE), "SENSOR"));
		}
		for (int i = 0; i < inputs.length; i++) {
			config.addEvent(new ConsumerEvent(CONSUMERS_PER_TYPE, "cons_" + i, new Endowment(new Stock(inputs[i], HOURS_PER_DAY)), consWeights.createUtilFun(i, 0)));
		}
		return config;
	}

	public static void main(String[] args) {
		ComputationalBenchmark bm = new ComputationalBenchmark(4);
		long t0 = System.nanoTime();
		Result res = bm.runAgentBased();
		long t1 = System.nanoTime();
		Result exact1 = bm.runConstrainedOptimization(res);
		long t2 = System.nanoTime();
		Result exact2 = bm.runConstrainedOptimization(null);
		long t3 = System.nanoTime();
		System.out.println("Agent-based took " + (t1 - t0) / 1000000 + "ms");
		System.out.println("Jacop with hints took " + (t2 - t1) / 1000000 + "ms");
		System.out.println("Jacop without hints took " + (t3 - t2) / 1000000 + "ms");
	}

}
