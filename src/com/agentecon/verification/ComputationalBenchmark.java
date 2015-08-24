package com.agentecon.verification;

import com.agentecon.agent.Endowment;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.sim.ConsumptionWeights;
import com.agentecon.sim.ProductionWeights;
import com.agentecon.sim.SimConfig;
import com.agentecon.sim.Simulation;

public class ComputationalBenchmark {

	private static final int CONSUMERS_PER_TYPE = 100;
	private static final int FIRMS_PER_TYPE = 10;

	private static final double RETURNS_TO_SCALE = 0.8;

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

	public void run() {
		SimConfig config = createConfiguration();
		Simulation sim = new Simulation(config);
		PriceMetric prices = new PriceMetric();
		sim.addListener(prices);
		while (!sim.isFinished()){
			sim.forward(100);
		}
		prices.printResult(System.out);
	}

	public SimConfig createConfiguration() {
		SimConfig config = new SimConfig(10000);
		for (int i = 0; i < outputs.length; i++) {
			config.addEvent(new FirmEvent(FIRMS_PER_TYPE, "firm_" + i, new Endowment(new IStock[]{new Stock(SimConfig.MONEY, 1000)}, new IStock[]{}), prodWeights.createProdFun(i, RETURNS_TO_SCALE), "SENSOR"));
		}
		for (int i = 0; i < inputs.length; i++) {
			config.addEvent(new ConsumerEvent(CONSUMERS_PER_TYPE, "cons_" + i, new Endowment(new Stock(inputs[i], 24)), consWeights.createUtilFun(i, 0)));
		}
		return config;
	}

	public static void main(String[] args) {
		new ComputationalBenchmark(1).run();
	}

}
