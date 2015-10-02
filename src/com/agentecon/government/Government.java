package com.agentecon.government;

import java.util.Collection;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.api.IConsumer;
import com.agentecon.consumer.Consumer;
import com.agentecon.finance.ITax;
import com.agentecon.good.IStock;

public class Government extends Agent {

	private DividendTax tax;

	public Government() {
		super("Government", new Endowment());
		this.tax = new DividendTax(getMoney());
	}

	public ITax getDividendTax() {
		return tax;
	}

	public void distributeWelfare(Collection<Consumer> cons) {
		IStock money = getMoney();
		double amount = getMoney().getAmount() / cons.size();
		for (IConsumer c : cons) {
			c.getMoney().transfer(money, amount);
		}
	}

	@Override
	public Agent clone() {
		Government klon = (Government) super.clone();
		klon.tax = new DividendTax(klon.getMoney());
		return klon;
	}

}
