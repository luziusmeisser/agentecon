package com.agentecon.finance;

import java.util.Iterator;
import java.util.LinkedList;

import com.agentecon.good.IStock;
import com.agentecon.stats.Numbers;

public class ShareRegister implements IRegister {

	private Ticker ticker;
	private Position rootPosition;
	private LinkedList<Position> all;

	public ShareRegister(String firmName, IStock wallet) {
		this.ticker = new Ticker(firmName);
		this.all = new LinkedList<>();
		this.rootPosition = new Position(this, ticker, wallet.getGood());
	}

	public void payDividend(IStock sourceWallet, double totalDividends) {
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

	public Position obtain(double size) {
		return rootPosition.split(size);
	}

	public Position[] split(int number) {
		double sharesPerPosition = rootPosition.getAmount() / number;
		Position[] poses = new Position[number];
		for (int i = 1; i < number; i++) {
			poses[i] = rootPosition.split(sharesPerPosition);
		}
		poses[0] = rootPosition.split(rootPosition.getAmount());
		return poses;
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
