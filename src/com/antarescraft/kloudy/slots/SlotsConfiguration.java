package com.antarescraft.kloudy.slots;

import java.util.HashMap;

import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigElementMap;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.DoubleConfigProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.OptionalConfigProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.StringConfigProperty;

public class SlotsConfiguration
{
	@OptionalConfigProperty
	@StringConfigProperty(defaultValue = "BLOCK_NOTE_HARP")
	@ConfigProperty(key = "slot-tick-sound", note = "The sound that plays while the slot machine is rolling.")
	private String slotTickSound;

	@DoubleConfigProperty(defaultValue = 5.00, maxValue = Double.POSITIVE_INFINITY, minValue = 0)
	@ConfigProperty(key = "buy-in", note = "How much money it costs to play the game per roll")
	private double buyIn;
	
	@ConfigElementMap
	@ConfigProperty(key = "jackpots", note = "List of possible jackpots")
	private HashMap<String, Jackpot> jackpots;
	
	public String getSlotTickSound()
	{
		return slotTickSound;
	}
	
	public double getBuyIn()
	{
		return buyIn;
	}
	
	public HashMap<String, Jackpot> getJackpots()
	{
		return jackpots;
	}
	
	public String toString()
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("{")
					.append("slotTickSound: " + slotTickSound + ",")
					.append("buyIn: " + buyIn + ",")
					.append("jackpots: ");
		
		if(jackpots != null)
		{
			strBuilder.append("[");
			for(Jackpot jackpot : jackpots.values())
			{
				strBuilder.append(jackpot.toString() + ",");
			}
			strBuilder.append("]");
		}
		
		strBuilder.append("}");
		
		return strBuilder.toString();	
	}
}