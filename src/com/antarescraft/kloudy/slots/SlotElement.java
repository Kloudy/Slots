package com.antarescraft.kloudy.slots;

public enum SlotElement
{
	QUESTION(null, "question-block.gif"),
	COIN("coin-jackpot", "coin.gif"),
	RING("ring-jackpot", "ring.gif"),
	STAR("star-jackpot", "star.gif"),
	TNT("tnt-jackpot", "tnt.gif"),
	CHERRY("cherry-jackpot", "cherry.gif"),
	TROPHY("trophy-jackpot", "trophy.gif"),
	DIAMOND("diamond-jackpot", "diamond.gif");
	
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
	
	/*public static SlotElement getJackpotTypeByTypeId(String typeId)
	{
		for(SlotElement type : SlotElement.values())
		{
			if(type.typeId != null && type.typeId.equals(typeId))
			{
				return type;
			}
		}
		
		return null;
	}*/
}