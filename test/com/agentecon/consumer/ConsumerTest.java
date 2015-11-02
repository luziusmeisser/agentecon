// Created on Jun 3, 2015 by Luzius Meisser

package com.agentecon.consumer;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.agentecon.agent.Endowment;
import com.agentecon.api.Price;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceFilter;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.stats.Numbers;

public class ConsumerTest {
	
	public static final Good MONEY = new Good("Taler");
	public static final Good PIZZA = new Good("Pizza", 1.0);
	public static final Good FONDUE = new Good("Fondue", 1.0);
	public static final Good SWISSTIME = new Good("Swiss man-hours", 0.0);
	public static final Good ITALTIME = new Good("Italian man-hours", 0.0);

	public static IOffer createBid() {
		return new Bid(new Stock(MONEY, 10000), new Stock(SWISSTIME), new Price(SWISSTIME, 2.97287), 1000);
	}

	public static IOffer createAsk() {
		return new Ask(new Stock(MONEY), new Stock(FONDUE, 1000), new Price(FONDUE, 10), 1000);
	}

	public static Endowment createEndowment() {
		return new Endowment(new Stock[] { new Stock(MONEY, 26.4537) }, new Stock[] { new Stock(SWISSTIME, 24) });
	}

	public static Endowment createEndowment2() {
		return new Endowment(new Stock[] { new Stock(MONEY, 1.465103413) }, new Stock[] { new Stock(ITALTIME, 24) });
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		LogUtil utilFun = new LogUtil(new Weight(FONDUE, 10), new Weight(SWISSTIME, 14));
		Consumer cons = new Consumer("dummy", createEndowment(), utilFun);
		cons.collectDailyEndowment();
		cons.maximizeUtility(new IPriceTakerMarket() {

			private IOffer ask = createAsk();
			private IOffer bid = createBid();

			@Override
			public Collection<IOffer> getOffers(IPriceFilter bidAskFilter) {
				return Arrays.asList(bid, ask);
			}

			@Override
			public IOffer getOffer(Good good, boolean bid) {
				return bid ? this.bid : this.ask;
			}

			@Override
			public Collection<IOffer> getBids() {
				return Arrays.asList(bid);
			}

			@Override
			public Collection<IOffer> getAsks() {
				return Arrays.asList(ask);
			}
		});
		double util = cons.consume();
		assert Numbers.equals(util, 58.400245564);
	}

	@Test
	public void test2() {
		LogUtil utilFun = new LogUtil(new Weight(PIZZA, 8), new Weight(ITALTIME, 14));
		Consumer cons = new Consumer("dummy", createEndowment2(), utilFun);
		cons.collectDailyEndowment();
		cons.maximizeUtility(new IPriceTakerMarket() {

			private IOffer ask = new Ask(new Stock(MONEY), new Stock(PIZZA, 1000), new Price(FONDUE, 1.0), 1000);
			private IOffer bid = new Bid(new Stock(MONEY, 10000), new Stock(ITALTIME), new Price(ITALTIME, 0.24424871756), 1000);

			@Override
			public Collection<IOffer> getOffers(IPriceFilter bidAskFilter) {
				return Arrays.asList(bid, ask);
			}

			@Override
			public IOffer getOffer(Good good, boolean bid) {
				return bid ? this.bid : this.ask;
			}

			@Override
			public Collection<IOffer> getBids() {
				return Arrays.asList(bid);
			}

			@Override
			public Collection<IOffer> getAsks() {
				return Arrays.asList(ask);
			}
		});
		assert Numbers.equals(cons.consume(), cons.getUtilityFunction().getUtility(Arrays.<IStock>asList(new Stock(PIZZA, 2.116844127999999), new Stock(ITALTIME, 21.331651435017676))));
	}

}
