package com.agentecon.sim;

import java.util.ArrayList;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.Weight;
import com.agentecon.events.EvolvingEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.firm.LogProdFun;
import com.agentecon.firm.SensorFirm;
import com.agentecon.good.Stock;
import com.agentecon.price.PriceFactory;

public class SavingFirmConfiguration extends TaxShockConfiguration {

	private double amount;

	public SavingFirmConfiguration(int seed, double amount) {
		super(seed);
		this.amount = amount;
	}

	protected void addFirms(ArrayList<SimEvent> config, ArrayList<EvolvingEvent> newList, Weight[] inputWeights) {
		for (int i = 0; i < firmTypes; i++) {
			Weight[] prodWeights = limit(rotate(inputWeights, i), 5);
			Endowment end = new Endowment(new Stock[] { new Stock(SimConfig.MONEY, 1000), new Stock(outputs[i], 10) }, new Stock[] {});
			LogProdFun fun = new LogProdFun(outputs[i], prodWeights);
			config.add(new FirmEvent(firmsPerType, "Firm " + i, end, fun, new String[] { PriceFactory.SENSOR, "0.05" }) {
				protected SensorFirm createFirm(String type, Endowment end, LogProdFun prodFun, PriceFactory pf) {
					return new SavingFirm(type, end, prodFun, pf, amount);
				}
			});
		}
	}

}
