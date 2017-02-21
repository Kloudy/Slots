package com.antarescraft.kloudy.slots;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.hologuiapi.plugincore.messaging.MessageManager;

import net.milkbowl.vault.economy.Economy;

public abstract class Slots extends HoloGUIPlugin
{	
	public static String pluginName = null;
	
	private Economy economy;
	
	private HashSet<UUID> players = new HashSet<UUID>();
	
	@Override
	public void onEnable()
	{	
		pluginName = getName();
		
		setMinSupportedApiVersion("1.0.9");
		checkMinApiVersion();
		
		getHoloGUIApi().hookHoloGUIPlugin(this);
		
		SlotsConfiguration.loadConfig(this);
				
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
		getHoloGUIApi().unhookHoloGUIPlugin(this);
		
		players.clear();
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
	
	/**
	 * @param player The player
	 * @return true if the player is currently playing the game. Returns false otherwise.
	 */
	public boolean isPlaying(Player player)
	{
		return players.contains(player.getUniqueId());
	}
	
	/**
	 * Sets the player's 'playing' state to the input value
	 * 
	 * @param player The player
	 * @param value true | false if the player is currently playing
	 */
	public void isPlaying(Player player, boolean value)
	{
		if(value)
		{
			players.add(player.getUniqueId());
		}
		else
		{
			players.remove(player.getUniqueId());
		}
	}
	
	public void removeAllPlayers()
	{
		players.clear();
	}
}