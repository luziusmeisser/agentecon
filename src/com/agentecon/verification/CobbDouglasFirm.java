package com.agentecon.verification;

import org.jacop.core.Store;
import org.jacop.floats.constraints.ExpPeqR;
import org.jacop.floats.constraints.LnPeqR;
import org.jacop.floats.constraints.PdivQeqR;
import org.jacop.floats.constraints.PeqC;
import org.jacop.floats.constraints.PminusQeqR;
import org.jacop.floats.constraints.PmulCeqR;
import org.jacop.floats.constraints.PmulQeqR;
import org.jacop.floats.constraints.PplusQeqR;
import org.jacop.floats.core.FloatVar;

import com.agentecon.good.Good;

public class CobbDouglasFirm implements IFirm {

	private Store store;

	private String type;
	private Good good;
	private Good[] inputGoods;
	private double[] weights;
	private double productivity;
	private FloatVar rawDividend;
	private FloatVar rawOutput;
	private FloatVar[] rawInputs;
	// private ArrayList<FloatVar> all = new ArrayList<FloatVar>();

	public CobbDouglasFirm(Store store, String type, Good good, double productivity, Good[] inputGoods, double[] inputWeights) {
		this.store = store;
		this.good = good;
		this.type = type;
		this.inputGoods = inputGoods;
		this.weights = inputWeights;
		this.productivity = productivity;

		this.rawDividend = new FloatVar(store, type + "_dividend", 0.0, Double.MAX_VALUE);
		this.rawOutput = new FloatVar(store, type + "_output", 0.0, Double.MAX_VALUE);

		this.rawInputs = new FloatVar[inputWeights.length];
		for (int i = 0; i < inputWeights.length; i++) {
			this.rawInputs[i] = new FloatVar(store, type + "-" + inputGoods[i].toString_(), 0.0, Double.MAX_VALUE);
		}
	}

	@Override
	public void imposeConstraints(Good outputs, FloatVar outputPrice, Good[] inputs, FloatVar... inputPrices) {
		assert outputs.equals(good);
		FloatVar factor = calcFactor(outputPrice, inputs, inputPrices, weights);
		for (int i = 0; i < inputGoods.length; i++) {
			imposeMax(factor, weights[i], rawInputs[i], find(inputGoods[i], inputs, inputPrices));
		}
		calcOutput(productivity);
		calcDividend(outputPrice, inputs, inputPrices);
	}

	private FloatVar calcFactor(FloatVar outputPrice, Good[] inputs, FloatVar[] inputPrices, double[] weights) {
		FloatVar mul = null;
		for (int i = 0; i < inputGoods.length; i++) {
			FloatVar weight = new FloatVar(store, 0.0, Double.MAX_VALUE);
			store.impose(new PeqC(weight, weights[i]));

			FloatVar temp = new FloatVar(store, 0.0, Double.MAX_VALUE);
			store.impose(new PdivQeqR(weight, find(inputGoods[i], inputs, inputPrices), temp));
			FloatVar pow = power(temp, weights[i]);
			if (mul == null) {
				mul = pow;
			} else {
				FloatVar t2 = new FloatVar(store, 0.0, Double.MAX_VALUE);
				store.impose(new PmulQeqR(mul, pow, t2));
				mul = t2;
			}
		}
		FloatVar mant = new FloatVar(store, 0.0, Double.MAX_VALUE);
		store.impose(new PmulQeqR(mul, outputPrice, mant));

		FloatVar tot = new FloatVar(store, 0.0, Double.MAX_VALUE);
		store.impose(new PmulCeqR(mant, productivity, tot));

		double exponent = 1.0 / (1.0 - sum(weights));
		FloatVar factor = power(tot, exponent);
		return factor;
	}

	private FloatVar find(Good good, Good[] inputs, FloatVar[] inputPrices) {
		for (int i=0; i<inputs.length; i++){
			if (inputs[i].equals(good)){
				return inputPrices[i];
			}
		}
		return null;
	}

	private double sum(double[] weights) {
		double s = 0.0;
		for (double w : weights) {
			s += w;
		}
		return s;
	}

	private void calcDividend(FloatVar outputPrice, Good[] inputs, FloatVar[] inputPrices) {
		FloatVar costs = null;
		for (int i = 0; i < inputGoods.length; i++) {
			FloatVar mul = new FloatVar(store, 0.0, Double.MAX_VALUE);
			store.impose(new PmulQeqR(find(inputGoods[i], inputs, inputPrices), rawInputs[i], mul));
			if (costs == null) {
				costs = mul;
			} else {
				FloatVar sum = new FloatVar(store, 0.0, Double.MAX_VALUE);
				store.impose(new PplusQeqR(costs, mul, sum));
				costs = sum;
			}
		}
		FloatVar revenue = new FloatVar(store, 0.0, Double.MAX_VALUE);
		store.impose(new PmulQeqR(outputPrice, rawOutput, revenue));
		store.impose(new PminusQeqR(revenue, costs, rawDividend));
	}

	private void calcOutput(double prod) {
		FloatVar output = null;
		for (int i = 0; i < rawInputs.length; i++) {
			FloatVar factor = power(rawInputs[i], weights[i]);
			if (output == null) {
				output = factor;
			} else {
				FloatVar mul = new FloatVar(store, 0.0, Double.MAX_VALUE);
				store.impose(new PmulQeqR(output, factor, mul));
				output = mul;
			}
		}
		store.impose(new PmulCeqR(output, prod, rawOutput));
	}

	private FloatVar power(FloatVar a, double exp) {
		FloatVar result = new FloatVar(store, 0, 100);
		FloatVar log = new FloatVar(store, -100, 100);
		store.impose(new LnPeqR(a, log));
		FloatVar mul = new FloatVar(store, -100, 100);
		store.impose(new PmulCeqR(log, exp, mul));
		store.impose(new ExpPeqR(mul, result));
		return result;
	}

	private void imposeMax(FloatVar factor, double weight, FloatVar amount, FloatVar inputPrice) {
		FloatVar right = new FloatVar(store, 0.0, Double.MAX_VALUE);
		store.impose(new PmulCeqR(factor, weight, right));
		store.impose(new PdivQeqR(right, inputPrice, amount));
	}

	public FloatVar getDividend() {
		return rawDividend;
	}

	public FloatVar getOutput() {
		return rawOutput;
	}

	@Override
	public Good[] getInputGoods() {
		return inputGoods;
	}

	@Override
	public FloatVar getInput(Good inputType) {
		for (int i = 0; i < inputGoods.length; i++) {
			if (inputGoods[i].equals(inputType)) {
				return rawInputs[i];
			}
		}
		return null;
	}

	public String getType() {
		return type;
	}

	@Override
	public Good getOutputGood() {
		return good;
	}

	// public static void main(String[] args) {
	// Store store = new Store();
	// CobbDouglasFirm firm = new CobbDouglasFirm(store, "test", 2, 0.5);
	// FloatVar in = new FloatVar(store, 0.0, Double.MAX_VALUE);
	// store.impose(new PeqC(in, 2));
	// FloatVar out = new FloatVar(store, 0.0, Double.MAX_VALUE);
	// store.impose(new PeqC(out, 6));
	//
	// firm.imposeConstraints(out, in);
	//
	// DepthFirstSearch<FloatVar> search = new DepthFirstSearch<FloatVar>();
	// firm.all.addAll(Arrays.asList(firm.getOutput()));
	// firm.all.addAll(Arrays.asList(firm.getInputs()[0]));
	// SplitSelectFloat<FloatVar> s = new SplitSelectFloat<FloatVar>(store, firm.all.toArray(new FloatVar[] {}), null);
	//
	// search.setSolutionListener(new PrintOutListener<FloatVar>());
	//
	// search.getSolutionListener().searchAll(true);
	// search.labeling(store, s);
	// System.out.println("finished");
	// }

}
