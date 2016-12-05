package com.antarescraft.kloudy.slots;

public class Jackpot
{
	private String name;
	private double payout;
	
	public Jackpot(String name, double payout)
	{
		this.name = name;
		this.payout = payout;
	}
	
	public String getName()
	{
		return name;
	}
	
	public double getPayout()
	{
		return payout;
	}
}