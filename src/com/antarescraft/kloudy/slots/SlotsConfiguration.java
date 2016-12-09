package com.antarescraft.kloudy.slots;

import java.util.HashMap;

import com.antarescraft.kloudy.plugincore.config.annotations.ConfigurationElementMap;
import com.antarescraft.kloudy.plugincore.config.annotations.DoubleConfigurationProperty;
import com.antarescraft.kloudy.plugincore.config.annotations.StringConfigurationProperty;

public class SlotsConfiguration
{
	@StringConfigurationProperty(key = "slot-tick-sound", defaultValue = "BLOCK_NOTE_HARP")
	private String slotTickSound;
	
	@DoubleConfigurationProperty(key = "buy-in", defaultValue = 5)
	private double buyIn;
	
	@ConfigurationElementMap(key = "jackpots", elementClasspath = "com.antarescraft.kloudy.slots.Jackpot")
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
			for(Jackpot jackpot : jackpots.values())
			{
				strBuilder.append("	" + jackpot.getName() + ": ")
							.append("   " + jackpot.getPayout());
			}
		}
		
		strBuilder.append("}");
		
		return strBuilder.toString();	
	}
}