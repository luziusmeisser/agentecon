package com.agentecon.finance;

public interface IPublicCompany {

	public ShareRegister getShareRegister();

	public Ticker getTicker();

	public void payDividends(int day);

}
