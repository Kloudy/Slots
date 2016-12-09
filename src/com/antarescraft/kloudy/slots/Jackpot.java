package com.antarescraft.kloudy.slots;

import com.antarescraft.kloudy.plugincore.config.annotations.ConfigurationElementKey;
import com.antarescraft.kloudy.plugincore.config.annotations.DoubleConfigurationProperty;

public class Jackpot
{
	@ConfigurationElementKey
	private String name;
	
	@DoubleConfigurationProperty(key = "payout", defaultValue = 15)
	private double payout;
	
	public String getName()
	{
		return name;
	}
	
	public double getPayout()
	{
		return payout;
	}
}