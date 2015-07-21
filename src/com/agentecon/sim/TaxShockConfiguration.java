package com.agentecon.sim;

import java.util.ArrayList;
import java.util.Random;

import com.agentecon.agent.Endowment;
import com.agentecon.api.SimulationConfig;
import com.agentecon.consumer.LogUtil;
import com.agentecon.consumer.Weight;
import com.agentecon.events.ArbitrageurEvent;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.TaxEvent;
import com.agentecon.firm.LogProdFun;
import com.agentecon.good.Good;
import com.agentecon.good.Stock;
import com.agentecon.price.PriceFactory;
import com.agentecon.stats.Numbers;

public class TaxShockConfiguration {

	private int firmsPerType;
	private int consumersPerType;
	private int firmTypes;
	private int consumerTypes;
	private int seed;
	private double prevProfits;
	
	private Good[] inputs, outputs;

	private ArrayList<ArbitrageurEvent> events;

	public TaxShockConfiguration(int firmsPerType, int consumersPerType, int consumerTypes, int firmTypes, int seed) {
		this.firmsPerType = firmsPerType;
		this.consumersPerType = consumersPerType;
		this.consumerTypes = consumerTypes;
		this.firmTypes = firmTypes;
		this.seed = seed;
		this.prevProfits = -1.0;
		this.events = new ArrayList<>();
		
		this.inputs = new Good[consumerTypes];
		for (int i = 0; i < consumerTypes; i++) {
			inputs[i] = new Good("input " + i);
		}
		this.outputs = new Good[firmTypes];
		for (int i = 0; i < firmTypes; i++) {
			outputs[i] = new Good("output " + i);
		}
	}

	public SimulationConfig createNextConfig() {
		SimulationConfig config = new SimConfig(1000, seed);

		Weight[] defaultPrefs = createPrefs(outputs);
		for (int i = 0; i < consumerTypes; i++) {
			String name = "Consumer " + i;
			Endowment end = new Endowment(new Stock(inputs[i], Endowment.HOURS_PER_DAY));
			LogUtil util = new LogUtil(defaultPrefs, new Weight(inputs[i], 14));
			config.addEvent(new ConsumerEvent(consumersPerType, name, end, util));
		}
		Weight[] inputWeights = createInputWeights(inputs);
		for (int i = 0; i < firmTypes; i++) {
			Weight[] prodWeights = limit(rotate(inputWeights, i), 5);
			Endowment end = new Endowment(new Stock[] { new Stock(SimConfig.MONEY, 1000), new Stock(outputs[i], 10) }, new Stock[] {});
			LogProdFun fun = new LogProdFun(outputs[i], prodWeights);
			config.addEvent(new FirmEvent(firmsPerType, "Firm " + i, end, fun, new String[] { PriceFactory.SENSOR, "0.05" }));
		}
		ArrayList<ArbitrageurEvent> newList = new ArrayList<>();
		if (events.isEmpty()) {
			prevProfits = -1.0;
			for (Good g : outputs) {
				newList.add(new ArbitrageurEvent(g));
			}
		} else {
			prevProfits = getTradersProfit();
			for (ArbitrageurEvent ae: events){
				newList.add(ae.getNextGeneration());
			}
		}
		for (ArbitrageurEvent ae: newList){
			config.addEvent(ae);
		}
		events = newList;
				
		config.addEvent(new TaxEvent(config.getRounds() / 2, 0.20));

		return config;
	}
	
	public boolean shouldTryAgain(){
		return !Numbers.equals(getTradersProfit(), prevProfits);
	}
	
	public double getTradersProfit(){
		double tot = 0.0;
		for (ArbitrageurEvent ae: events){
			tot += ae.getProfits();
		}
		return tot;
	}
	
	public String getInv(){
		return events.iterator().next().getAgent().getInventory().toString();
	}

	private Weight[] limit(Weight[] rotate, int limit) {
		if (rotate.length > limit) {
			Weight[] inputs = new Weight[limit];
			System.arraycopy(rotate, 0, inputs, 0, limit);
			return inputs;
		} else {
			return rotate;
		}
	}

	private static Weight[] rotate(Weight[] productionWeights, int i) {
		int len = productionWeights.length;
		i = i % len;
		Weight[] rotated = new Weight[len];
		System.arraycopy(productionWeights, 0, rotated, len - i, i);
		System.arraycopy(productionWeights, i, rotated, 0, len - i);
		return rotated;
	}

	private static Weight[] createInputWeights(Good[] inputs) {
		Weight[] ws = new Weight[inputs.length];
		if (ws.length <= 3) {
			double[] defaults = new double[] { 6.0, 4.0, 8.0 };
			for (int i = 0; i < ws.length; i++) {
				ws[i] = new Weight(inputs[i], defaults[i]);
			}
		} else {
			Random rand = new Random(23);
			for (int i = 0; i < ws.length; i++) {
				double weight = rand.nextDouble() * 9 + 1;
				ws[i] = new Weight(inputs[i], weight);
			}
		}
		return ws;
	}

	private static Weight[] createPrefs(Good[] outputs) {
		Weight[] ws = new Weight[outputs.length];
		if (ws.length == 1) {
			ws[0] = new Weight(outputs[0], 8.0);
		} else if (ws.length == 2) {
			ws[0] = new Weight(outputs[0], 8.0);
			ws[1] = new Weight(outputs[1], 2.0);
		} else if (ws.length == 3) {
			ws[0] = new Weight(outputs[0], 3.0);
			ws[1] = new Weight(outputs[1], 2.0);
			ws[2] = new Weight(outputs[2], 5.0);
		} else {
			Random rand = new Random(17);
			for (int i = 0; i < ws.length; i++) {
				double weight = rand.nextDouble() * 9 + 1;
				ws[i] = new Weight(outputs[i], weight);
			}
		}
		return ws;
	}

}
