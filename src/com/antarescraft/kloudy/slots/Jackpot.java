package com.antarescraft.kloudy.slots;

import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigElementKey;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.DoubleConfigProperty;

public class Jackpot
{
	@ConfigElementKey
	private String name;
	
	@DoubleConfigProperty(defaultValue = 0, maxValue = Double.POSITIVE_INFINITY, minValue = 0)
	@ConfigProperty(key = "payout")
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