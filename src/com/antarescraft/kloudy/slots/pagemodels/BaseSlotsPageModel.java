package com.antarescraft.kloudy.slots.pagemodels;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.entity.Player;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.hologuiapi.guicomponentproperties.ImageComponentProperties;
import com.antarescraft.kloudy.hologuiapi.guicomponentproperties.LabelComponentProperties;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ComponentPosition;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIComponentFactory;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIPage;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ImageComponent;
import com.antarescraft.kloudy.hologuiapi.handlers.GUIPageCloseHandler;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPage;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPageModel;
import com.antarescraft.kloudy.hologuiapi.plugincore.messaging.MessageManager;
import com.antarescraft.kloudy.slots.SlotElement;
import com.antarescraft.kloudy.slots.pagemodels.SlotsPageModel.RollerThread;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnableContext;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnableScheduler;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnableTask;
import com.antarescraft.kloudy.slots.util.ThreadSequenceCompleteCallback;

public abstract class BaseSlotsPageModel extends PlayerGUIPageModel
{
	private PlayerGUIPage playerGUIPage;
	
	private ImageComponent slot1;
	private ImageComponent slot2;
	private ImageComponent slot3;
	
	private BukkitIntervalRunnableScheduler slot1Roller;
	private BukkitIntervalRunnableScheduler slot2Roller;
	private BukkitIntervalRunnableScheduler slot3Roller;
	
	private static SlotElement[] slot1Elements = new SlotElement[]{SlotElement.COIN, SlotElement.RING, SlotElement.COIN, SlotElement.TNT, SlotElement.WILD, SlotElement.COIN, SlotElement.TROPHY, SlotElement.WILD};
	private static SlotElement[] slot2Elements = new SlotElement[]{SlotElement.COIN, SlotElement.RING, SlotElement.COIN, SlotElement.TNT, SlotElement.COIN, SlotElement.TROPHY};
	private static SlotElement[] slot3Elements = new SlotElement[]{SlotElement.COIN, SlotElement.RING, SlotElement.COIN, SlotElement.TNT, SlotElement.WILD, SlotElement.COIN, SlotElement.TROPHY, SlotElement.WILD};
	
	//tick intervals that the slot elements change while rolling
	private static final int[] intervals = new int[] { 2, 2, 2, 2, 2, 2, 2, 3, 3, 4, 4, 7 };
	
	//array of selected slot elements after the slot finishes rolling
	private SlotElement[] slotResultElements = new SlotElement[3];
	
	protected boolean isRolling = false;
	
	public BaseSlotsPageModel(HoloGUIPlugin plugin, GUIPage guiPage, Player player)
	{
		super(plugin, guiPage, player);
		
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
	
	/*
	 *Initializes all of the dynamic gui components on the slot machine gui
	 */
	protected void initSlotImages()
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
			//deposit the jackpot payout amount into the player's account
			double payout = config.getJackpot(slotResultElements[0].getTypeId()).getPayout();
			economy.depositPlayer(player, payout);
			
			MessageManager.success(player, "Jackpot! You won " + payout  + economy.currencyNamePlural() + "!");
		}
	}
	
	/*
	 * Rolls the slot machine
	 */
	protected void roll()
	{
		playerGUIPage.removeComponent(slot1.getProperties().getId());
		playerGUIPage.removeComponent(slot2.getProperties().getId());
		playerGUIPage.removeComponent(slot3.getProperties().getId());
		
		slot1Roller = new BukkitIntervalRunnableScheduler(plugin, new BukkitIntervalRunnableTask(new RollerThread(slot1, slot1Elements, new Random().nextInt(slot1Elements.length))), intervals,
				new ThreadSequenceCompleteCallback()
				{
					@Override
					public void call(BukkitIntervalRunnableContext context)
					{
						SlotElement element = (SlotElement)context.getContextVariable("selection");
						setResult(0, element);
						
						slot2Roller.run();
					}
				}
		);
		
		slot2Roller = new BukkitIntervalRunnableScheduler(plugin, new BukkitIntervalRunnableTask(new RollerThread(slot2, slot2Elements, new Random().nextInt(slot2Elements.length))), intervals, 
				new ThreadSequenceCompleteCallback()
				{
					@Override
					public void call(BukkitIntervalRunnableContext context)
					{
						SlotElement element = (SlotElement)context.getContextVariable("selection");
						setResult(1, element);
						
						slot3Roller.run();
					}
				}
		);
		
		slot3Roller = new BukkitIntervalRunnableScheduler(plugin, new BukkitIntervalRunnableTask(new RollerThread(slot3, slot3Elements, new Random().nextInt(slot3Elements.length))), intervals, 
				new ThreadSequenceCompleteCallback()
				{
					@Override
					public void call(BukkitIntervalRunnableContext context)
					{
						SlotElement element = (SlotElement)context.getContextVariable("selection");
						setResult(2, element);
						
						checkJackpot();
						
						isRolling = false;
					}
				});
		
		isRolling = true;
		
		slot1Roller.run();
	}
	
	/*
	 * Sets the result of the slot roll
	 */
	private void setResult(int slotIndex, SlotElement element)
	{
		slotResultElements[slotIndex] = element;
	}
}