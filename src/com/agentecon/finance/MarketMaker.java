package com.agentecon.finance;

import java.util.HashMap;

import com.agentecon.api.IAgent;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.util.Average;

public class MarketMaker implements IPublicCompany, IAgent, Cloneable {

	public static final double TARGET_OWNERSHIP = 0.1;

	private IStock money;
	private Portfolio portfolio;
	private ShareRegister register;
	private HashMap<Ticker, MarketMakerPrice> priceBeliefs;

	public MarketMaker(IStock money) {
		this.money = money;
		this.portfolio = new Portfolio(money);
		this.priceBeliefs = new HashMap<Ticker, MarketMakerPrice>();
		this.register = new ShareRegister("MarketMaker", money);
	}

	public void postOffers(IStockMarket dsm) {
		portfolio.collectDividends();
		for (MarketMakerPrice e : priceBeliefs.values()) {
			Position pos = portfolio.getShares(e.getTicker());
			boolean needMore = pos.getOwnershipShare() < TARGET_OWNERSHIP;
			e.trade(dsm, money, pos, needMore);
		}
	}

	public Ticker getTicker() {
		return register.getTicker();
	}
	
	public void addPosition(Position pos){
		portfolio.addPosition(pos);
		priceBeliefs.put(pos.getTicker(), new MarketMakerPrice(pos.getTicker()));
	}

	@Override
	public ShareRegister getShareRegister() {
		return register;
	}
	
	@Override
	public void payDividends(int day) {
		double targetCashLevel = calcDesiredCashLevel();
		double excessCash = money.getAmount() - targetCashLevel;
		if (excessCash > 0){
			register.payDividend(money, excessCash / 5);
		}
	}

	private double calcDesiredCashLevel() {
		double tot = 0.0;
		for (MarketMakerPrice p: priceBeliefs.values()){
			tot += p.getPrice() * Position.SHARES_PER_COMPANY * TARGET_OWNERSHIP;
		}
		return Math.min(1000, tot);
	}

	@Override
	public String getType() {
		return getClass().getSimpleName();
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public IStock getMoney() {
		return money;
	}

	@Override
	public Inventory getInventory() {
		return new Inventory();
	}
	
	public void inherit(Portfolio inheritance) {
		portfolio.absorb(inheritance);
	}
	
	public double getPrice(Good output) {
		return priceBeliefs.get(output).getPrice();
	}
	
	public Average getAvgHoldings(){
		Average avg = new Average();
		for (Ticker t: priceBeliefs.keySet()){
			avg.add(portfolio.getShares(t).getAmount());
		}
		return avg;
	}
	
	@Override
	public MarketMaker clone(){
		return this; // TEMP todo
	}

	@Override
	public String toString(){
		return money + ", holding " + getAvgHoldings() + ", price index: " + getIndex(); //priceBeliefs.values().toString();
	}

	private Average getIndex() {
		Average avg = new Average();
		for (MarketMakerPrice mmp: priceBeliefs.values()){
			avg.add(mmp.getPrice());
		}
		return avg;
	}

}
