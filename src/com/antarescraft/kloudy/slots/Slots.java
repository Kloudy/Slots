package com.antarescraft.kloudy.slots;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.slots.events.CommandEvent;

public class Slots extends HoloGUIPlugin
{	
	public static String pluginName;
	
	@Override
	public void onEnable()
	{
		pluginName = getName();
		
		saveDefaultConfig();
		
		setMinSupportedApiVersion("1.0.6");
		checkMinApiVersion();
		
		SlotsConfiguration.loadConfig(this);
		
		getCommand("slots").setExecutor(new CommandEvent(this));
		
		//copyResourceConfigs(true);
		loadGUIPages();
	}
	
	@Override
	public void onDisable()
	{
		getHoloGUIApi().destroyGUIPages(this);
	}
}