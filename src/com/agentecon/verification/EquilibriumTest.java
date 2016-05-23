package com.agentecon.verification;

import java.util.Collection;

import com.agentecon.api.IFirm;
import com.agentecon.finance.IPublicCompany;
import com.agentecon.firm.Firm;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.metric.IFirmListener;
import com.agentecon.util.Average;

public class EquilibriumTest implements IFirmListener {
	
	private CobbDouglasProduction prodFun;
	private Average dividend;
	private Average[] inputs;
	
	public EquilibriumTest(String firmType, Collection<? extends IFirm> collection){
		this.dividend = new Average();
		for (IFirm f: collection){
			Firm firm = (Firm)f;
			if (firm.getType().equals(firmType)){
				firm.addFirmMonitor(this);
				if (prodFun == null){
					prodFun = (CobbDouglasProduction) firm.getProductionFunction();
					this.inputs = new Average[prodFun.getInput().length];
					for (int i=0; i<inputs.length; i++){
						this.inputs[i] = new Average();
					}
				}
			}
		}
	}
	
	public EquilibriumTest(int i, Collection<? extends IFirm> collection) {
		this("firm_" + i, collection);
	}

	@Override
	public void notifyProduced(IPublicCompany arg0, String arg1, IStock[] arg2, IStock arg3) {
		for (int i=0; i<inputs.length; i++){
			for (IStock s: arg2){
				if (s.getGood().equals(prodFun.getInput()[i])){
					inputs[i].add(s.getAmount());
				}
			}
		}
	}

	@Override
	public void reportDividend(IPublicCompany arg0, double arg1) {
		dividend.add(arg1);
	}

	@Override
	public void reportResults(IPublicCompany arg0, double arg1, double arg2, double arg3) {
	}

	public double getDeviation(Result res) {
		Good output = prodFun.getOutput();
		double production = res.getAmount(output) / StolperSamuelson.FIRMS_PER_TYPE;
		double revenue = res.getPrice(output) * production;
		double profit = dividend.getAverage();
		double idealProfit = (1.0 - prodFun.getTotalWeight()) * revenue;
		double deviation = Math.abs(profit - idealProfit) / idealProfit;
		for (int i=0; i<inputs.length; i++){
			Good input = prodFun.getInput()[i];
			double spent = inputs[i].getAverage() * res.getPrice(input);
			double ideal = prodFun.getWeight(input) * revenue;
			deviation += Math.abs(spent - ideal) / ideal;
		}
		return deviation / (1 + inputs.length);
	}

	public double getDividend() {
		return dividend.getAverage();
	}

}
