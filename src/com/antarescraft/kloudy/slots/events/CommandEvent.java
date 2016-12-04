package com.antarescraft.kloudy.slots.events;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.antarescraft.kloudy.hologuiapi.plugincore.command.CommandHandler;
import com.antarescraft.kloudy.hologuiapi.plugincore.command.CommandParser;
import com.antarescraft.kloudy.plugincore.messaging.MessageManager;
import com.antarescraft.kloudy.slots.Slots;
import com.antarescraft.kloudy.slots.pagemodels.SlotsPageModel;

public class CommandEvent implements CommandExecutor
{
	private Slots plugin;
	
	public CommandEvent(Slots plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		return CommandParser.parseCommand(plugin, this, "slots", cmd.getName(), sender, args);
	}
	
	@CommandHandler(description = "Reloads the config files", 
			mustBePlayer = false, permission = "slots.admin", subcommands = "reload")
	public void reload(CommandSender sender, String[] args)
	{
		plugin.getHoloGUIApi().destroyGUIPages(plugin);
		plugin.getConfigManager().loadConfigValues(plugin);
		plugin.loadGUIPages();
		
		MessageManager.info(sender, "Reloaded the config");
	}

	@CommandHandler(description = "Opens the Slots GUI",
			mustBePlayer = true, permission = "slots.play", subcommands = "play")
	public void play(CommandSender sender, String[] args)
	{
		Player player = (Player)sender;
		
		SlotsPageModel model = new SlotsPageModel(plugin, plugin.getGUIPage("slot-machine"), player);
		plugin.getHoloGUIApi().openGUIPage(plugin, player, model);
	}
}