package com.antarescraft.kloudy.plugincore.config;

import java.lang.reflect.Field;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.antarescraft.kloudy.plugincore.config.annotations.ConfigurationElement;
import com.antarescraft.kloudy.plugincore.config.annotations.ConfigurationElementCollection;
import com.antarescraft.kloudy.plugincore.config.annotations.ConfigurationProperty;

public class ConfigParser
{
	
	
	public <T> T parse(ConfigurationSection section, Class<T> classType) throws InstantiationException, IllegalAccessException
	{
		Set<String> keySet = section.getKeys(false);
		String[] keys = new String[keySet.size()];
		
		for(String key : keySet.toArray(keys))
		{
			for(Field field : classType.getFields())
			{
				//Configuration property field
				if(field.isAnnotationPresent(ConfigurationProperty.class))
				{
					
				}
				
				//Configuration element
				else if(field.isAnnotationPresent(ConfigurationElement.class))
				{
					
				}
				
				//Configuration element collection
				else if(field.isAnnotationPresent(ConfigurationElementCollection.class))
				{
					
				}
			}
		}

		return (T)classType.newInstance();
	}
}
