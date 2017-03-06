package com.antarescraft.kloudy.slots.events;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.antarescraft.kloudy.hologuiapi.plugincore.command.CommandHandler;
import com.antarescraft.kloudy.hologuiapi.plugincore.command.CommandParser;
import com.antarescraft.kloudy.hologuiapi.plugincore.messaging.MessageManager;
import com.antarescraft.kloudy.slots.Slots;
import com.antarescraft.kloudy.slots.SlotsConfiguration;
import com.antarescraft.kloudy.slots.pagemodels.SlotsPageModel;

public class CommandEvent implements CommandExecutor
{
	protected Slots slots;
	
	protected SlotsConfiguration config;
	
	public CommandEvent(Slots plugin)
	{
		this.slots = plugin;
		
		config = SlotsConfiguration.getSlotsConfiguration(slots);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		return CommandParser.parseCommand(slots, this, "slots", cmd.getName(), sender, args);
	}
	
	@CommandHandler(description = "Reloads the config files", 
			mustBePlayer = false, permission = "slots.admin", subcommands = "reload")
	public void reload(CommandSender sender, String[] args)
	{
		slots.getHoloGUIApi().destroyGUIPages(slots);

		slots.removeAllPlayers();
		
		SlotsConfiguration.loadConfig(slots);
		
		MessageManager.info(sender, "Reloaded the config");
	}

	@CommandHandler(description = "Opens the Slots GUI",
			mustBePlayer = true, permission = "slots.play", subcommands = "play")
	public void play(CommandSender sender, String[] args)
	{
		Player player = (Player)sender;
		
		if(!slots.isPlaying(player))
		{
			SlotsPageModel model = new SlotsPageModel(slots, slots.getGUIPage("slot-machine"), player);
			slots.getHoloGUIApi().openGUIPage(slots, model);
			
			slots.isPlaying(player, true);
		}
		else
		{
			player.sendMessage(config.getAlreadyPlayingMessage());
		}
	}
}