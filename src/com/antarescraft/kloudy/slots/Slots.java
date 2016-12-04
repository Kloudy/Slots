package com.antarescraft.kloudy.slots;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.slots.events.CommandEvent;
import com.antarescraft.kloudy.slots.util.ConfigManager;

public class Slots extends HoloGUIPlugin
{
	private ConfigManager configManager;
	
	@Override
	public void onEnable()
	{
		getCommand("slots").setExecutor(new CommandEvent(this));
		
		copyResourceConfigs(true);
		loadGUIPages();
		
		configManager = new ConfigManager();
		configManager.loadConfigValues(this);
	}
	
	@Override
	public void onDisable()
	{
		getHoloGUIApi().destroyGUIPages(this);
	}
	
	public ConfigManager getConfigManager()
	{
		return configManager;
	}
}