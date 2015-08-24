package com.agentecon.verification;

import org.jacop.core.Store;
import org.jacop.floats.constraints.PeqC;
import org.jacop.floats.constraints.PeqQ;
import org.jacop.floats.constraints.PmulQeqR;
import org.jacop.floats.constraints.PplusCeqR;
import org.jacop.floats.constraints.PplusQeqR;
import org.jacop.floats.core.FloatVar;

import com.agentecon.good.Good;

public class Consumer implements IConsumer {

	private Store store;
	private String type;

	private Good timeType;
	private double[] weights;
	private Good[] goods;
	private FloatVar[] inputs;
	private FloatVar work;
	private FloatVar leisure;
	private double leisureWeight;

	public Consumer(Store store, String type, double hoursPerDay, Good timeType, Good[] goods, double... inputWeights) {
		this.type = type;
		this.store = store;
		this.timeType = timeType;
		this.leisureWeight = find(timeType, goods, inputWeights);
		this.inputs = new FloatVar[goods.length - 1];
		this.goods = new Good[goods.length - 1];
		this.weights = new double[goods.length - 1];
		int pos = 0;
		for (int i = 0; i < inputs.length; i++) {
			if (!goods[i].equals(timeType)){
				this.inputs[pos] = new FloatVar(store, 0.0, Double.MAX_VALUE);
				this.goods[pos] = goods[i];
				this.weights[pos] = inputWeights[i];
				pos++;
			}
		}
		this.work = new FloatVar(store, type + "_work", 0.0, hoursPerDay);
		this.leisure = new FloatVar(store, timeType.toString(), 0.0, hoursPerDay);

		FloatVar sum = new FloatVar(store, 0.0, Double.MAX_VALUE);
		store.impose(new PplusQeqR(work, leisure, sum));
		store.impose(new PeqC(sum, hoursPerDay));
	}

	static boolean first = true;

	@Override
	public void imposeConstraints(FloatVar dividend, Good input, FloatVar wage, Good[] allGoods, FloatVar... goodsPrices) {
		assert input.equals(this.timeType);
		if (first) {
			first = false;
		} else {
			imposeBudget(dividend, wage, allGoods, goodsPrices);
		}
		FloatVar lambda = new FloatVar(store, 0.0, Double.MAX_VALUE);
		imposeLabor(lambda, wage);
		for (int i = 0; i < inputs.length; i++) {
			imposeInput(lambda, inputs[i], find(goods[i], allGoods, goodsPrices), weights[i]);
		}
	}
	
	private FloatVar find(Good good, Good[] inputs, FloatVar[] inputPrices) {
		for (int i=0; i<inputs.length; i++){
			if (inputs[i].equals(good)){
				return inputPrices[i];
			}
		}
		return null;
	}
	
	private double find(Good good, Good[] inputs, double[] inputPrices) {
		for (int i=0; i<inputs.length; i++){
			if (inputs[i].equals(good)){
				return inputPrices[i];
			}
		}
		assert false;
		return 0.0;
	}	

	private void imposeInput(FloatVar lambda, FloatVar input, FloatVar price, double weight) {
		FloatVar oneup = new FloatVar(store, 0.0, Double.MAX_VALUE);
		store.impose(new PplusCeqR(input, 1.0, oneup));

		FloatVar temp1 = new FloatVar(store, 0.0, Double.MAX_VALUE);
		store.impose(new PmulQeqR(oneup, price, temp1));
		FloatVar left = new FloatVar(store, 0.0, Double.MAX_VALUE);
		store.impose(new PmulQeqR(lambda, temp1, left));
		store.impose(new PeqC(left, weight));
	}

	private void imposeLabor(FloatVar lambda, FloatVar wage) {
		FloatVar oneup = new FloatVar(store, 0.0, Double.MAX_VALUE);
		store.impose(new PplusCeqR(leisure, 1.0, oneup));

		FloatVar temp1 = new FloatVar(store, 0.0, Double.MAX_VALUE);
		store.impose(new PmulQeqR(wage, oneup, temp1));
		FloatVar left = new FloatVar(store, 0.0, Double.MAX_VALUE);
		store.impose(new PmulQeqR(lambda, temp1, left));
		store.impose(new PeqC(left, leisureWeight));
	}

	private void imposeBudget(FloatVar dividend, FloatVar wage, Good[] allGoods, FloatVar[] goodsPrices) {
		FloatVar income = new FloatVar(store, 0.0, Double.MAX_VALUE);
		FloatVar salary = new FloatVar(store, 0.0, Double.MAX_VALUE);
		store.impose(new PmulQeqR(work, wage, salary));
		store.impose(new PplusQeqR(dividend, salary, income));

		FloatVar costs = null;
		for (int i = 0; i < goods.length; i++) {
			FloatVar mul = new FloatVar(store, 0.0, Double.MAX_VALUE);
			store.impose(new PmulQeqR(find(goods[i], allGoods, goodsPrices), inputs[i], mul));
			if (costs == null) {
				costs = mul;
			} else {
				FloatVar sum = new FloatVar(store, 0.0, Double.MAX_VALUE);
				store.impose(new PplusQeqR(costs, mul, sum));
				costs = sum;
			}
		}
		store.impose(new PeqQ(income, costs));
	}

	@Override
	public FloatVar getWorkHours() {
		return work;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Good getWorkType() {
		return timeType;
	}

	@Override
	public FloatVar getConsumption(Good good) {
		for (int i = 0; i < goods.length; i++) {
			if (goods[i].equals(good)) {
				return inputs[i];
			}
		}
		return null;
	}
	
	@Override
	public Good[] getInputs() {
		return goods;
	}

}
