// Created by Luzius on Apr 28, 2014

package com.agentecon.firm;

import java.util.Arrays;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.api.IFirm;
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

	private ShareRegister register;
	private InputFactor[] inputs;
	private OutputFactor output;
	private IProductionFunction prod;

	private FirmListeners monitor;

	public Firm(String type, Endowment end, IProductionFunction prod, IPriceFactory prices) {
		super(type, end);
		this.prod = prod;
		this.register = new ShareRegister(getName(), getMoney());

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
					}
				} else {
					// in case it becomes available
					createSymbolicOffer(market, f);
				}
			}
		}
		output.createOffer(market, getMoney(), output.getStock().getAmount());
	}

	private double getTotalInputWeight() {
		double tot = 0.0;
		for (InputFactor in: inputs){
			if (in.isObtainable()){
				tot += prod.getWeight(in.getGood());
			}
		}
		return tot;
	}

	private void createSymbolicOffer(IPriceMakerMarket market, InputFactor f) {
		if (getMoney().getAmount() > 100){
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

	public double produce() {
		IStock[] inputAmounts = new IStock[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			inputs[i].adaptPrice();
			inputAmounts[i] = inputs[i].getStock().duplicate();
		}
		output.adaptPrice();

		double produced = prod.produce(getInventory());
		monitor.notifyProduced(getType(), inputAmounts, new Stock(output.getGood(), produced));
		return produced;
	}

	public Good getGood() {
		return output.getGood();
	}

	public double payDividends(int day) {
		IStock wallet = getMoney();
		double cash = wallet.getAmount();
		double cogs = getCostOfMaximumProfits(getTotalInputWeight());
		
		double profits = calcProfits();
		double dividend = Math.max(0, profits);
//		if (cash - dividend < 3*cogs){
//			dividend = Math.max(0, cash - 3*cogs);
//		} else if (dividend < cash - 800){
//			dividend = cash - 800;
//		}
		assert dividend >= 0;
		monitor.reportDividend(dividend);
		
//		register.payDividend(wallet, dividend);
		wallet.remove(dividend);
		return dividend;
	}

	public double calcProfits() {
		double profits = output.getVolume();
		for (InputFactor input : inputs) {
			profits -= input.getVolume();
		}
		return profits;
	}

	@Override
	public String toString() {
		return "Firm with " + getMoney() + ", " + output + ", " + Arrays.toString(inputs);
	}

}
