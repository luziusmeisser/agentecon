package com.agentecon.verification;

import java.util.ArrayList;

import com.agentecon.agent.Endowment;
import com.agentecon.api.SimulationConfig;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.finance.IPublicCompany;
import com.agentecon.firm.Producer;
import com.agentecon.firm.decisions.CogsDividend;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.metric.IFirmListener;
import com.agentecon.price.PriceConfig;
import com.agentecon.price.PriceFactory;
import com.agentecon.sim.config.IConfiguration;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.world.IWorld;

public class CompetitiveScenario implements IConfiguration {

	private int iteration;
	private ArrayList<FirmStatistics> firmStats;

	public CompetitiveScenario() {
		this.iteration = -1;
		this.firmStats = new ArrayList<>();
	}

	@Override
	public SimulationConfig createNextConfig() {
		iteration++;
		final FirmStatistics stats = new FirmStatistics();
		firmStats.add(stats);
		StolperSamuelson scenario = new StolperSamuelson() {

			@Override
			protected void addSpecialEvents(SimConfig config) {
				double val = HIGH;
				double step = 0.05;
				for (int i = 1000; i < 5000; i += 500) {
					val -= step;
					step += 0.05;
					super.updatePrefs(config, i, val);
				}
				config.addEvent(new SimEvent(0, 0) {

					@Override
					public void execute(IWorld sim) {
						sim.addListener(stats);
					}

				});
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
								return new CogsDividend(0.5, iteration);
//								int count = (iteration + 2) / 3;
//								int shift = (iteration - 1) % 3;
//								int index = type % count;
//								switch ((index + shift) % 3) {
//								case 0:
//									return new AdjustingDividend();
//								case 1:
//									return new FractionalDividends();
//								case 2:
//									return new StandardStrategy();
//								case 3:
//									return new CogsDividend(0.5);
//								default:
//									throw new RuntimeException();
//								}
							}
						});
					}
				}
			}

		};
		return scenario.createConfiguration(PriceConfig.DEFAULT, 5000);
	}

	@Override
	public boolean shouldTryAgain() {
//		return false;
//		System.out.println(firmStats.get(firmStats.size() - 1).getRanking());
		return iteration < 10;
	}

	@Override
	public String getComment() {
		return new CogsDividend(0.5, iteration).getDescription();
	}

}
