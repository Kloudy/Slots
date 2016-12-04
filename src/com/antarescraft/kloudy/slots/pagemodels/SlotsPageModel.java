package com.antarescraft.kloudy.slots.pagemodels;

import org.bukkit.entity.Player;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ButtonComponent;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIPage;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ImageComponent;
import com.antarescraft.kloudy.hologuiapi.handlers.ClickHandler;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPageModel;

public class SlotsPageModel extends PlayerGUIPageModel
{
	private ButtonComponent closeButton;
	private ButtonComponent rollButton;
	private ImageComponent slot1;
	private ImageComponent slot2;
	private ImageComponent slot3;

	public SlotsPageModel(final HoloGUIPlugin plugin, GUIPage guiPage, final Player player)
	{
		super(plugin, guiPage, player);
		
		closeButton = (ButtonComponent)guiPage.getComponent("close-btn");
		rollButton = (ButtonComponent)guiPage.getComponent("roll-btn");
		slot1 = (ImageComponent)guiPage.getComponent("slot-1");
		slot2 = (ImageComponent)guiPage.getComponent("slot-2");
		slot3 = (ImageComponent)guiPage.getComponent("slot-3");
		
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
				
			}
		});
	}
}