package com.antarescraft.kloudy.slots;

public enum SlotElement
{
	WILD(null, "question-block.gif"),
	COIN("coin-jackpot", "coin.gif"),
	RING("ring-jackpot", "ring.gif"),
	STAR("star-jackpot", "star.gif"),
	TNT("tnt-jackpot", "tnt.gif"),
	TROPHY("trophy-jackpot", "trophy.gif");
	
	private String typeId;
	private String imageName;
	
	SlotElement(String jackpotTypeId, String imageName)
	{
		this.typeId = jackpotTypeId;
		this.imageName = imageName;
	}
	
	public String getTypeId()
	{
		return typeId;
	}
	
	public String getImageName()
	{
		return imageName;
	}
}