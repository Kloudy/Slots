package com.antarescraft.kloudy.slots.util;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import com.antarescraft.kloudy.slots.Slots;

public class ConfigManager
{
	private Sound slotTickSound;
	private double buyIn;
	
	public void loadConfigValues(Slots plugin)
	{
		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		
		FileConfiguration root = plugin.getConfig();
		
		try
		{
			slotTickSound = Sound.valueOf(root.getString("slot-tick-sound", "BLOCK_NOTE_HARP"));
		}catch(Exception e){}
	}
	
	public Sound getSlotTickSound()
	{
		return slotTickSound;
	}
}