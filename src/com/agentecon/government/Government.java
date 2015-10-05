package com.agentecon.government;

import java.util.Collection;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.api.IConsumer;
import com.agentecon.consumer.Consumer;
import com.agentecon.finance.ITax;
import com.agentecon.good.IStock;
import com.agentecon.stats.Numbers;

public class Government extends Agent {

	private DividendTax tax;

	public Government() {
		super("Government", new Endowment());
		this.tax = new DividendTax(getMoney());
	}

	public ITax getDividendTax() {
		return tax;
	}

	public void distributeWelfare(int day, Collection<Consumer> cons) {
		IStock money = getMoney();
		// System.out.println(day + " distributing " + money.getAmount());
		double amount = getMoney().getAmount() / cons.size();
		if (Numbers.isBigger(amount, 0.0)) {
			for (IConsumer c : cons) {
				c.getMoney().transfer(money, amount);
			}
		}
	}

	@Override
	public Agent clone() {
		Government klon = (Government) super.clone();
		klon.tax = new DividendTax(klon.getMoney());
		return klon;
	}

}