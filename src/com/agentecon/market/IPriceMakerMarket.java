// Created by Luzius on Apr 28, 2014

package com.agentecon.market;

import com.agentecon.good.Good;

public interface IPriceMakerMarket {
	
	public void offer(Bid offer);
	
	public void offer(Ask offer);
	
}
