package com.antarescraft.kloudy.slots.pagemodels;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.antarescraft.kloudy.hologuiapi.guicomponents.ButtonComponent;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIPage;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ImageComponent;
import com.antarescraft.kloudy.hologuiapi.handlers.ClickHandler;
import com.antarescraft.kloudy.hologuiapi.handlers.GUIPageCloseHandler;
import com.antarescraft.kloudy.hologuiapi.handlers.GUIPageLoadHandler;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPage;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPageModel;
import com.antarescraft.kloudy.slots.Slots;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnable;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnableContext;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnableTask;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnableScheduler;
import com.antarescraft.kloudy.slots.util.ThreadSequenceCompleteCallback;

public class SlotsPageModel extends PlayerGUIPageModel
{
	private PlayerGUIPage playerGUIPage;
	
	private ButtonComponent closeButton;
	private ButtonComponent rollButton;
	private ImageComponent slot1;
	private ImageComponent slot2;
	private ImageComponent slot3;
	
	private BukkitIntervalRunnableScheduler slot1Roller;
	private BukkitIntervalRunnableScheduler slot2Roller;
	private BukkitIntervalRunnableScheduler slot3Roller;
	private boolean isRolling = false;
	
	//intervals that the slot images change images
	private static final int[] intervals = new int[] { 2, 2, 2, 2, 2, 2, 2, 3, 3, 4, 4, 7, 7 };
	
	//array of images that could fill the slot as the result of a roll
	private static final String[] images = new String[] { "question-block.gif", "coin.gif", "star.gif", "ring.gif", "tnt.gif", "trophy.gif" };
	
	private static HashMap<String, String[][]> imageLines;
	
	public SlotsPageModel(final Slots plugin, GUIPage guiPage, final Player player)
	{
		super(plugin, guiPage, player);
		
		if(imageLines == null)//load the slot images if we haven't already done so
		{
			imageLines = new HashMap<String, String[][]>();
			
			for(String image : images)
			{
				imageLines.put(image, plugin.loadImage(image, 18, 18, true));
			}
		}
		
		closeButton = (ButtonComponent)guiPage.getComponent("close-btn");
		rollButton = (ButtonComponent)guiPage.getComponent("roll-btn");
		slot1 = (ImageComponent)guiPage.getComponent("slot-1").clone();
		slot2 = (ImageComponent)guiPage.getComponent("slot-2").clone();
		slot3 = (ImageComponent)guiPage.getComponent("slot-3").clone();
		
		closeButton.registerClickHandler(player, new ClickHandler()
		{
			@Override
			public void onClick()
			{
				plugin.getHoloGUIApi().closeGUIPage(player);
			}
		});
		
		rollButton.registerClickHandler(player, new ClickHandler()
		{
			@Override
			public void onClick()
			{
				roll();
			}
		});
				
		guiPage.registerPageLoadHandler(player, new GUIPageLoadHandler()
		{
			@Override
			public void onPageLoad(PlayerGUIPage _playerGUIPage)
			{
				playerGUIPage = _playerGUIPage;
				
				//render the slot images
				playerGUIPage.renderComponent(slot1);
				playerGUIPage.renderComponent(slot2);
				playerGUIPage.renderComponent(slot3);
			}
		});
		
		guiPage.registerPageCloseHandler(player, new GUIPageCloseHandler()
		{
			@Override
			public void onPageClose()
			{
				//cancel all of the slot roller threads
				if(slot1Roller != null)slot1Roller.cancel();
				if(slot2Roller != null)slot2Roller.cancel();
				if(slot3Roller != null)slot3Roller.cancel();
			}
		});
	}
	
	/**
	 * Returns the total amount of time it takes to complete one slot roll
	 */
	private static int rollTime()
	{
		int t = 0;
		for(int interval : intervals)
		{
			t += interval;
		}
		
		t+= 5;//add 10 tick delay to the end
		
		return t;
	}
	
	private void isRolling(boolean isRolling)
	{
		this.isRolling = isRolling;
	}
	
	/*
	 * Rolls the slot machine
	 */
	private void roll()
	{
		if(isRolling) return;//already rolling
		
		playerGUIPage.removeComponent(slot1.getId());
		playerGUIPage.removeComponent(slot2.getId());
		playerGUIPage.removeComponent(slot3.getId());
		
		slot1Roller = new BukkitIntervalRunnableScheduler(plugin, new BukkitIntervalRunnableTask(new RollerThread(slot1)), intervals);
		
		slot2Roller = new BukkitIntervalRunnableScheduler(plugin, new BukkitIntervalRunnableTask(new RollerThread(slot2)), intervals);
		
		slot3Roller = new BukkitIntervalRunnableScheduler(plugin, new BukkitIntervalRunnableTask(new RollerThread(slot3)), intervals, 
				new ThreadSequenceCompleteCallback()
				{
					@Override
					public void call(BukkitIntervalRunnableContext context)
					{
						
					}
				});
		
		isRolling = true;
		slot1Roller.run(0);
		slot2Roller.run(rollTime());
		slot3Roller.run(rollTime() * 2);
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				isRolling(false);
			}
		}.runTaskLater(plugin, rollTime() * 3);//isRolling is false after all the threads have finished rolling
	}
	
	public class RollerThread implements BukkitIntervalRunnable
	{
		private ImageComponent slotImage;
		
		public RollerThread(ImageComponent slotImage)
		{
			this.slotImage = slotImage;
		}
		
		@Override
		public void run(BukkitIntervalRunnableContext context)
		{
			playerGUIPage.removeComponent(slotImage.getId());//remove the old image
						
			int prevIndex = 0;
			if(context.containsKey("prevIndex"))
			{
				prevIndex = (int)context.getContextVariable("prevIndex");
			}
			
			int imageIndex = new Random().nextInt(images.length-1) + 1;
			if(prevIndex == imageIndex)
			{
				imageIndex = (imageIndex % (images.length-1)) + 1;
			}
			
			slotImage = slotImage.clone();
			slotImage.setLines(imageLines.get(images[imageIndex]));
			
			context.setContextVariable("prevIndex", imageIndex);
			
			playerGUIPage.renderComponent(slotImage);//render new image
			
			Sound slotTickSound = ((Slots)plugin).getConfigManager().getSlotTickSound();
			if(slotTickSound != null)
			{
				player.playSound(player.getLocation(), slotTickSound, 0.5f, 1);
			}
		}
		
		@Override
		public RollerThread clone()
		{
			return new RollerThread(slotImage);
		}
	}
}