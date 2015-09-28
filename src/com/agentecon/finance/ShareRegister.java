package com.agentecon.finance;

import java.util.Iterator;
import java.util.LinkedList;

import com.agentecon.api.Price;
import com.agentecon.good.IStock;
import com.agentecon.stats.Numbers;

public class ShareRegister implements IRegister {
	
	public static final double INITIAL_PRICE = 10.0;

	private Ticker ticker;
	private Position rootPosition;
	private double latestDividends;
	private LinkedList<Position> all;

	public ShareRegister(String firmName, IStock wallet) {
		this.ticker = new Ticker(firmName);
		this.all = new LinkedList<>();
		this.latestDividends = 0.0;
		this.rootPosition = new Position(this, ticker, wallet.getGood());
	}
	
	public void raiseCapital(DailyStockMarket dsm, IStock wallet) {
		if (!rootPosition.isEmpty()){
			rootPosition.collectDividend(wallet);
			dsm.offer(new AskFin(wallet, rootPosition, new Price(getTicker(), INITIAL_PRICE), rootPosition.getAmount()));
		}
	}

	public void payDividend(IStock sourceWallet, double totalDividends) {
		latestDividends = totalDividends;

		if (!Numbers.equals(getTotalShares(), Position.SHARES_PER_COMPANY)) {
			double diff = getTotalShares() - Position.SHARES_PER_COMPANY;
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
				pos.receiveDividend(sourceWallet, totalDividends / Position.SHARES_PER_COMPANY);
			}
		}
	}
	
	public double getLatestDividends(){
		return latestDividends;
	}
	
	public Position createPosition(){
		return rootPosition.split(0);
	}
	
	public void inherit(Position pos){
		pos.dispose(rootPosition);
	}

	public Ticker getTicker() {
		return ticker;
	}

	@Override
	public void register(Position position) {
		all.add(position);
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
		return ticker + " has " + all.size() + " shareholders";
	}

}
