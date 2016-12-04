package com.antarescraft.kloudy.slots.util;

import java.util.HashMap;

public class BukkitIntervalRunnableContext 
{
	private HashMap<String, Object> contextDictionary;
	
	public BukkitIntervalRunnableContext()
	{
		contextDictionary = new HashMap<String, Object>();
	}
	
	public void setContextVariable(String key, Object value)
	{
		contextDictionary.put(key, value);
	}
	
	public Object getContextVariable(String key)
	{
		return contextDictionary.get(key);
	}
	
	public void removeContextVariable(String key)
	{
		contextDictionary.remove(key);
	}
	
	public boolean containsKey(String key)
	{
		return contextDictionary.containsKey(key);
	}
}