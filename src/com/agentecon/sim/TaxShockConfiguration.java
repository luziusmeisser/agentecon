package com.agentecon.sim;

import java.util.ArrayList;
import java.util.Random;

import com.agentecon.agent.Endowment;
import com.agentecon.api.SimulationConfig;
import com.agentecon.consumer.LogUtil;
import com.agentecon.consumer.Weight;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.EvolvingEvent;
import com.agentecon.events.EvolvingFirmEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.events.TaxEvent;
import com.agentecon.firm.LogProdFun;
import com.agentecon.good.Good;
import com.agentecon.good.Stock;
import com.agentecon.price.PriceFactory;

public class TaxShockConfiguration {

	public static final int ROUNDS = 1000;
	public static final int TAX_EVENT = ROUNDS / 2;
	
	private int iteration = 0;
	protected int firmsPerType;
	protected int consumersPerType;
	protected int firmTypes;
	protected int consumerTypes;
	private int seed;
	
	protected Good[] inputs, outputs;

	protected ArrayList<SimEvent> constantEvents;
	protected ArrayList<EvolvingEvent> evolvingEvents;
	
	public TaxShockConfiguration(int seed) {
		this(10, 100, 1, 1, seed);
	}

	public TaxShockConfiguration(int firmsPerType, int consumersPerType, int consumerTypes, int firmTypes, int seed) {
		this.firmsPerType = firmsPerType;
		this.consumersPerType = consumersPerType;
		this.consumerTypes = consumerTypes;
		this.firmTypes = firmTypes;
		this.seed = seed;
		this.evolvingEvents = new ArrayList<>();
		this.constantEvents = new ArrayList<>();
		
		this.inputs = new Good[consumerTypes];
		for (int i = 0; i < consumerTypes; i++) {
			inputs[i] = new Good("input " + i, 0.0);
		}
		this.outputs = new Good[firmTypes];
		for (int i = 0; i < firmTypes; i++) {
			outputs[i] = new Good("output " + i, SimConfig.GOODS_PERSISTENCE);
		}
	}

	public SimulationConfig createNextConfig() {
		if (iteration == 0){
			Weight[] inputWeights = createInputWeights(inputs, 1.0);
			addFirms(constantEvents, evolvingEvents, inputWeights);
			Weight[] defaultPrefs = createPrefs(outputs);
			addConsumers(constantEvents, evolvingEvents, defaultPrefs);
			
			constantEvents.add(new TaxEvent(TAX_EVENT, 0.2));
		} else {
			ArrayList<EvolvingEvent> newList = new ArrayList<>();
			for (EvolvingEvent ee: evolvingEvents){
				newList.add(ee.createNextGeneration());
			}
			evolvingEvents = newList;
		}
		SimulationConfig config = new SimConfig(1000, seed);
		for (SimEvent event: constantEvents){
			config.addEvent(event);
		}
		for (SimEvent event: evolvingEvents){
			config.addEvent(event);
		}
		iteration++;
		return config;
	}
	
	public String getComment() {
		String c = "";
		for (EvolvingEvent ee: evolvingEvents){
			if (c.length() > 0){
				c += "\n";
			}
			c += ee.toString();
		}
		return c;
	}
	
	protected void addConsumers(ArrayList<SimEvent> config, ArrayList<EvolvingEvent> newList, Weight[] defaultPrefs) {
		for (int i = 0; i < consumerTypes; i++) {
			String name = "Consumer " + i;
			Endowment end = new Endowment(new Stock(inputs[i], Endowment.HOURS_PER_DAY));
			LogUtil util = new LogUtil(defaultPrefs, new Weight(inputs[i], 14));
			config.add(new ConsumerEvent(consumersPerType, name, end, util));
		}
	}

	protected void addFirms(ArrayList<SimEvent> config, ArrayList<EvolvingEvent> newList, Weight[] inputWeights) {
		Random rand = new Random(seed);
		for (int i = 0; i < firmTypes; i++) {
			Weight[] prodWeights = limit(rotate(inputWeights, i), 5);
			Endowment end = new Endowment(new Stock[] { new Stock(SimConfig.MONEY, 1000), new Stock(outputs[i], 0) }, new Stock[] {});
			LogProdFun fun = new LogProdFun(outputs[i], prodWeights);
//			config.add(new FirmEvent(firmsPerType, "Firm " + i, end, fun, new String[] { PriceFactory.SENSOR, "0.01" }));
			newList.add(new EvolvingFirmEvent(firmsPerType, "Firm " + i, end, fun, new Random(rand.nextLong()), PriceFactory.RATIONAL, "0.05"));
		}
	}
	
	public boolean shouldTryAgain(){
		return iteration < 50 && evolvingEvents.size() > 0;
	}
	
	public double getScore(){
		double tot = 0.0;
		for (EvolvingEvent ae: evolvingEvents){
			tot += ae.getScore();
		}
		return tot;
	}
	
	protected Weight[] limit(Weight[] rotate, int limit) {
		if (rotate.length > limit) {
			Weight[] inputs = new Weight[limit];
			System.arraycopy(rotate, 0, inputs, 0, limit);
			return inputs;
		} else {
			return rotate;
		}
	}

	protected static Weight[] rotate(Weight[] productionWeights, int i) {
		int len = productionWeights.length;
		i = i % len;
		Weight[] rotated = new Weight[len];
		System.arraycopy(productionWeights, 0, rotated, len - i, i);
		System.arraycopy(productionWeights, i, rotated, 0, len - i);
		return rotated;
	}

	protected Weight[] createInputWeights(Good[] inputs, double multiplier) {
		Weight[] ws = new Weight[inputs.length];
		if (ws.length <= 3) {
			double[] defaults = new double[] { 6.0 * multiplier, 4.0 * multiplier, 8.0 * multiplier };
			for (int i = 0; i < ws.length; i++) {
				ws[i] = new Weight(inputs[i], defaults[i]);
			}
		} else {
			Random rand = new Random(23);
			for (int i = 0; i < ws.length; i++) {
				double weight = (rand.nextDouble() * 9 + 1) * multiplier;
				ws[i] = new Weight(inputs[i], weight);
			}
		}
		return ws;
	}

	private Weight[] createPrefs(Good[] outputs) {
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
