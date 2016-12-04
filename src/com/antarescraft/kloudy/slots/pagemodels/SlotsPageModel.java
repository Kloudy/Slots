package com.antarescraft.kloudy.slots.pagemodels;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ButtonComponent;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIPage;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ImageComponent;
import com.antarescraft.kloudy.hologuiapi.handlers.ClickHandler;
import com.antarescraft.kloudy.hologuiapi.handlers.GUIPageCloseHandler;
import com.antarescraft.kloudy.hologuiapi.handlers.GUIPageLoadHandler;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPage;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPageModel;
import com.antarescraft.kloudy.util.BukkitIntervalRunnable;
import com.antarescraft.kloudy.util.BukkitRunnableIntervalScheduler;
import com.antarescraft.kloudy.util.ThreadSequenceCompleteCallback;

public class SlotsPageModel extends PlayerGUIPageModel
{
	private PlayerGUIPage playerGUIPage;
	
	private ButtonComponent closeButton;
	private ButtonComponent rollButton;
	private ImageComponent slot1;
	private ImageComponent slot2;
	private ImageComponent slot3;
	
	private BukkitRunnableIntervalScheduler slot1Roller;
	private BukkitRunnableIntervalScheduler slot2Roller;
	private BukkitRunnableIntervalScheduler slot3Roller;
	private boolean isRolling = false;
	
	//intervals that the slot images change images
	private static final int[] intervals = new int[] { 8, 6, 4, 3, 3, 3, 3, 3, 3, 3, 4, 5, 6, 9 };
	
	//array of images that could fill the slot as the result of a roll
	private static final String[] images = new String[] { "question-block.gif", "coin.gif", "star.gif", "ring.gif", "tnt.gif", "trophy.gif" };
	
	private static HashMap<String, String[][]> imageLines;
	
	public SlotsPageModel(final HoloGUIPlugin plugin, GUIPage guiPage, final Player player)
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
				slot1Roller.cancel();
				slot2Roller.cancel();
				slot3Roller.cancel();
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
		
		slot1Roller = new BukkitRunnableIntervalScheduler(plugin, new RollerThread(slot1), intervals, 
				new ThreadSequenceCompleteCallback()
				{
					@Override
					public void call()
					{
						//runs when the slot stops rolling
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 0.5f, 1);
					}
				});
		
		slot2Roller = new BukkitRunnableIntervalScheduler(plugin, new RollerThread(slot2), intervals, 
				new ThreadSequenceCompleteCallback()
				{
					@Override
					public void call()
					{
						//runs when the slot stops rolling
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 0.5f, 1);
					}
				});
		
		slot3Roller = new BukkitRunnableIntervalScheduler(plugin, new RollerThread(slot3), intervals, 
				new ThreadSequenceCompleteCallback()
				{
					@Override
					public void call()
					{
						//runs when the slot stops rolling
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 0.5f, 1);
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
	
	public class RollerThread extends BukkitIntervalRunnable
	{
		private ImageComponent slotImage;
		private int prevSlotIndex = 0;
		
		public RollerThread(ImageComponent slotImage)
		{
			this.slotImage = slotImage;
		}
		
		@Override
		public void run()
		{
			player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.5f, 1);
			
			playerGUIPage.removeComponent(slotImage.getId());//remove the old image
			
			int imageIndex = new Random().nextInt(images.length-1) + 1;
			if(prevSlotIndex == imageIndex)
			{
				imageIndex++;
				imageIndex = imageIndex % (images.length - 1);
			}
			
			slotImage = slotImage.clone();
			slotImage.setLines(imageLines.get(images[imageIndex]));
			prevSlotIndex = imageIndex;
			
			playerGUIPage.renderComponent(slotImage);//render new image
		}
		
		@Override
		public RollerThread clone()
		{
			return new RollerThread(slotImage);
		}
	}
}