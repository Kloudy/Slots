package com.antarescraft.kloudy.slots.pagemodels;

import org.bukkit.entity.Player;

import com.antarescraft.kloudy.hologuiapi.HoloGUIPlugin;
import com.antarescraft.kloudy.hologuiapi.guicomponentproperties.ImageComponentProperties;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ComponentPosition;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIComponentFactory;
import com.antarescraft.kloudy.hologuiapi.guicomponents.GUIPage;
import com.antarescraft.kloudy.hologuiapi.guicomponents.ImageComponent;
import com.antarescraft.kloudy.hologuiapi.handlers.GUIPageLoadHandler;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPage;
import com.antarescraft.kloudy.hologuiapi.playerguicomponents.PlayerGUIPageModel;

public class TutorialPageModel extends PlayerGUIPageModel
{
	private PlayerGUIPage playerGUIPage;
	
	private ImageComponent slot1;
	private ImageComponent slot2;
	private ImageComponent slot3;
	
	public TutorialPageModel(HoloGUIPlugin plugin, GUIPage guiPage, Player player)
	{
		super(plugin, guiPage, player);
		
		guiPage.registerPageLoadHandler(player, new GUIPageLoadHandler()
		{
			@Override
			public void onPageLoad(PlayerGUIPage loadedPlayerGUIPage)
			{
				playerGUIPage = loadedPlayerGUIPage;
				
				initSlotImages();
				
				
			}
		});
	}
	
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
}