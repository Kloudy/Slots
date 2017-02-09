package com.antarescraft.kloudy.slots.pagemodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.hologuiapi.guicomponentproperties.ImageComponentProperties;
import com.antarescraft.kloudy.hologuiapi.guicomponentproperties.LabelComponentProperties;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ButtonComponent;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ComponentPosition;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIComponentFactory;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIPage;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ImageComponent;
import com.antarescraft.kloudy.hologuiapi.guicomponents.LabelComponent;
import com.antarescraft.kloudy.hologuiapi.handlers.ClickHandler;
import com.antarescraft.kloudy.hologuiapi.handlers.GUIPageCloseHandler;
import com.antarescraft.kloudy.hologuiapi.handlers.GUIPageLoadHandler;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPage;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPageModel;
import com.antarescraft.kloudy.hologuiapi.plugincore.messaging.MessageManager;
import com.antarescraft.kloudy.slots.SlotElement;
import com.antarescraft.kloudy.slots.Slots;
import com.antarescraft.kloudy.slots.SlotsConfiguration;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnable;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnableContext;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnableTask;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnableScheduler;
import com.antarescraft.kloudy.slots.util.ThreadSequenceCompleteCallback;

import net.milkbowl.vault.economy.Economy;

public class SlotsPageModel extends PlayerGUIPageModel
{
	private PlayerGUIPage playerGUIPage;
	
	private SlotsConfiguration config;
	
	private LabelComponent buyInLabel;
	private ButtonComponent closeButton;
	private ButtonComponent rollButton;
	private ImageComponent slot1;
	private ImageComponent slot2;
	private ImageComponent slot3;
	
	private BukkitIntervalRunnableScheduler slot1Roller;
	private BukkitIntervalRunnableScheduler slot2Roller;
	private BukkitIntervalRunnableScheduler slot3Roller;
	private boolean isRolling = false;
	
	//array of selected slot elements after the slot finishes rolling
	SlotElement[] slotResultElements = new SlotElement[3];
	
	//tick intervals that the slot elements change while rolling
	private static final int[] intervals = new int[] { 2, 2, 2, 2, 2, 2, 2, 3, 3, 4, 4, 7 };
	
	private static SlotElement[] slot1Elements = new SlotElement[]{SlotElement.COIN, SlotElement.RING, SlotElement.COIN, SlotElement.TNT, SlotElement.WILD, SlotElement.COIN, SlotElement.TROPHY, SlotElement.WILD};
	private static SlotElement[] slot2Elements = new SlotElement[]{SlotElement.COIN, SlotElement.RING, SlotElement.COIN, SlotElement.TNT, SlotElement.COIN, SlotElement.TROPHY};
	private static SlotElement[] slot3Elements = new SlotElement[]{SlotElement.COIN, SlotElement.RING, SlotElement.COIN, SlotElement.TNT, SlotElement.WILD, SlotElement.COIN, SlotElement.TROPHY, SlotElement.WILD};
	
	private static HashMap<String, String[][]> imageLines;
	
	private Economy economy;
	
	public SlotsPageModel(final HoloGUIPlugin plugin, GUIPage guiPage, final Player player)
	{
		super(plugin, guiPage, player);
				
		config = SlotsConfiguration.getSlotsConfiguration((Slots)plugin);
		
		economy = ((Slots)plugin).getEconomy();
		
		if(imageLines == null)//load the slot images if we haven't already done so
		{
			imageLines = new HashMap<String, String[][]>();
			
			for(SlotElement slotElement : SlotElement.values())
			{
				imageLines.put(slotElement.getImageName(), plugin.loadImage(slotElement.getImageName(), 18, 18, true));
			}
		}
		
		buyInLabel = (LabelComponent)guiPage.getComponent("buy-in");
		closeButton = (ButtonComponent)guiPage.getComponent("close-btn");
		rollButton = (ButtonComponent)guiPage.getComponent("roll-btn");

		initDynamicComponents();
		
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
				
				//render buy-in label
				playerGUIPage.renderComponent(buyInLabel);
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
	private void initDynamicComponents()
	{
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("&6&lBUY IN: &a&l" + config.getBuyIn() + " " + economy.currencyNamePlural());
		
		LabelComponentProperties buyInLabelProperties = new LabelComponentProperties();
		buyInLabelProperties.setId("buy-in");
		buyInLabelProperties.setLabelDistance(6);
		buyInLabelProperties.setLines(lines);
		buyInLabelProperties.setPosition(new ComponentPosition(0, -0.2));
		
		buyInLabel = GUIComponentFactory.createLabelComponent(plugin, buyInLabelProperties);
		
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
		slot2Properties.setPosition(new ComponentPosition(-0, 0.38));
		
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
	
	//Function used for debugging purposes to generate a roll that will result in a jackpot of the specified type
	/*private void createJackpot(SlotElement element)
	{
		slot1Elements = new SlotElement[]{element};
		slot2Elements = new SlotElement[]{element};
		slot3Elements = new SlotElement[]{element};
	}*/
	
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
	
	/*
	 * Sets the result of the slot roll
	 */
	private void setResult(int slotIndex, SlotElement element)
	{
		slotResultElements[slotIndex] = element;
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
	private void roll()
	{
		if(isRolling) return;//already rolling
		
		if(economy.getBalance(player) >= config.getBuyIn())
		{
			economy.withdrawPlayer(player, config.getBuyIn());//withdraw the buy-in amount from the player's account
		}
		else
		{
			player.sendMessage(config.getNotEnoughMoneyMessage());
			
			return;
		}
		
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
		slot1Roller.run(0);
		slot2Roller.run(rollTime());
		slot3Roller.run(rollTime() * 2);
	}
	
	public class RollerThread implements BukkitIntervalRunnable
	{
		private ImageComponent slotImage;
		private SlotElement[] slotElements;
		private int index;
		
		public RollerThread(ImageComponent slotImage, SlotElement[] slotElements, int index)
		{
			this.slotImage = slotImage;
			this.slotElements = slotElements;
			this.index = index;
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
			
			Sound slotTickSound = Sound.valueOf(SlotsConfiguration.getSlotsConfiguration((Slots)plugin).getSlotTickSound());
			if(slotTickSound != null)
			{
				player.playSound(player.getLocation(), slotTickSound, 0.5f, 1);
			}
		}
		
		@Override
		public RollerThread clone()
		{
			return new RollerThread(slotImage, slotElements, index);
		}
	}
}