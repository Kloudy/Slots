package com.antarescraft.kloudy.slots;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.hologuiapi.plugincore.messaging.MessageManager;
import com.antarescraft.kloudy.slots.events.CommandEvent;

import net.milkbowl.vault.economy.Economy;

public class Slots extends HoloGUIPlugin
{	
	public static String pluginName;
	
	private Economy economy;
	
	@Override
	public void onEnable()
	{
		pluginName = getName();
				
		setMinSupportedApiVersion("1.0.6");
		checkMinApiVersion();
		
		SlotsConfiguration.loadConfig(this);
		
		getCommand("slots").setExecutor(new CommandEvent(this));
		
		copyResourceConfigs(true);
		
		if(!setupEconomy())
		{
			MessageManager.error(Bukkit.getConsoleSender(), "Slots requires Vault be installed on your server!");
		}
	}
	
	@Override
	public void onDisable()
	{
		getHoloGUIApi().destroyGUIPages(this);
	}
	
	private boolean setupEconomy() 
	{
        if (getServer().getPluginManager().getPlugin("Vault") == null)
        {
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        
        economy = rsp.getProvider();
        
        return economy != null;
    }
	
	public Economy getEconomy()
	{
		return economy;
	}
}