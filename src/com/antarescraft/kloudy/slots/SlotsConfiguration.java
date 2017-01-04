package com.antarescraft.kloudy.slots;

import java.util.HashMap;

import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigElementMap;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigParser;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.DoubleConfigProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.OptionalConfigProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.StringConfigProperty;

public class SlotsConfiguration
{
	private static SlotsConfiguration instance;
	
	public static void loadConfig(Slots slots)
	{
		slots.saveDefaultConfig();
		slots.reloadConfig();
		slots.loadGUIPages();
		
		if(instance == null)
		{
			instance = ConfigParser.parse(Slots.pluginName, slots.getConfig(), SlotsConfiguration.class);
		}
	}
	
	public static SlotsConfiguration getSlotsConfiguration(Slots slots)
	{
		if(instance == null)
		{
			instance = ConfigParser.parse(Slots.pluginName, slots.getConfig(), SlotsConfiguration.class);
		}
		
		return instance;
	}
	
	@OptionalConfigProperty
	@StringConfigProperty(defaultValue = "BLOCK_NOTE_HARP")
	@ConfigProperty(key = "slot-tick-sound")
	private String slotTickSound;

	@DoubleConfigProperty(defaultValue = 5.00, maxValue = Double.POSITIVE_INFINITY, minValue = 0)
	@ConfigProperty(key = "buy-in")
	private double buyIn;
	
	@ConfigElementMap
	@ConfigProperty(key = "jackpots")
	private HashMap<String, Jackpot> jackpots;
	
	public String getSlotTickSound()
	{
		return slotTickSound;
	}
	
	public double getBuyIn()
	{
		return buyIn;
	}
	
	public Jackpot getJackpot(String jackpotTypeId)
	{
		return jackpots.get(jackpotTypeId);
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