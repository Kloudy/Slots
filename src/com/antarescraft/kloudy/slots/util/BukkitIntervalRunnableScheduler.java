package com.antarescraft.kloudy.slots.util;

import java.util.ArrayList;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * BukkitRunnableIntervalExecutor
 * 
 * Schedules BukkitRunnables to run with the given tick intervals
 */
public class BukkitIntervalRunnableScheduler 
{
	private Plugin plugin;
	private BukkitIntervalRunnableTask thread;
	private int[] intervals;
	private ThreadSequenceCompleteCallback callback;
	private BukkitIntervalRunnableContext context;
	private ArrayList<BukkitRunnable> threads;
	
	public BukkitIntervalRunnableScheduler(Plugin plugin, BukkitIntervalRunnableTask thread, int[] intervals)
	{
		this(plugin, thread, intervals, null);
	}
	
	public BukkitIntervalRunnableScheduler(Plugin plugin, BukkitIntervalRunnableTask thread, int[] intervals, 
			ThreadSequenceCompleteCallback callback)
	{		
		this.plugin = plugin;
		this.thread = thread;
		this.intervals = intervals;
		this.callback = callback;
		
		threads = new ArrayList<BukkitRunnable>();
		context = new BukkitIntervalRunnableContext();
	}
	
	/**
	 * Starts running the thread sequence
	 */
	public void run(long delay)
	{
		thread.setContext(context);
		threads.add(thread);
		
		//Schedule the first thread
		thread.runTaskLater(plugin, delay);
		
		//Schedule the following thread intervals
		long t = delay;
		for(int i = 0; i < intervals.length; i++)
		{
			t += intervals[i];
			
			BukkitIntervalRunnableTask threadClone = thread.clone();
			threadClone.setContext(context);
			
			threads.add(threadClone);
			threadClone.runTaskLater(plugin, t);
			
			if(i == intervals.length - 1 && callback != null)//final thread, run the complete callback function if it exists
			{
				BukkitRunnable callbackThread = new BukkitRunnable()
				{
					@Override
					public void run()
					{
						callback.call(context);
					}
				};
				callbackThread.runTaskLater(plugin, t);
				
				threads.add(callbackThread);
			}
		}
	}
	
	/**
	 * Cancels all of the threads in the sequence
	 */
	public void cancel()
	{
		for(BukkitRunnable thread : threads)
		{
			try
			{
				thread.cancel();
			}
			catch(Exception e){}
		}
	}
}