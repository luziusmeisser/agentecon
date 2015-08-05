package com.agentecon.events;

import java.util.ArrayList;
import java.util.Random;

import com.agentecon.agent.Endowment;
import com.agentecon.firm.Firm;
import com.agentecon.firm.LogProdFun;
import com.agentecon.firm.RationalExpectationsFirm;
import com.agentecon.price.PriceFactory;
import com.agentecon.util.Average;
import com.agentecon.world.IWorld;

public class RationalFirmEvent extends EvolvingEvent {

	private Endowment end;
	private LogProdFun prodFun;
	private ArrayList<RationalExpectationsFirm> firms;

	public RationalFirmEvent(int firmsPerType, String type, Endowment end, LogProdFun fun, String sensor, String string2) {
		super(0, firmsPerType);
		this.end = end;
		this.prodFun = fun;
		this.firms = new ArrayList<>();
		for (int i = 0; i < getCardinality(); i++) {
			firms.add(new RationalExpectationsFirm(type, end, fun, new PriceFactory(new Random(3), sensor, string2))); // TEMP Random
		}
	}

	private RationalFirmEvent(int cardinality, Endowment end, LogProdFun prodFun, ArrayList<RationalExpectationsFirm> newFirms) {
		super(0, cardinality);
		this.end = end;
		this.prodFun = prodFun;
		this.firms = newFirms;
	}

	@Override
	public EvolvingEvent createNextGeneration() {
		ArrayList<RationalExpectationsFirm> newFirms = new ArrayList<>();
		for (RationalExpectationsFirm firm : firms) {
			newFirms.add(firm.createNextGeneration(end, prodFun));
		}
		return new RationalFirmEvent(getCardinality(), end, prodFun, newFirms);
	}

	@Override
	public double getScore() {
		Average avg = new Average();
		for (RationalExpectationsFirm firm : firms) {
			avg.add(firm.getOutputPrice());
		}
		return avg.getAverage();
	}

	@Override
	public void execute(IWorld sim) {
		for (Firm firm : firms) {
			sim.getFirms().add(firm);
		}
	}

	public String toString() {
		return "Firms with average price " + getScore();
	}

}
