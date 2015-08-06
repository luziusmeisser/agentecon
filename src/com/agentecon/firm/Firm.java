// Created by Luzius on Apr 28, 2014

package com.agentecon.firm;

import java.util.Arrays;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.api.IFirm;
import com.agentecon.consumer.Consumer;
import com.agentecon.finance.ShareRegister;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.metric.FirmListeners;
import com.agentecon.metric.IFirmListener;
import com.agentecon.price.IPriceFactory;

public class Firm extends Agent implements IFirm {

	public static double DIVIDEND_RATE = 0.2;

	// private ShareRegister register; clone?
	protected InputFactor[] inputs;
	protected OutputFactor output;
	private IProductionFunction prod;

	private FirmListeners monitor;

	protected IPriceFactory prices;

	public Firm(String type, Endowment end, IProductionFunction prod, IPriceFactory prices) {
		super(type, end);
		this.prod = prod;
		this.prices = prices;
		// this.register = new ShareRegister(getName(), getMoney());

		Good[] inputs = prod.getInput();
		this.inputs = new InputFactor[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			this.inputs[i] = createInputFactor(prices, getStock(inputs[i]));
		}
		IStock outStock = getStock(prod.getOutput());
		this.output = createOutputFactor(prices, outStock);
		this.monitor = new FirmListeners();
	}

	protected OutputFactor createOutputFactor(IPriceFactory prices, IStock outStock) {
		return new OutputFactor(outStock, prices.createPrice(outStock.getGood()));
	}

	protected InputFactor createInputFactor(IPriceFactory prices, IStock stock) {
		return new InputFactor(stock, prices.createPrice(stock.getGood()));
	}

	public void addFirmMonitor(IFirmListener prodmon) {
		this.monitor.add(prodmon);
	}

	public void offer(IPriceMakerMarket market) {
		double totWeight = getTotalInputWeight();
		double totSalaries = Math.min(getCostOfMaximumProfits(totWeight) + sumInputPrices(), calcSpendableWealth());
		double offerPerWeight = totSalaries / totWeight;
		if (!getMoney().isEmpty()) {
			for (InputFactor f : inputs) {
				if (f.isObtainable()) {
					double amount = offerPerWeight * prod.getWeight(f.getGood()) - f.getPrice();
					if (amount > 0) {
						f.createOffers(market, getMoney(), amount);
					} else {
						// so we find out about the true price even if we are not interested
						createSymbolicOffer(market, f);
					}
				} else {
					// in case it becomes available
					createSymbolicOffer(market, f);
				}
			}
		}
		output.createOffer(market, getMoney(), output.getStock().getAmount());
	}

	@Override
	public Firm clone() {
		Firm klon = (Firm) super.clone();
		klon.output = output.duplicate(klon.getStock(output.getGood()));
		klon.inputs = new InputFactor[inputs.length];
		for (int i=0; i<inputs.length; i++){
			klon.inputs[i] = inputs[i].duplicate(klon.getStock(inputs[i].getGood()));
		}
		return klon;
	}

	private double getTotalInputWeight() {
		double tot = 0.0;
		for (InputFactor in : inputs) {
			if (in.isObtainable()) {
				tot += prod.getWeight(in.getGood());
			}
		}
		return tot;
	}

	private void createSymbolicOffer(IPriceMakerMarket market, InputFactor f) {
		if (getMoney().getAmount() > 100) {
			f.createOffers(market, getMoney(), 1);
		}
	}

	public double getCostOfMaximumProfits(double totweight) {
		double outprice = output.getPrice();
		double inputPriceSum = sumInputPrices();
		double adjustmentDueToFirstFreeInputUnit = inputPriceSum;
		return outprice * totweight - adjustmentDueToFirstFreeInputUnit;
	}

	protected double sumInputPrices() {
		double inputPriceSum = 0.0;
		for (InputFactor input : inputs) {
			if (input.isObtainable()) {
				inputPriceSum += input.getPrice();
			}
		}
		return inputPriceSum;
	}

	private double calcSpendableWealth() {
		return getMoney().getAmount() / 4;
	}
	
	public void adaptPrices() {
		adaptInputPrices();
		adaptOutputPrice();
	}

	public void adaptInputPrices() {
		for (int i = 0; i < inputs.length; i++) {
			inputs[i].adaptPrice();
		}
	}

	public void adaptOutputPrice() {
		output.adaptPrice();
	}

	public boolean arePricesStable() {
		boolean stable = true;
		for (int i = 0; i < inputs.length; i++) {
			stable &= inputs[i].isStable();
		}
		return stable && output.isStable();
	}

	public double produce(int day) {
		IStock[] inputAmounts = new IStock[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			inputAmounts[i] = inputs[i].getStock().duplicate();
		}

		double produced = prod.produce(getInventory());
		monitor.notifyProduced(getType(), inputAmounts, new Stock(output.getGood(), produced));
		return produced;
	}

	public Good getGood() {
		return output.getGood();
	}

	public double payDividends(int day) {
		IStock wallet = getMoney();
		double dividend = calcCogsDividend(wallet);
		assert dividend >= 0;
		monitor.reportDividend(dividend);

		// register.payDividend(wallet, dividend);
		wallet.remove(dividend);
		return dividend;
	}

	private double calcRelativeDividend(IStock wallet) {
		return wallet.getAmount() * DIVIDEND_RATE;
	}

	private double calcConstDividend(IStock wallet) {
		return Math.max(0, wallet.getAmount() - 800);
	}

	private double calcCogsDividend(IStock wallet) {
		double cash = wallet.getAmount();
		double cogs = calcCogs();

		double profits = calcProfits();
		double dividend = Math.max(0, profits);
		if (cash - 3 * cogs < dividend) {
			// limits dividend
			dividend = Math.max(0, cash - 3 * cogs);
			// } else if (dividend < cash - 800){
			// // increases dividend
			// dividend = cash - 800;
		}
		return dividend;
	}

	public double calcProfits() {
		return output.getVolume() - calcCogs();
	}

	private double calcCogs() {
		double cogs = 0.0;
		for (InputFactor input : inputs) {
			cogs += input.getVolume();
		}
		return cogs;
	}

	@Override
	public String toString() {
		return "Firm with " + getMoney() + ", " + output + ", " + Arrays.toString(inputs);
	}

	public IProductionFunction getProductionFunction() {
		return prod;
	}

	public void setProductionFunction(IProductionFunction prodFun) {
		this.prod = prodFun;
	}

	public double getOutputPrice() {
		return output.getPrice();
	}

	public Firm createNextGeneration(Endowment end, IProductionFunction prod) {
		return new Firm(getType(), end, prod, prices);
	}

}
