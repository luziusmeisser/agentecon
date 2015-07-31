package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.firm.Firm;
import com.agentecon.firm.LogProdFun;
import com.agentecon.firm.SensorFirm;
import com.agentecon.price.PriceFactory;
import com.agentecon.world.IWorld;

public class FirmEvent extends SimEvent {

	private String type;
	protected Endowment end;
	protected LogProdFun prodFun;
	protected String[] priceParams;

	public FirmEvent(int card, String type, Endowment end, LogProdFun prodFun, String[] priceParams) {
		this(0, card, type, end, prodFun, priceParams);
	}

	public FirmEvent(int step, int card, String type, Endowment end, LogProdFun prodFun, String[] priceParams) {
		super(step, card);
		this.end = end;
		this.type = type;
		this.prodFun = prodFun;
		this.priceParams = priceParams;
	}

	@Override
	public void execute(IWorld sim) {
		PriceFactory pf = new PriceFactory(sim.getRand(), priceParams);
		for (int i = 0; i < getCardinality(); i++) {
			if (priceParams[0].equals(PriceFactory.SENSOR)) {
				SensorFirm firm = createFirm(type, end, prodFun, pf);
				sim.getFirms().add(firm);
			} else {
				sim.getFirms().add(new Firm(type, end, prodFun, pf));
			}
		}
	}

	protected SensorFirm createFirm(String type2, Endowment end2, LogProdFun prodFun2, PriceFactory pf) {
		return new SensorFirm(type, end, prodFun, pf);
	}

}
