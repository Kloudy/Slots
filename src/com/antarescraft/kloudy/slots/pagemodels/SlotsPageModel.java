package com.antarescraft.kloudy.slots.pagemodels;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.hologuiapi.guicomponentproperties.LabelComponentProperties;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ButtonComponent;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ComponentPosition;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIComponentFactory;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIPage;
import com.antarescraft.kloudy.hologuiapi.guicomponents.LabelComponent;
import com.antarescraft.kloudy.hologuiapi.handlers.ClickHandler;
import com.antarescraft.kloudy.hologuiapi.handlers.GUIPageLoadHandler;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPage;
import com.antarescraft.kloudy.hologuiapi.plugincore.messaging.MessageManager;
import com.antarescraft.kloudy.slots.SlotElement;
import com.antarescraft.kloudy.slots.Slots;
import com.antarescraft.kloudy.slots.SlotsConfiguration;

import net.milkbowl.vault.economy.Economy;

public class SlotsPageModel extends BaseSlotsPageModel
{
	private SlotsConfiguration config;
	
	private LabelComponent buyInLabel;
	private ButtonComponent closeButton;
	private ButtonComponent rollButton;
	private ButtonComponent tutorialButton;
		
	private Economy economy;
	
	public SlotsPageModel(final HoloGUIPlugin plugin, GUIPage guiPage, final Player player)
	{
		super(plugin, guiPage, player);
				
		config = SlotsConfiguration.getSlotsConfiguration((Slots)plugin);
		
		economy = ((Slots)plugin).getEconomy();
		
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
				
				// render buy-in label
				playerGUIPage.renderComponent(buyInLabel);
			}
		});
	}
	
	@Override
	protected void jackpot(SlotElement elementJackpot)
	{
		// deposit the jackpot payout amount into the player's account
		double payout = config.getJackpot(elementJackpot.getTypeId()).getPayout();
		economy.depositPlayer(player, payout);
		
		MessageManager.success(player, "Jackpot! You won " + payout  + economy.currencyNamePlural() + "!");
	}
	
	@Override
	public void rollComplete(){}
}