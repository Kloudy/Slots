package com.antarescraft.kloudy.slots.pagemodels;

import org.bukkit.entity.Player;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ButtonComponent;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIPage;
import com.antarescraft.kloudy.hologuiapi.handlers.ClickHandler;
import com.antarescraft.kloudy.hologuiapi.handlers.GUIPageLoadHandler;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPage;

public class TutorialPageModel extends BaseSlotsPageModel
{
	private ButtonComponent doneButton;
	
	public TutorialPageModel(HoloGUIPlugin plugin, GUIPage guiPage, Player player)
	{
		super(plugin, guiPage, player);
		
		guiPage.registerPageLoadHandler(player, new GUIPageLoadHandler()
		{
			@Override
			public void onPageLoad(PlayerGUIPage loadedPlayerGUIPage)
			{
				
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
}