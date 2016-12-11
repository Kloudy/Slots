package com.antarescraft.kloudy.slots;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.hologuiapi.plugincore.config.ConfigParser;
import com.antarescraft.kloudy.slots.events.CommandEvent;

public class Slots extends HoloGUIPlugin
{
	private SlotsConfiguration slotsConfig;
	
	@Override
	public void onEnable()
	{
		saveDefaultConfig();
		
		setMinSupportedApiVersion("1.0.3");
		checkMinApiVersion();
		
		reloadSlotsConfig();
		
		getCommand("slots").setExecutor(new CommandEvent(this));
		
		//copyResourceConfigs(true);
		loadGUIPages();
	}
	
	@Override
	public void onDisable()
	{
		getHoloGUIApi().destroyGUIPages(this);
	}
	
	public void reloadSlotsConfig()
	{
		reloadConfig();

		try 
		{
			slotsConfig = ConfigParser.parse(getConfig().getRoot(), SlotsConfiguration.class, String.format("plugins/%s/config-docs.yml", getName()), 25);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public SlotsConfiguration getSlotsConfig()
	{
		return slotsConfig;
	}
}