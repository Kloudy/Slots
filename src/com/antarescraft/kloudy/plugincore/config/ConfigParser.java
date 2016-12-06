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
					ConfigurationProperty annotation = field.getAnnotation(ConfigurationProperty.class);
					if(annotation.key().equals(key))
					{
						if(field.getType().equals(String.class))
						{
							
						}
					}
				}
				
				//Configuration element
				else if(field.isAnnotationPresent(ConfigurationElement.class))
				{
					ConfigurationElement annotation = field.getAnnotation(ConfigurationElement.class);
					if(annotation.key().equals(key))
					{
						
					}
				}
				
				//Configuration element collection
				else if(field.isAnnotationPresent(ConfigurationElementCollection.class))
				{
					ConfigurationElementCollection annotation = field.getAnnotation(ConfigurationElementCollection.class);
					if(annotation.key().equals(key))
					{
						
					}
				}
			}
		}

		return (T)classType.newInstance();
	}
}
