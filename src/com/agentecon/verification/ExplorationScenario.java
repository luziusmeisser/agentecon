package com.agentecon.verification;

import java.util.ArrayList;

import com.agentecon.agent.Endowment;
import com.agentecon.api.SimulationConfig;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.finance.IPublicCompany;
import com.agentecon.firm.Producer;
import com.agentecon.firm.decisions.EExplorationMode;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.firm.decisions.StrategyExploration;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.metric.IFirmListener;
import com.agentecon.price.PriceConfig;
import com.agentecon.price.PriceFactory;
import com.agentecon.sim.config.IConfiguration;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.stats.Numbers;
import com.agentecon.world.IWorld;

public class ExplorationScenario implements IConfiguration {

	private static final int DAYS = 2000;

//	private static final double MIN = -5;
//	private static final double MAX = 5.0;
//	private static final double INCREMENT = 0.1;
	
	private static final double MIN = -1.0;
	private static final double MAX = 1.0;
	private static final double INCREMENT = 0.5;

	protected static final double STEP = 0.1;
	protected static final double STEPSTEP = 0.05;

	private double fr;
	private boolean exploreMode;
	private EExplorationMode mode;
	private ArrayList<FirmStatistics> firmStats;

	public ExplorationScenario(EExplorationMode mode) {
		this(mode, MIN);
		this.exploreMode = false;
	}

	public ExplorationScenario(double fr) {
		this(EExplorationMode.values()[0], fr);
		this.exploreMode = true;
	}

	private ExplorationScenario(EExplorationMode mode, double fr) {
		this.mode = mode;
		this.fr = fr - INCREMENT;
		this.firmStats = new ArrayList<>();
	}

	@Override
	public SimulationConfig createNextConfig() {
		if (exploreMode) {
			int next = mode.ordinal() + 1;
			this.mode = EExplorationMode.values()[next];
		} else {
			fr += INCREMENT;
		}
		final FirmStatistics stats = new FirmStatistics();
		firmStats.add(stats);
		StolperSamuelson scenario = new StolperSamuelson() {

			@Override
			protected void addSpecialEvents(SimConfig config) {
				double val = HIGH;
				double step = STEP;
				for (int i = 1000; i < DAYS; i += 250) {
					val -= step;
					step += STEPSTEP;
					super.updatePrefs(config, i, val);
				}
				config.addEvent(new SimEvent(0, 0) {

					@Override
					public void execute(IWorld sim) {
						sim.addListener(stats);
					}

				});
			}

			protected int getRandomSeed() {
				return (int) (fr * 10000);
			}

			@Override
			protected void addFirms(PriceConfig pricing, int scale, SimConfig config) {
				for (int i = 0; i < outputs.length; i++) {
					Endowment end = new Endowment(new IStock[] { new Stock(SimConfig.MONEY, 1000) }, new IStock[] {});
					for (int f = 0; f < scale * FIRMS_PER_TYPE; f++) {
						final int number = f;
						IProductionFunction prodfun = prodWeights.createProdFun(i, RETURNS_TO_SCALE);
						config.addEvent(new FirmEvent(1, "firm_" + i, end, prodfun, pricing) {
							protected Producer createFirm(String type, Endowment end, IProductionFunction prodFun, PriceFactory pf) {
								final IFirmDecisions strategy = createStrategy(number);
								Producer f = createFirm(type, end, prodFun, pf, strategy);
								f.addFirmMonitor(new IFirmListener() {

									@Override
									public void notifyProduced(IPublicCompany inst, String producer, IStock[] inputs, IStock output) {
									}

									@Override
									public void reportDividend(IPublicCompany inst, double amount) {
									}

									@Override
									public void reportResults(IPublicCompany inst, double revenue, double cogs, double profits) {
										stats.reportProfits(strategy, profits);
									}

								});
								return f;
							}

							private IFirmDecisions createStrategy(int type) {
								return ExplorationScenario.this.createStrategy();
							}
						});
					}
				}
			}

		};
		return scenario.createConfiguration(PriceConfig.DEFAULT, 0, 1, DAYS);
	}

	@Override
	public boolean shouldTryAgain() {
		if (exploreMode) {
			return mode.ordinal() < EExplorationMode.values().length - 1;
		} else {
			return Numbers.isSmaller(fr, MAX);
		}
	}

	protected IFirmDecisions createStrategy() {
		return new StrategyExploration(StolperSamuelson.RETURNS_TO_SCALE, fr, mode);
	}

	@Override
	public String getComment() {
		FirmStatistics latest = firmStats.get(firmStats.size() - 1);
		return createStrategy().toString(); // + "\t" + latest.getProfits();
	}

	public static void main(String[] args) {
		double val = StolperSamuelson.HIGH;
		double step = STEP;
		for (int i = 0; i < 5; i++) {
			StolperSamuelson ss1 = new StolperSamuelson(10.0 - val);
			val -= step;
			step += STEPSTEP;
			Result r1 = ss1.runConstrainedOptimization(null, 0.0001);
			System.out.println(r1);
		}
	}

}
