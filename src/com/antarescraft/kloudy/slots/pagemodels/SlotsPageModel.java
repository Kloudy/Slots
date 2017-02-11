package com.antarescraft.kloudy.slots.pagemodels;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.hologuiapi.guicomponentproperties.LabelComponentProperties;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ButtonComponent;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ComponentPosition;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIComponentFactory;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIPage;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ImageComponent;
import com.antarescraft.kloudy.hologuiapi.guicomponents.LabelComponent;
import com.antarescraft.kloudy.hologuiapi.handlers.ClickHandler;
import com.antarescraft.kloudy.hologuiapi.handlers.GUIPageLoadHandler;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPage;
import com.antarescraft.kloudy.hologuiapi.plugincore.messaging.MessageManager;
import com.antarescraft.kloudy.slots.SlotElement;
import com.antarescraft.kloudy.slots.Slots;
import com.antarescraft.kloudy.slots.SlotsConfiguration;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnable;
import com.antarescraft.kloudy.slots.util.BukkitIntervalRunnableContext;

import net.milkbowl.vault.economy.Economy;

public class SlotsPageModel extends BaseSlotsPageModel
{
	private PlayerGUIPage playerGUIPage;
	
	private SlotsConfiguration config;
	
	private LabelComponent buyInLabel;
	private ButtonComponent closeButton;
	private ButtonComponent rollButton;
	private ButtonComponent tutorialButton;
	
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
		tutorialButton = (ButtonComponent)guiPage.getComponent("tutorial-btn");

		ArrayList<String> lines = new ArrayList<String>();
		lines.add("&6&lBUY IN: &a&l" + config.getBuyIn() + " " + economy.currencyNamePlural());
		
		LabelComponentProperties buyInLabelProperties = new LabelComponentProperties();
		buyInLabelProperties.setId("buy-in");
		buyInLabelProperties.setLabelDistance(6);
		buyInLabelProperties.setLines(lines);
		buyInLabelProperties.setPosition(new ComponentPosition(0, -0.3));
		
		buyInLabel = GUIComponentFactory.createLabelComponent(plugin, buyInLabelProperties);
		
		initSlotImages();
		
		closeButton.registerClickHandler(player, new ClickHandler()
		{
			@Override
			public void onClick()
			{
				plugin.getHoloGUIApi().closeGUIPage(player);
				
				// Player is no longer playing, update the player's 'playing' state.
				Slots slots = (Slots)plugin;
				slots.isPlaying(player, false);
			}
		});
		
		rollButton.registerClickHandler(player, new ClickHandler()
		{
			@Override
			public void onClick()
			{
				if(isRolling) return;//already rolling
				
				if(economy.getBalance(player) >= config.getBuyIn())
				{
					economy.withdrawPlayer(player, config.getBuyIn());//withdraw the buy-in amount from the player's account
				
					isRolling = true;
					
					roll();
				}
				else
				{
					player.sendMessage(config.getNotEnoughMoneyMessage());
					
					return;
				}
			}
		});
		
		tutorialButton.registerClickHandler(player, new ClickHandler()
		{
			@Override
			public void onClick()
			{
				TutorialPageModel model = new TutorialPageModel(plugin, plugin.getGUIPage("tutorial"), player);
				plugin.getHoloGUIApi().openGUIPage(plugin, player, model);
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
	
	@Override
	private void jackpot(SlotElement elementJackpot)
	{
		//deposit the jackpot payout amount into the player's account
		double payout = config.getJackpot(slotResultElements[0].getTypeId()).getPayout();
		economy.depositPlayer(player, payout);
		
		MessageManager.success(player, "Jackpot! You won " + payout  + economy.currencyNamePlural() + "!");
	}
}