package com.antarescraft.kloudy.slots;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigParser;
import com.antarescraft.kloudy.slots.events.CommandEvent;

public class Slots extends HoloGUIPlugin
{	
	@Override
	public void onEnable()
	{
		saveDefaultConfig();
		
		setMinSupportedApiVersion("1.0.2");
		checkMinApiVersion();
		
		getSlotsConfig();
		
		getCommand("slots").setExecutor(new CommandEvent(this));
		
		//copyResourceConfigs(true);
		loadGUIPages();
	}
	
	@Override
	public void onDisable()
	{
		getHoloGUIApi().destroyGUIPages(this);
	}
	
	
	public SlotsConfiguration getSlotsConfig()
	{
		reloadConfig();
		
		SlotsConfiguration config = null;
		
		try 
		{
			config = ConfigParser.parse(getConfig().getRoot(), SlotsConfiguration.class);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return config;
	}
}