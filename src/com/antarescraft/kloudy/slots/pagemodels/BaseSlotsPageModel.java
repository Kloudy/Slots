package com.antarescraft.kloudy.slots.pagemodels;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.hologuiapi.guicomponentproperties.ImageComponentProperties;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ComponentPosition;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIComponentFactory;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIPage;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ImageComponent;
import com.antarescraft.kloudy.hologuiapi.handlers.GUIPageCloseHandler;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPage;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPageModel;
import com.antarescraft.kloudy.slots.SlotElement;
import com.antarescraft.kloudy.slots.Slots;
import com.antarescraft.kloudy.slots.SlotsConfiguration;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnable;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnableContext;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnableScheduler;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnableTask;
import com.antarescraft.kloudy.slots.util.ThreadSequenceCompleteCallback;

import net.milkbowl.vault.economy.Economy;

public abstract class BaseSlotsPageModel extends PlayerGUIPageModel
{
	protected PlayerGUIPage playerGUIPage;
	
	private ImageComponent slot1;
	private ImageComponent slot2;
	private ImageComponent slot3;
		
	private BukkitIntervalRunnableScheduler slot1Roller;
	private BukkitIntervalRunnableScheduler slot2Roller;
	private BukkitIntervalRunnableScheduler slot3Roller;
	
	private static SlotElement[] slot1Elements = new SlotElement[] {SlotElement.WILD, SlotElement.COIN, SlotElement.TNT, SlotElement.STAR, SlotElement.WILD, SlotElement.COIN, SlotElement.TNT, SlotElement.STAR, SlotElement.WILD, SlotElement.COIN, SlotElement.TNT, SlotElement.WILD, SlotElement.TROPHY, SlotElement.COIN};
	private static SlotElement[] slot2Elements = new SlotElement[] {SlotElement.COIN, SlotElement.TNT, SlotElement.STAR, SlotElement.COIN, SlotElement.TNT, SlotElement.STAR, SlotElement.COIN, SlotElement.TROPHY, SlotElement.COIN, SlotElement.TNT};
	private static SlotElement[] slot3Elements = new SlotElement[] {SlotElement.COIN, SlotElement.TNT, SlotElement.STAR, SlotElement.COIN, SlotElement.TNT, SlotElement.STAR, SlotElement.COIN, SlotElement.TROPHY, SlotElement.COIN, SlotElement.TNT};	
	//tick intervals that the slot elements change while rolling
	private static final int[] intervals = new int[] { 2, 2, 2, 2, 2, 2, 2, 3, 3, 4, 4, 7 };
	
	//array of selected slot elements after the slot finishes rolling
	private SlotElement[] slotResultElements = new SlotElement[3];
	
	protected boolean isRolling = false;
	
	private static HashMap<String, String[][]> imageLines = null;
	
	protected Economy economy;
	
	public BaseSlotsPageModel(HoloGUIPlugin plugin, GUIPage guiPage, Player player)
	{
		super(plugin, guiPage, player);
		
		economy = ((Slots)plugin).getEconomy();
		
		if(imageLines == null)//load the slot images if we haven't already done so
		{
			imageLines = new HashMap<String, String[][]>();
			
			for(SlotElement slotElement : SlotElement.values())
			{
				imageLines.put(slotElement.getImageName(), plugin.loadImage(slotElement.getImageName(), 18, 18, true));
			}
		}
		
		initSlotImages();
		
		guiPage.registerPageCloseHandler(player, new GUIPageCloseHandler()
		{
			@Override
			public void onPageClose()
			{
				stopRollerThreads();
								
				// Player is no longer playing, update the player's 'playing' state.
				Slots slots = (Slots)plugin;
				slots.isPlaying(player, false);
			}
		});
	}
	
	protected abstract void jackpot(SlotElement element);
	protected abstract void noJackpot();
	protected abstract void rollComplete();
	
	/* 
	 * Cancel all of the slot roller threads
	 */
	protected void stopRollerThreads()
	{
		if(slot1Roller != null)slot1Roller.cancel();
		if(slot2Roller != null)slot2Roller.cancel();
		if(slot3Roller != null)slot3Roller.cancel();
	}
	
	/*
	 *Initializes all of the dynamic gui components on the slot machine gui
	 */
	private void initSlotImages()
	{		
		ImageComponentProperties slot1Properties = new ImageComponentProperties();
		slot1Properties.setId("slot1");
		slot1Properties.setImageSource("question-block.gif");
		slot1Properties.setSymmetrical(true);
		slot1Properties.setWidth(18);
		slot1Properties.setHeight(18);
		slot1Properties.setPosition(new ComponentPosition(-0.45, 0.35));
		
		slot1 = GUIComponentFactory.createImageComponent(plugin, slot1Properties);
		
		ImageComponentProperties slot2Properties = new ImageComponentProperties();
		slot2Properties.setId("slot2");
		slot2Properties.setImageSource("question-block.gif");
		slot2Properties.setSymmetrical(true);
		slot2Properties.setWidth(18);
		slot2Properties.setHeight(18);
		slot2Properties.setPosition(new ComponentPosition(0, 0.38));
		
		slot2 = GUIComponentFactory.createImageComponent(plugin, slot2Properties);
		
		ImageComponentProperties slot3Properties = new ImageComponentProperties();
		slot3Properties.setId("slot3");
		slot3Properties.setImageSource("question-block.gif");
		slot3Properties.setSymmetrical(true);
		slot3Properties.setWidth(18);
		slot3Properties.setHeight(18);
		slot3Properties.setPosition(new ComponentPosition(0.44, 0.35));
		
		slot3 = GUIComponentFactory.createImageComponent(plugin, slot3Properties);
	}
	
	/*
	 * Finds the non-wild slotElement in the result.
	 * There is guaranteed to be at least 1 non-wild slot result
	 */
	private SlotElement findNonWildSlot()
	{
		for(SlotElement slotElement : slotResultElements)
		{
			if(slotElement != SlotElement.WILD)
			{
				return slotElement;
			}
		}
		
		return null;
	}
	
	/*
	 * Checks to see if the player won a jackpot and if so awards the player the payout
	 */
	private void checkJackpot()
	{
		// If a wild is rolled, set it to be the first non-wild element in the result
		
		if(slotResultElements[0] == SlotElement.WILD)
		{
			slotResultElements[0] = findNonWildSlot();
		}
		
		if(slotResultElements[1] == SlotElement.WILD)
		{
			slotResultElements[1] = findNonWildSlot();
		}
		
		if(slotResultElements[2] == SlotElement.WILD)
		{
			slotResultElements[2] = findNonWildSlot();
		}
		
		//all three slots have the same element, jackpot!
		if(slotResultElements[0] == slotResultElements[1] && slotResultElements[0] == slotResultElements[2])
		{
			jackpot(slotResultElements[0]);
		}
		else
		{
			noJackpot();
		}
	}
	
	protected void roll(boolean playTickSound)
	{
		roll(0, playTickSound);
	}
	
	protected void roll(long delay, boolean playTickSound)
	{
		roll(null, delay, playTickSound);
	}
	
	protected void roll(SlotElement forcedResult, boolean playTickSound)
	{
		roll(forcedResult, 0, playTickSound);
	}
	
	/*
	 * Rolls the slot machine
	 * 
	 * Force the roll to create jackpot with the specified forcedResult SlotElement. If 'forcedResult' is null, then a random roll will occur
	 */
	protected void roll(SlotElement forcedResult, long delay, boolean playTickSound)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				playerGUIPage.removeComponent(slot1.getProperties().getId());
				playerGUIPage.removeComponent(slot2.getProperties().getId());
				playerGUIPage.removeComponent(slot3.getProperties().getId());
			}
		}.runTaskLater(plugin, delay);

		slot1Roller = new BukkitIntervalRunnableScheduler(plugin, new BukkitIntervalRunnableTask(new RollerThread(slot1, slot1Elements, new Random().nextInt(slot1Elements.length), playTickSound)), intervals,
				new ThreadSequenceCompleteCallback()
				{
					@Override
					public void call(BukkitIntervalRunnableContext context)
					{
						if(forcedResult != null)
						{
							renderForcedResult(slot1, forcedResult);
							
							setResult(0, forcedResult);
						}
						else
						{
							SlotElement element = (SlotElement)context.getContextVariable("selection");
							
							setResult(0, element);
						}
						
						slot2Roller.run();
					}
				}
		);
		
		slot2Roller = new BukkitIntervalRunnableScheduler(plugin, new BukkitIntervalRunnableTask(new RollerThread(slot2, slot2Elements, new Random().nextInt(slot2Elements.length), playTickSound)), intervals, 
				new ThreadSequenceCompleteCallback()
				{
					@Override
					public void call(BukkitIntervalRunnableContext context)
					{
						if(forcedResult != null)
						{
							renderForcedResult(slot2, forcedResult);
							
							setResult(1, forcedResult);
						}
						else
						{
							SlotElement element = (SlotElement)context.getContextVariable("selection");
							
							setResult(1, element);
						}

						slot3Roller.run();
					}
				}
		);
		
		slot3Roller = new BukkitIntervalRunnableScheduler(plugin, new BukkitIntervalRunnableTask(new RollerThread(slot3, slot3Elements, new Random().nextInt(slot3Elements.length), playTickSound)), intervals, 
				new ThreadSequenceCompleteCallback()
				{
					@Override
					public void call(BukkitIntervalRunnableContext context)
					{
						if(forcedResult != null)
						{
							renderForcedResult(slot3, forcedResult);
							
							setResult(2, forcedResult);
						}
						else
						{
							SlotElement element = (SlotElement)context.getContextVariable("selection");
							
							setResult(2, element);
						}

						checkJackpot();
						
						isRolling = false;
						
						rollComplete();
					}
				});
		
		isRolling = true;
		
		slot1Roller.run(delay);
	}
	
	/**
	 * Renders the specified slot image with the input forcedResult
	 * @param slotImage
	 * @param forcedResult
	 */
	private void renderForcedResult(ImageComponent slotImage, SlotElement forcedResult)
	{
		playerGUIPage.removeComponent(slotImage.getProperties().getId());
		
		slotImage.setLines(imageLines.get(forcedResult.getImageName()));
		
		playerGUIPage.renderComponent(slotImage);
	}
	
	/*
	 * Sets the result of the slot roll
	 */
	private void setResult(int slotIndex, SlotElement element)
	{
		slotResultElements[slotIndex] = element;
	}
	
	private class RollerThread implements BukkitIntervalRunnable
	{
		private ImageComponent slotImage;
		private SlotElement[] slotElements;
		private int index;
		private boolean playTickSound;
		
		public RollerThread(ImageComponent slotImage, SlotElement[] slotElements, int index, boolean playTickSound)
		{
			this.slotImage = slotImage;
			this.slotElements = slotElements;
			this.index = index;
			this.playTickSound = playTickSound;
		}
		
		@Override
		public void run(BukkitIntervalRunnableContext context)
		{
			playerGUIPage.removeComponent(slotImage.getProperties().getId());//remove the old image

			if(context.containsKey("index"))
			{
				index = (int) context.getContextVariable("index");
			}
			
			context.setContextVariable("selection", slotElements[index]);
						
			slotImage.setLines(imageLines.get(slotElements[index].getImageName()));
			
			playerGUIPage.renderComponent(slotImage);//render new image

			index = (index + 1) % slotElements.length;
			
			if(playTickSound)
			{
				Sound slotTickSound = Sound.valueOf(SlotsConfiguration.getSlotsConfiguration((Slots)plugin).getSlotTickSound());
				if(slotTickSound != null)
				{
					player.playSound(player.getLocation(), slotTickSound, 0.5f, 1);
				}
			}
		}
		
		@Override
		public RollerThread clone()
		{
			return new RollerThread(slotImage, slotElements, index, playTickSound);
		}
	}
}