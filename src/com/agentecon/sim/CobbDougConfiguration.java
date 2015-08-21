package com.agentecon.sim;

import java.util.ArrayList;

import com.agentecon.agent.Endowment;
import com.agentecon.api.SimulationConfig;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.EvolvingEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.Good;
import com.agentecon.good.Stock;
import com.agentecon.price.PriceFactory;

public class CobbDougConfiguration implements IConfiguration {

	public static final int ROUNDS = 5000;
	public static final int ITERATIONS = 0;

	private int iteration = 0;
	protected int firmsPerType;
	protected int consumersPerType;
	private int seed;

	protected Good[] inputs, outputs;

	protected ArrayList<SimEvent> constantEvents;
	protected ArrayList<EvolvingEvent> evolvingEvents;

	public CobbDougConfiguration(int seed) {
		this(3, 30, 1, 1, seed);
	}

	public CobbDougConfiguration(int firmsPerType, int consumersPerType, int consumerTypes, int firmTypes, int seed) {
		this.firmsPerType = firmsPerType;
		this.consumersPerType = consumersPerType;
		this.seed = seed;
		this.evolvingEvents = new ArrayList<>();
		this.constantEvents = new ArrayList<>();
		this.createGoods(consumerTypes, firmTypes);
		// PriceFactory.NORMALIZED_GOOD = outputs[0];
	}

	protected void createGoods(int consumerTypes, int firmTypes) {
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
		if (iteration > 0) {
			createGoods(inputs.length + 1, outputs.length + 1);
		}
		{
			constantEvents.clear();
			ArrayList<EvolvingEvent> evolvingEvents = iteration == 0 ? this.evolvingEvents : new ArrayList<EvolvingEvent>();
			addFirms(constantEvents, evolvingEvents, new ProductionWeights(inputs, outputs));
			addConsumers(constantEvents, evolvingEvents, new ConsumptionWeights(inputs, outputs));
		}

		// constantEvents.add(new TaxEvent(TAX_EVENT, 0.2));
		// constantEvents.add(new MoneyPrintEvent(1000, 1, 63));
		//
		// constantEvents.add(new MoneyPrintEvent(2000, 3, 20));
		// for (int i=1000; i<ROUNDS; i+=2000){
		// constantEvents.add(new MoneyPrintEvent(2000, 1, 1000));
		// }
		// for (int i=5000; i<10000; i+=250){
		// constantEvents.add(new MoneyPrintEvent(i, 100, 10));
		// }
		if (iteration > 0) {
			ArrayList<EvolvingEvent> newList = new ArrayList<>();
			for (EvolvingEvent ee : evolvingEvents) {
				newList.add(ee.createNextGeneration());
			}
			evolvingEvents = newList;
		}
		SimulationConfig config = createConfig(seed);
		for (SimEvent event : constantEvents)

		{
			config.addEvent(event);
		}
		for (

		SimEvent event : evolvingEvents)

		{
			config.addEvent(event);
		}
		iteration++;
		return config;
	}

	protected SimConfig createConfig(int seed) {
		return new SimConfig(ROUNDS, seed, ITERATIONS);
	}

	public String getComment() {
		String c = "";
		for (EvolvingEvent ee : evolvingEvents) {
			if (c.length() > 0) {
				c += "\n";
			}
			c += ee.toString();
		}
		return c;
	}

	protected void addConsumers(ArrayList<SimEvent> config, ArrayList<EvolvingEvent> newList, ConsumptionWeights defaultPrefs) {
		for (int i = 0; i < inputs.length; i++) {
			String name = "Consumer " + i;
			Endowment end = new Endowment(new Stock(inputs[i], Endowment.HOURS_PER_DAY));
			config.add(new ConsumerEvent(consumersPerType, name, end, defaultPrefs.getFactory(i)));
		}
	}

	protected void addFirms(ArrayList<SimEvent> config, ArrayList<EvolvingEvent> newList, ProductionWeights prod) {
		for (int i = 0; i < outputs.length; i++) {
			Endowment end = new Endowment(new Stock[] { new Stock(SimConfig.MONEY, 1000), new Stock(outputs[i], 10) }, new Stock[] {});
			IProductionFunction fun = prod.createUtilFun(i, 0.8);
			config.add(new FirmEvent(firmsPerType, "Firm " + i, end, fun, new String[] { PriceFactory.SENSOR, "0.05" }));
			// newList.add(new EvolvingFirmEvent(firmsPerType, "Firm " + i, end, fun, new Random(rand.nextLong()), PriceFactory.SENSOR, "0.05"));
		}
	}

	public boolean shouldTryAgain() {
		return iteration < 5;
	}

	public double getScore() {
		double tot = 0.0;
		for (EvolvingEvent ae : evolvingEvents) {
			tot += ae.getScore();
		}
		return tot;
	}

}
