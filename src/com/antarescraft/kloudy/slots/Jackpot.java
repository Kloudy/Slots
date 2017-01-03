package com.antarescraft.kloudy.slots;

import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigElementKey;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.DoubleConfigProperty;

public class Jackpot
{
	@ConfigElementKey
	private String jackpotType;
	
	@DoubleConfigProperty(defaultValue = 0, maxValue = Double.POSITIVE_INFINITY, minValue = 0)
	@ConfigProperty(key = "payout")
	private double payout;
	
	public SlotElement getType()
	{
		return SlotElement.getJackpotTypeByTypeId(jackpotType);
	}
	
	public double getPayout()
	{
		return payout;
	}
	
	public String toString()
	{
		return String.format("{ name: %s, payout: %s }", jackpotType, Double.toString(payout));
	}
}