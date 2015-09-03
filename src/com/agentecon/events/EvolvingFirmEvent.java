package com.agentecon.events;

import java.util.ArrayList;
import java.util.Random;

import com.agentecon.agent.Endowment;
import com.agentecon.firm.Firm;
import com.agentecon.firm.production.LogProdFun;
import com.agentecon.firm.sensor.SensorFirm;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.price.PriceConfig;
import com.agentecon.price.PriceFactory;
import com.agentecon.util.Average;
import com.agentecon.world.IWorld;

public class EvolvingFirmEvent extends EvolvingEvent {

	private Endowment end;
	private FirstDayProduction prod;
	private LogProdFun prodFun;
	private ArrayList<Firm> firms;

	public EvolvingFirmEvent(int firmsPerType, String type, Endowment end, LogProdFun fun, Random rand, PriceConfig config) {
		super(0, firmsPerType);
		this.end = end;
		this.prodFun = fun;
		this.firms = new ArrayList<>();
		for (int i = 0; i < getCardinality(); i++) {
			firms.add(new SensorFirm(type, end, fun, new PriceFactory(rand, config)));
		}
		initListener();
	}

	private void initListener() {
		this.prod = new FirstDayProduction(firms.size());
		for (Firm firm : firms) {
			firm.addFirmMonitor(prod);
		}
	}

	private EvolvingFirmEvent(int cardinality, Endowment end, LogProdFun prodFun, ArrayList<Firm> firms) {
		super(0, cardinality);
		this.end = end;
		this.prodFun = prodFun;
		this.firms = firms;
		initListener();

	}

	@Override
	public EvolvingEvent createNextGeneration() {
		ArrayList<Firm> newFirms = new ArrayList<>();
		adaptEndowment();
		for (Firm firm : firms) {
			newFirms.add(firm.createNextGeneration(end, prodFun));
		}
		return new EvolvingFirmEvent(getCardinality(), end, prodFun, newFirms);
	}

	private void adaptEndowment() {
		Inventory inv = end.getInitialInventory();
		IStock stock = inv.getStock(prod.getGood());
		double diff = prod.getAmount() - stock.getAmount();
		if (diff > 0) {
			stock.add(diff);
		} else {
			stock.remove(-diff);
		}
		end = new Endowment(inv.getAll().toArray(new IStock[]{}), end.getDaily());
	}

	@Override
	public double getScore() {
		Average avg = new Average();
		for (Firm firm : firms) {
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
