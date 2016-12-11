package com.antarescraft.kloudy.slots;

import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigElementKey;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigElementKeyNote;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.DoubleConfigProperty;

public class Jackpot
{
	@ConfigElementKeyNote(key = "coin-jackpot", note = "1/5 (20%) chance of rolling this jackpot. This is the most common jackpot")
	@ConfigElementKeyNote(key = "ring-jackpot", note = "1/8 (12.5%) chance of rolling this jackpot")
	@ConfigElementKeyNote(key = "star-jackpot", note = "1/12 (8.3%) chance of rolling this jackpot")
	@ConfigElementKeyNote(key = "tnt-jackpot", note = "1/20 (5%) chance of rolling this jackpot")
	@ConfigElementKeyNote(key = "cherry-jackpot", note = "1/50 (2%) chance of rolling this jackpot")
	@ConfigElementKeyNote(key = "trophy-jackpot", note = "1/100 (1%) chance of rolling this jackpot")
	@ConfigElementKeyNote(key = "diamond-jackpot", note = "1/1000 (0.1%) chance of rolling this jackpot. This is the most rare jackpot to be rolled")
	@ConfigElementKey
	private String name;
	
	@DoubleConfigProperty(defaultValue = 0, maxValue = Double.POSITIVE_INFINITY, minValue = 0)
	@ConfigProperty(key = "payout", note = "The amount of money that the slot machine pays out to the player as a result of winning this jackpot")
	private double payout;
	
	public String getName()
	{
		return name;
	}
	
	public double getPayout()
	{
		return payout;
	}
	
	public String toString()
	{
		return String.format("{ name: %s, payout: %s }", name, Double.toString(payout));
	}
}