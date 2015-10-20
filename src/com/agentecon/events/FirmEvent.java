package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.firm.Producer;
import com.agentecon.firm.decisions.ExpectedRevenueBasedStrategy;
import com.agentecon.firm.decisions.DifferentialDividend;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.firm.production.CobbDouglasProduction;
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
		return createFirm(type, end, prodFun, pf, new ExpectedRevenueBasedStrategy(((CobbDouglasProduction)prodFun).getReturnsToScale()));
	}

	protected Producer createFirm(String type, Endowment end, IProductionFunction prodFun, PriceFactory pf, IFirmDecisions strategy) {
		if (priceParams.isSensor()) {
			return new SensorFirm(type, end, prodFun, pf, strategy);
		} else {
			return new Producer(type, end, prodFun, pf, strategy);
		}
	}

}
