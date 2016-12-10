package com.antarescraft.kloudy.slots;

import com.antarescraft.kloudy.hologuiapi.plugincore.config.annotations.ConfigurationElementKey;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.annotations.DoubleConfigurationProperty;

public class Jackpot
{
	@ConfigurationElementKey
	private String name;
	
	@DoubleConfigurationProperty(key = "payout")
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