package com.antarescraft.kloudy.slots;

import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigObject;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.PassthroughParams;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.annotations.ConfigElementKey;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.annotations.ConfigProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.annotations.DoubleConfigProperty;

public class Jackpot implements ConfigObject
{
	@ConfigElementKey
	private String jackpotType;
	
	@DoubleConfigProperty(defaultValue = 0, maxValue = Double.MAX_VALUE, minValue = 0)
	@ConfigProperty(key = "payout")
	private double payout;
	
	@ConfigProperty(key = "name")
	private String name;
	
	private Jackpot(){}
	
	public double getPayout()
	{
		return payout;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		return String.format("{ name: %s, payout: %s }", jackpotType, Double.toString(payout));
	}

	@Override
	public void configParseComplete(PassthroughParams params) {}
}