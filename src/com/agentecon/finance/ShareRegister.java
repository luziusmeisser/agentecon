package com.agentecon.finance;

import java.util.Iterator;
import java.util.LinkedList;


import com.agentecon.api.Price;
import com.agentecon.good.IStock;
import com.agentecon.stats.Numbers;
import com.agentecon.util.MovingAverage;

public class ShareRegister implements IRegister {
	
	public static final double INITIAL_PRICE = 10.0;

	private Ticker ticker;
	private Position rootPosition;
	private MovingAverage dividend;
	private LinkedList<Position> all;

	public ShareRegister(String firmName, IStock wallet) {
		this.ticker = new Ticker(firmName);
		this.all = new LinkedList<>();
		this.dividend = new MovingAverage(0.8);
		this.rootPosition = new Position(this, ticker, wallet.getGood(), SHARES_PER_COMPANY);
	}
	
	public void raiseCapital(DailyStockMarket dsm, IStock wallet) {
		if (!rootPosition.isEmpty()){
			rootPosition.collectDividend(wallet);
			dsm.offer(new AskFin(wallet, rootPosition, new Price(getTicker(), INITIAL_PRICE), rootPosition.getAmount()));
		}
	}

	public void payDividend(IStock sourceWallet, double totalDividends) {
		dividend.add(totalDividends);

		if (!Numbers.equals(getTotalShares(), SHARES_PER_COMPANY)) {
			double diff = getTotalShares() - SHARES_PER_COMPANY;
			if (diff > 0) {
				all.getLast().add(diff);
			}
		}

		Iterator<Position> iter = all.iterator();
		while (iter.hasNext()) {
			Position pos = iter.next();
			if (pos.isDisposed()) {
				iter.remove();
			} else {
				pos.receiveDividend(sourceWallet, totalDividends / SHARES_PER_COMPANY);
			}
		}
	}
	
	@Override
	public double getAverageDividend() {
		return dividend.getAverage();
	}
	
	public Position createPosition(){
		Position pos = new Position(this, getTicker(), rootPosition.getCurrency(), 0.0);
		all.add(pos);
		return pos;
	}
	
	public void inherit(Position pos){
		pos.dispose(rootPosition);
	}

	public Ticker getTicker() {
		return ticker;
	}

	private double getTotalShares() {
		double tot = 0.0;
		for (Position p : all) {
			tot += p.getAmount();
		}
		return tot;
	}

	@Override
	public String toString() {
		return ticker + " has " + all.size() + " shareholders and pays " + dividend;
	}

}
