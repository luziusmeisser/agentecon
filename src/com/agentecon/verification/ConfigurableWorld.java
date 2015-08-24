package com.agentecon.verification;

import java.util.ArrayList;
import java.util.Arrays;

import org.jacop.core.Store;
import org.jacop.floats.constraints.PeqC;
import org.jacop.floats.constraints.PeqQ;
import org.jacop.floats.constraints.PmulCeqR;
import org.jacop.floats.constraints.PplusQeqR;
import org.jacop.floats.core.FloatVar;
import org.jacop.floats.search.SplitSelectFloat;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.PrintOutListener;

import com.agentecon.good.Good;

public class ConfigurableWorld {

	protected Store store;
	protected ArrayList<IFirm> firms;
	private ArrayList<IConsumer> consumers;

	private FloatVar dividend;
	private Good[] inputs, outputs;
	private FloatVar[] inputPrices, outputPrices;

	public ConfigurableWorld(Good[] inputs, Good[] outputs) {
		this.store = new Store();
		this.inputs = inputs;
		this.outputs = outputs;
		this.consumers = new ArrayList<IConsumer>();
		this.firms = new ArrayList<IFirm>();

		this.inputPrices = new FloatVar[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			this.inputPrices[i] = new FloatVar(store, "wage_" + inputs[i], 0.0, Double.MAX_VALUE);
		}
		this.outputPrices = new FloatVar[outputs.length];
		for (int i = 0; i < outputs.length; i++) {
			this.outputPrices[i] = new FloatVar(store, "price_" + outputs[i], 0.0, Double.MAX_VALUE);
		}
		this.dividend = new FloatVar(store, "dividends", 0.0, Double.MAX_VALUE);

		System.out.println(store.variablesHashMap.size() + " variables");
	}

	public void imposeConstraints() {
		store.impose(new PeqC(outputPrices[0], 1.0)); // normalize a price to 1

		for (int i = 0; i < firms.size(); i++) {
			firms.get(i).imposeConstraints(outputs[i], outputPrices[i], inputs, inputPrices);
		}

		FloatVar dividendFraction = new FloatVar(store, "dividends_per_consumer", 0.0, Double.MAX_VALUE);
		store.impose(new PmulCeqR(dividendFraction, consumers.size(), dividend));
		for (int i = 0; i < consumers.size(); i++) {
			consumers.get(i).imposeConstraints(dividendFraction, inputs[i], inputPrices[i], outputs, outputPrices);
		}

		wireLabor();
		wireOutputsToConsumption();
		wireDividend();
	}

	private void wireLabor() {
		for (int i = 0; i < consumers.size(); i++) {
			final Good inputType = consumers.get(i).getWorkType();
			FloatVar sum = sum(firms, new IVarSelector<IFirm>() {

				@Override
				public FloatVar extract(IFirm con) {
					return con.getInput(inputType);
				}

			});
			store.impose(new PeqQ(sum, consumers.get(i).getWorkHours()));
		}
	}

	public FloatVar getOutput(int i) {
		return firms.get(i).getOutput();
	}

	private void wireDividend() {
		store.impose(new PeqQ(dividend, sum(firms, new IVarSelector<IFirm>() {

			@Override
			public FloatVar extract(IFirm con) {
				return con.getDividend();
			}
		})));
	}

	private void wireOutputsToConsumption() {
		for (int i = 0; i < firms.size(); i++) {
			final Good outputType = firms.get(i).getOutputGood();
			FloatVar sum = sum(consumers, new IVarSelector<IConsumer>() {

				@Override
				public FloatVar extract(IConsumer con) {
					return con.getConsumption(outputType);
				}

			});
			store.impose(new PeqQ(sum, firms.get(i).getOutput()));
		}
	}
	
	private int count = 0;

	private <T> FloatVar sum(ArrayList<T> list, IVarSelector<T> selector) {
		FloatVar sum = null;
		for (T con : list) {
			FloatVar var = selector.extract(con);
			if (var != null) {
				if (sum == null) {
					sum = var;
				} else {
					FloatVar newsum = new FloatVar(store, "tempsum" + count++, 0.0, Double.MAX_VALUE);
					store.impose(new PplusQeqR(var, sum, newsum));
					sum = newsum;
				}
			}
		}
		return sum;
	}

	public void addConsumerType(String string, double count, Good timeType, double timeEndow, Good[] goods, double... weights) {
		IConsumer cons = new Consumer(store, string, timeEndow, timeType, goods, weights);
		consumers.add(new ScaledConsumer(store, cons, count));
	}

	public void addFirmType(String name, double count, Good output, double productivity, Good[] inputGoods, double[] inputWeights) {
		CobbDouglasFirm firm = new CobbDouglasFirm(store, name, output, productivity, inputGoods, inputWeights);
		firms.add(new ScaledFirm(store, firm, count));
	}

	public void solve() {
		DepthFirstSearch<FloatVar> search = new DepthFirstSearch<FloatVar>();
		ArrayList<FloatVar> all = new ArrayList<FloatVar>();
		all.addAll(Arrays.asList(inputPrices));
		all.addAll(Arrays.asList(outputPrices));
		all.add(dividend);
		for (IConsumer c : consumers) {
			all.add(c.getWorkHours());
		}
//		for (IConsumer c : consumers) {
//			all.addAll(Arrays.asList(c.getConsumption()));
//		}
		SplitSelectFloat<FloatVar> s = new SplitSelectFloat<FloatVar>(store, all.toArray(new FloatVar[] {}), null);

		search.setSolutionListener(new PrintOutListener<FloatVar>());

		search.getSolutionListener().searchAll(true);
		search.labeling(store, s);
		System.out.println(store.variablesHashMap.size() + " variables");
	}

}
