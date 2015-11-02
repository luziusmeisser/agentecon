package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.firm.Producer;
import com.agentecon.firm.decisions.CogsDividend;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.firm.sensor.SensorFirm;
import com.agentecon.world.IWorld;

public class FirmEvent extends SimEvent {

	private String type;
	protected Endowment end;
	protected IProductionFunction prodFun;

	public FirmEvent(int card, String type, Endowment end, IProductionFunction prodFun) {
		super(0, card);
		this.end = end;
		this.type = type;
		this.prodFun = prodFun;
	}

	@Override
	public void execute(IWorld sim) {
		for (int i = 0; i < getCardinality(); i++) {
			sim.add(createFirm(type, end, prodFun));
		}
	}

	protected Producer createFirm(String type, Endowment end, IProductionFunction prodFun) {
		return new SensorFirm(type, end, prodFun, new CogsDividend(((CobbDouglasProduction) prodFun).getReturnsToScale()));
	}

}
