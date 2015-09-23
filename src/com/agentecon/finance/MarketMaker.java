package com.agentecon.finance;

import java.util.HashMap;

import com.agentecon.api.IAgent;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;

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
		return tot;
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
	
	@Override
	public MarketMaker clone(){
		return this; // TEMP todo
	}

	@Override
	public String toString(){
		return priceBeliefs.values().toString();
	}

}
