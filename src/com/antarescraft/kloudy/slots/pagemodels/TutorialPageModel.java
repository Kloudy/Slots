package com.antarescraft.kloudy.slots.pagemodels;

import org.bukkit.entity.Player;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ButtonComponent;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIPage;
import com.antarescraft.kloudy.hologuiapi.handlers.ClickHandler;
import com.antarescraft.kloudy.hologuiapi.handlers.GUIPageLoadHandler;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPage;
import com.antarescraft.kloudy.slots.SlotElement;

public class TutorialPageModel extends BaseSlotsPageModel
{
	private ButtonComponent doneButton;
	
	private static SlotElement[] forcedResults = new SlotElement[] { SlotElement.COIN, SlotElement.RING, SlotElement.STAR, SlotElement.TNT, SlotElement.TROPHY };
	
	private int resultIndex = 0;
	
	public TutorialPageModel(HoloGUIPlugin plugin, GUIPage guiPage, Player player)
	{
		super(plugin, guiPage, player);
		
		guiPage.registerPageLoadHandler(player, new GUIPageLoadHandler()
		{
			@Override
			public void onPageLoad(PlayerGUIPage _playerGUIPage)
			{
				playerGUIPage = _playerGUIPage;
			}
		});
		
		doneButton = (ButtonComponent)guiPage.getComponent("done-btn");
		doneButton.registerClickHandler(player, new ClickHandler()
		{
			@Override
			public void onClick()
			{
				SlotsPageModel model = new SlotsPageModel(plugin, plugin.getGUIPage("slot-machine"), player);
				plugin.getHoloGUIApi().openGUIPage(plugin, player, model);
			}
		});
	}
	
	@Override
	public void rollComplete()
	{
		resultIndex = (resultIndex + 1) % forcedResults.length;
		
		roll(forcedResults[resultIndex], 20); // waits a second before rolling again
	}
	
	@Override
	public void jackpot(SlotElement element){}
}