package com.agentecon.market;

import com.agentecon.api.Price;
import com.agentecon.good.Good;
import com.agentecon.metric.AbstractListenerList;
import com.agentecon.metric.IMarketListener;

public class MarketListeners extends AbstractListenerList<IMarketListener> implements IMarketListener {

	@Override
	public void notifyOffered(Good good, double quantity, Price price) {
		for (IMarketListener l: list){
			l.notifyOffered(good, quantity, price);
		}
	}

	@Override
	public void notifySold(Good good, double quantity, Price price) {
		for (IMarketListener l: list){
			l.notifySold(good, quantity, price);
		} 
	}

}
