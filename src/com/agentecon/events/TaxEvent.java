package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.api.IFirm;
import com.agentecon.firm.Firm;
import com.agentecon.firm.LogProdFun;
import com.agentecon.firm.ProductionTax;
import com.agentecon.firm.SensorFirm;
import com.agentecon.price.PriceFactory;
import com.agentecon.world.IWorld;

public class TaxEvent extends SimEvent {

	private double rate;

	public TaxEvent(int when, double rate) {
		super(when, -1);
		this.rate = rate;
	}

	@Override
	public void execute(IWorld sim) {
		for (Firm firm: sim.getFirms().getRandomFirms(getCardinality())){
			firm.setProductionFunction(new ProductionTax(firm.getProductionFunction(), rate));
		}
	}

}
