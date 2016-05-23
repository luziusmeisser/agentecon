package com.agentecon.verification;

import org.jacop.core.Store;
import org.jacop.floats.constraints.ExpPeqR;
import org.jacop.floats.constraints.LnPeqR;
import org.jacop.floats.constraints.PmulCeqR;
import org.jacop.floats.constraints.PmulQeqR;
import org.jacop.floats.core.FloatVar;

import com.agentecon.good.Good;

public class CobbDouglasFirm2 implements IFirm {

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

	public CobbDouglasFirm2(Store store, String type, Good good, double productivity, Good[] inputGoods, double[] inputWeights) {
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
		calcOutput(productivity);
		calcDividend(rawDividend, rawOutput);
		
		for (int i=0; i<inputs.length; i++){
			FloatVar right = new FloatVar(store, 0.0, Double.MAX_VALUE);
			store.impose(new PmulQeqR(inputPrices[i], rawInputs[i], right));
			store.impose(new PmulCeqR(rawOutput, weights[i], right));
		}
	}

	private void calcDividend(FloatVar rawDividend, FloatVar rawOutput) {
		double profitShare = 1.0;
		for (double w: weights){
			profitShare -= w;
		}
		store.impose(new PmulCeqR(rawOutput, profitShare, rawDividend));
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

//	public static void main(String[] args) {
//		Store store = new Store();
//		CobbDouglasFirm firm = new CobbDouglasFirm(store, "test", 2, 0.5);
//		FloatVar in = new FloatVar(store, 0.0, Double.MAX_VALUE);
//		store.impose(new PeqC(in, 2));
//		FloatVar out = new FloatVar(store, 0.0, Double.MAX_VALUE);
//		store.impose(new PeqC(out, 6));
//
//		firm.imposeConstraints(out, in);
//
//		DepthFirstSearch<FloatVar> search = new DepthFirstSearch<FloatVar>();
//		firm.all.addAll(Arrays.asList(firm.getOutput()));
//		firm.all.addAll(Arrays.asList(firm.getInputs()[0]));
//		SplitSelectFloat<FloatVar> s = new SplitSelectFloat<FloatVar>(store, firm.all.toArray(new FloatVar[] {}), null);
//
//		search.setSolutionListener(new PrintOutListener<FloatVar>());
//
//		search.getSolutionListener().searchAll(true);
//		search.labeling(store, s);
//		System.out.println("finished");
//	}

}
