package com.antarescraft.kloudy.util;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class BukkitIntervalRunnable extends BukkitRunnable
{
	public abstract BukkitIntervalRunnable clone();
}