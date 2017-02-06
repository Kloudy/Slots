package com.antarescraft.kloudy.slots;

import java.util.HashMap;

import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigObject;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigParser;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.PassthroughParams;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.annotations.ConfigElementMap;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.annotations.ConfigProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.annotations.DoubleConfigProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.annotations.OptionalConfigProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.annotations.StringConfigProperty;
import com.antarescraft.kloudy.hologuiapi.plugincore.messaging.MessageManager;

public class SlotsConfiguration implements ConfigObject
{
	private static SlotsConfiguration instance;
	
	public static void loadConfig(Slots slots)
	{
		slots.saveDefaultConfig();
		slots.reloadConfig();
		slots.loadGUIPages();
		
		if(instance == null)
		{
			instance = ConfigParser.parse(slots.getConfig(), SlotsConfiguration.class, Slots.pluginName);
		}
	}
	
	public static SlotsConfiguration getSlotsConfiguration(Slots slots)
	{
		if(instance == null)
		{
			instance = ConfigParser.parse(slots.getConfig(), SlotsConfiguration.class, Slots.pluginName);
		}
		
		return instance;
	}
	
	@OptionalConfigProperty
	@StringConfigProperty(defaultValue = "BLOCK_NOTE_HARP")
	@ConfigProperty(key = "slot-tick-sound")
	private String slotTickSound;

	@DoubleConfigProperty(defaultValue = 5.00, maxValue = Double.MAX_VALUE, minValue = 0)
	@ConfigProperty(key = "buy-in")
	private double buyIn;
	
	@ConfigProperty(key = "not-enough-money")
	private String notEnoughMoneyMessage;
	
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
	
	public String getNotEnoughMoneyMessage()
	{
		return MessageManager.setFormattingCodes(notEnoughMoneyMessage);
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

	@Override
	public void configParseComplete(PassthroughParams params) {}
}