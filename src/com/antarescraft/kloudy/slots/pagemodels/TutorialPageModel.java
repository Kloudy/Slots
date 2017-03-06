package com.antarescraft.kloudy.slots.pagemodels;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
import com.antarescraft.kloudy.slots.SlotElement;
import com.antarescraft.kloudy.slots.Slots;
import com.antarescraft.kloudy.slots.SlotsConfiguration;

public class TutorialPageModel extends BaseSlotsPageModel
{
	private ButtonComponent doneButton;
	private ButtonComponent closeButton;
	private LabelComponent jackpotDetails;
	
	private static SlotElement[] forcedResults = new SlotElement[] { SlotElement.COIN, SlotElement.TNT, SlotElement.STAR, SlotElement.TROPHY };
	
	private int resultIndex = 0;
	
	public TutorialPageModel(HoloGUIPlugin plugin, GUIPage guiPage, Player player)
	{
		super(plugin, guiPage, player);
		
		LabelComponentProperties labelProperties = new LabelComponentProperties();
		labelProperties.setId("jackpot-details");
		labelProperties.setLines(new ArrayList<String>());
		labelProperties.setPosition(new ComponentPosition(0, 0.4));
		labelProperties.setLabelDistance(6);
		
		jackpotDetails = GUIComponentFactory.createLabelComponent(plugin, labelProperties);
		
		closeButton = (ButtonComponent)guiPage.getComponent("close-btn");
		
		closeButton.registerClickHandler(player, new ClickHandler()
		{
			@Override
			public void onClick()
			{
				plugin.getHoloGUIApi().closeGUIPage(player);
			}
		});
		
		guiPage.registerPageLoadHandler(player, new GUIPageLoadHandler()
		{
			@Override
			public void onPageLoad(PlayerGUIPage _playerGUIPage)
			{
				playerGUIPage = _playerGUIPage;
				
				roll(forcedResults[resultIndex], false);
			}
		});
		
		doneButton = (ButtonComponent)guiPage.getComponent("done-btn");
		doneButton.registerClickHandler(player, new ClickHandler()
		{
			@Override
			public void onClick()
			{
				SlotsPageModel model = new SlotsPageModel(plugin, plugin.getGUIPage("slot-machine"), player);
				plugin.getHoloGUIApi().openGUIPage(plugin, model);
			}
		});
	}
	
	@Override
	public void rollComplete()
	{
		SlotsConfiguration config = SlotsConfiguration.getSlotsConfiguration((Slots)plugin);
		String jackpotName = config.getJackpot(forcedResults[resultIndex].getTypeId()).getName();
		double payout = config.getJackpot(forcedResults[resultIndex].getTypeId()).getPayout();
		
		ArrayList<String> lines = new ArrayList<String>();
		lines.add(String.format("&6&l%s", jackpotName, Double.toString(payout)));
		lines.add("");
		lines.add(String.format("&lPayout: &a&l%s %s", payout, economy.currencyNamePlural()));
		jackpotDetails.setLines(lines);
		
		playerGUIPage.renderComponent(jackpotDetails);
		
		resultIndex = (resultIndex + 1) % forcedResults.length;
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				playerGUIPage.removeComponent("jackpot-details");
			}
		}.runTaskLater(plugin, 60);
		
		roll(forcedResults[resultIndex], 60, false); // waits a second before rolling again
	}
	
	@Override
	public void jackpot(SlotElement element){}
	
	@Override
	public void noJackpot(){}
}