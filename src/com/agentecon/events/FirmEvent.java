package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.firm.Producer;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.firm.sensor.SensorFirm;
import com.agentecon.price.PriceConfig;
import com.agentecon.price.PriceFactory;
import com.agentecon.world.IWorld;

public class FirmEvent extends SimEvent {

	private String type;
	protected Endowment end;
	protected IProductionFunction prodFun;
	protected PriceConfig priceParams;
	
	public FirmEvent(int card, String type, Endowment end, IProductionFunction prodFun) {
		this(card, type, end, prodFun, PriceConfig.DEFAULT);
	}

	public FirmEvent(int card, String type, Endowment end, IProductionFunction prodFun, PriceConfig priceParams) {
		super(0, card);
		this.end = end;
		this.type = type;
		this.prodFun = prodFun;
		this.priceParams = priceParams;
	}

	@Override
	public void execute(IWorld sim) {
		PriceFactory pf = new PriceFactory(sim.getRand(), priceParams);
		for (int i = 0; i < getCardinality(); i++) {
			sim.add(createFirm(type, end, prodFun, pf));
		}
	}

	protected Producer createFirm(String type, Endowment end, IProductionFunction prodFun, PriceFactory pf) {
		if (priceParams.isSensor()) {
			return new SensorFirm(type, end, prodFun, pf);
		} else {
			return new Producer(type, end, prodFun, pf);
		}
	}
	
	@Override
	public String toString(){
		return getCardinalityString() + " firms";
	}

}
