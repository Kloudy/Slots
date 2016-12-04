package com.antarescraft.kloudy.slots.util;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Wrapper class that wraps the runnable method and passes in the context
 */
public class BukkitIntervalRunnableTask extends BukkitRunnable
{		
	private BukkitIntervalRunnableContext context;
	private BukkitIntervalRunnable thread;
	
	public BukkitIntervalRunnableTask(BukkitIntervalRunnable thread)
	{
		this.thread = thread;
	}
	
	public void setContext(BukkitIntervalRunnableContext context)
	{
		this.context = context;
	}

	public BukkitIntervalRunnableTask clone()
	{
		return new BukkitIntervalRunnableTask(thread);
	}
	
	public void run()
	{
		thread.run(context);
	}
}