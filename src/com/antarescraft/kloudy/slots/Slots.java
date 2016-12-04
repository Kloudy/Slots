package com.antarescraft.kloudy.slots;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.slots.events.CommandEvent;

public class Slots extends HoloGUIPlugin
{
	@Override
	public void onEnable()
	{
		getCommand("slots").setExecutor(new CommandEvent(this));
		
		copyResourceConfigs(true);
		loadGUIPages();
	}
	
	@Override
	public void onDisable()
	{
		getHoloGUIApi().destroyGUIPages(this);
	}
}