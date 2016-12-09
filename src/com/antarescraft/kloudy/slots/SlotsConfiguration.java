package com.antarescraft.kloudy.slots;

import java.util.HashMap;

import com.antarescraft.kloudy.hologuiapi.plugincore.config.annotations.ConfigurationElementMap;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.annotations.DoubleConfigurationProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.annotations.StringConfigurationProperty;

public class SlotsConfiguration
{
	@StringConfigurationProperty(key = "slot-tick-sound", defaultValue = "BLOCK_NOTE_HARP")
	private String slotTickSound;
	
	@DoubleConfigurationProperty(key = "buy-in", defaultValue = 5)
	private double buyIn;
	
	@ConfigurationElementMap(key = "jackpots")
	private HashMap<String, Jackpot> jackpots;
	
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