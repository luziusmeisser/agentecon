package com.agentecon.government;

import com.agentecon.finance.ITax;
import com.agentecon.good.IStock;

public class DividendTax implements ITax {

	private static final double TAX_RATE = 0.2;
	
	private IStock money;
	
	public DividendTax(IStock money){
		this.money = money;
	}

	@Override
	public void collect(IStock wallet, double latestDividends) {
		money.transfer(wallet, latestDividends * TAX_RATE);
	}

}
