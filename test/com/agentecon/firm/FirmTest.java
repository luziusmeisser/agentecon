// Created on May 22, 2015 by Luzius Meisser

package com.agentecon.firm;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.agentecon.agent.Endowment;
import com.agentecon.api.Price;
import com.agentecon.consumer.Weight;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.firm.production.LogProdFun;
import com.agentecon.good.Good;
import com.agentecon.good.Inventory;
import com.agentecon.good.Stock;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.Market;
import com.agentecon.price.HardcodedPrice;
import com.agentecon.price.IPrice;
import com.agentecon.price.IPriceFactory;
import com.agentecon.price.PriceConfig;
import com.agentecon.price.PriceFactory;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.stats.Numbers;

public class FirmTest {

	private Endowment end;
	private Random rand;

	@Before
	public void setUp() throws Exception {
		this.rand = new Random(23);
		this.end = new Endowment(new Stock[] { new Stock(SimConfig.MONEY, 1000) }, new Stock[] {});
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPriceFinding() {
		TestConsumer tc = new TestConsumer(new Price(SimConfig.PIZZA, 30), new Price(SimConfig.SWISSTIME, 10), new Price(SimConfig.ITALTIME, 15));
		Firm firm = new Firm("testfirm", end, new LogProdFun(SimConfig.PIZZA, new Weight(SimConfig.ITALTIME, 5.0), new Weight(SimConfig.SWISSTIME, 5.0)), new PriceFactory(rand, PriceConfig.DEFAULT));
		for (int i = 0; i < 100; i++) {
			Market market = new Market(rand);
			firm.offer(market);
			System.out.println(tc.getPriceSquareError(market));
			tc.buyAndSell(market);
			firm.produce(i);
		}
		for (int i = 0; i < 100; i++) {
			Market market = new Market(rand);
			firm.offer(market);
			System.out.println(tc.getPriceSquareError(market));
			tc.buyAndSell(market);
			firm.adaptPrices();
			firm.produce(i);
		}
		Market market = new Market(rand);
		firm.offer(market);
		assert tc.checkPrices(market, 0.10);
	}
	
	@Test
	public void testOptimalProduction(){
		final double hourPrice = 2.972868529894414d;
		this.end = new Endowment(new Stock[] { new Stock(SimConfig.MONEY, 1000), new Stock(SimConfig.FONDUE, 36.156428643107d) }, new Stock[] {});
		Firm firm = new Firm("chalet", end, new LogProdFun(SimConfig.FONDUE, new Weight(SimConfig.SWISSTIME, 10.0)), new IPriceFactory(){

			@Override
			public IPrice createPrice(Good good) {
				if (good.equals(SimConfig.SWISSTIME)){
					return new HardcodedPrice(hourPrice);
				} else {
					return new HardcodedPrice(10.0);
				}
			}
			
		});
		firm.offer(new IPriceMakerMarket() {
			
			@Override
			public void offer(Ask offer) {
				assert offer.getPrice().getPrice() == 10.0;
				offer.accept(new Stock(SimConfig.MONEY, 100000), new Stock(offer.getGood()), offer.getAmount());
			}
			
			@Override
			public void offer(Bid offer) {
				assert offer.getPrice().getPrice() == hourPrice;
				assert Math.abs(offer.getAmount() - 32.63754535204813) < 0.0001 : "Firm does not seek optimal input amount";
				offer.accept(new Stock(SimConfig.MONEY), new Stock(offer.getGood(), offer.getAmount()), offer.getAmount());
			}

		});
		firm.adaptPrices();
		double production = firm.produce(0);
		assert Math.abs(production - 36.1564) < 0.001;
//		double profits = firm.getLatestProfits();
//		assert Math.abs(profits - 264.537) < 0.001;
	}
	
	@Test
	public void testOptimalProductionCobbDouglas1(){
		final double hourPrice1 = 2.0;
		final double fonduePrice = 10.0;
		this.end = new Endowment(new Stock[] { new Stock(SimConfig.MONEY, 1000) }, new Stock[] {});
		double alpha = 0.5;
		IProductionFunction prodFun = new CobbDouglasProduction(SimConfig.FONDUE, 1.0, new Weight(SimConfig.SWISSTIME, alpha));
		Firm firm = new Firm("chalet", end, prodFun, new IPriceFactory(){

			@Override
			public IPrice createPrice(Good good) {
				if (good.equals(SimConfig.SWISSTIME)){
					return new HardcodedPrice(hourPrice1);
				} else {
					return new HardcodedPrice(fonduePrice);
				}
			}
			
		});
		firm.offer(new IPriceMakerMarket() {
			
			@Override
			public void offer(Ask offer) {
				assert offer.getPrice().getPrice() == fonduePrice;
				offer.accept(new Stock(SimConfig.MONEY, 100000), new Stock(offer.getGood()), offer.getAmount());
			}
			
			@Override
			public void offer(Bid offer) {
				assert offer.getPrice().getPrice() == hourPrice1;
				offer.accept(new Stock(SimConfig.MONEY), new Stock(offer.getGood(), offer.getAmount()), offer.getAmount());
			}

		});
		double production = firm.produce(0);
		System.out.println("Produced " + production);
		double x1 = 6.25;
		double production2 = prodFun.produce(new Inventory(new Stock(SimConfig.SWISSTIME, x1)));
		System.out.println(production2);
		assert Numbers.equals(production, production2);
	}
	
	@Test
	public void testOptimalProductionCobbDouglas2(){
		final double hourPrice1 = 5.0;
		final double hourPrice2 = 4.0;
		final double fonduePrice = 10.0;
		this.end = new Endowment(new Stock[] { new Stock(SimConfig.MONEY, 1000) }, new Stock[] {});
		double alpha = 0.45;
		double beta = 0.25;
		double factor = 2.0;
		IProductionFunction prodFun = new CobbDouglasProduction(SimConfig.FONDUE, factor, new Weight(SimConfig.SWISSTIME, alpha), new Weight(SimConfig.ITALTIME, beta));
		Firm firm = new Firm("chalet", end, prodFun, new IPriceFactory(){

			@Override
			public IPrice createPrice(Good good) {
				if (good.equals(SimConfig.SWISSTIME)){
					return new HardcodedPrice(hourPrice1);
				} else if (good.equals(SimConfig.ITALTIME)){
					return new HardcodedPrice(hourPrice2);
				} else {
					return new HardcodedPrice(fonduePrice);
				}
			}
			
		});
		firm.offer(new IPriceMakerMarket() {
			
			@Override
			public void offer(Ask offer) {
				assert offer.getPrice().getPrice() == fonduePrice;
				offer.accept(new Stock(SimConfig.MONEY, 100000), new Stock(offer.getGood()), offer.getAmount());
			}
			
			@Override
			public void offer(Bid offer) {
				assert offer.getPrice().getPrice() == hourPrice1 || offer.getPrice().getPrice() == hourPrice2;
				offer.accept(new Stock(SimConfig.MONEY), new Stock(offer.getGood(), offer.getAmount()), offer.getAmount());
			}

		});
		double production = firm.produce(0);
		System.out.println("Produced " + production);
		double x1 = Math.pow(factor * fonduePrice*Math.pow(alpha / hourPrice1, 1 - beta)*Math.pow(beta / hourPrice2, beta), 1/(1 - alpha - beta));
		double x2 = hourPrice1 / hourPrice2 * beta / alpha * x1;
		double production2 = prodFun.produce(new Inventory(new Stock(SimConfig.SWISSTIME, x1), new Stock(SimConfig.ITALTIME, x2)));
		System.out.println(production2);
		assert Numbers.equals(production, production2);
	}
	
}
