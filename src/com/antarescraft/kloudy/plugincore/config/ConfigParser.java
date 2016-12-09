package com.antarescraft.kloudy.plugincore.config;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.antarescraft.kloudy.plugincore.config.annotations.BooleanConfigurationProperty;
import com.antarescraft.kloudy.plugincore.config.annotations.ConfigurationElement;
import com.antarescraft.kloudy.plugincore.config.annotations.ConfigurationElementCollection;
import com.antarescraft.kloudy.plugincore.config.annotations.ConfigurationElementKey;
import com.antarescraft.kloudy.plugincore.config.annotations.ConfigurationElementMap;
import com.antarescraft.kloudy.plugincore.config.annotations.DoubleConfigurationProperty;
import com.antarescraft.kloudy.plugincore.config.annotations.IntegerConfigurationProperty;
import com.antarescraft.kloudy.plugincore.config.annotations.ListConfigurationProperty;
import com.antarescraft.kloudy.plugincore.config.annotations.LongConfigurationProperty;
import com.antarescraft.kloudy.plugincore.config.annotations.StringConfigurationProperty;

public class ConfigParser
{
	public static <T> T parse(ConfigurationSection section, Class<T> classType) throws InstantiationException, IllegalAccessException, IllegalArgumentException, ClassNotFoundException, ConfigurationParseException
	{
		Set<String> keySet = section.getKeys(false);
				
		T obj = classType.newInstance();
		
		for(String key : keySet.toArray(new String[keySet.size()]))
		{
			for(Field field : classType.getDeclaredFields())
			{
				field.setAccessible(true);
				
				if(field.isAnnotationPresent(ConfigurationElementKey.class))
				{
					field.set(obj, section.getName());
				}
				else if(field.isAnnotationPresent(StringConfigurationProperty.class))
				{
					StringConfigurationProperty annotation = field.getAnnotation(StringConfigurationProperty.class);
					
					if(annotation.key().equals(key))
					{
						field.set(obj, section.getString(key));
					}
				}
				else if(field.isAnnotationPresent(BooleanConfigurationProperty.class))
				{
					BooleanConfigurationProperty annotation = field.getAnnotation(BooleanConfigurationProperty.class);
					if(annotation.key().equals(key))
					{
						field.set(obj, section.getBoolean(key));
					}
				}
				else if(field.isAnnotationPresent(IntegerConfigurationProperty.class))
				{
					IntegerConfigurationProperty annotation = field.getAnnotation(IntegerConfigurationProperty.class);
					
					if(annotation.key().equals(key))
					{
						field.set(obj, section.getInt(key));
					}
				}
				else if(field.isAnnotationPresent(DoubleConfigurationProperty.class))
				{
					DoubleConfigurationProperty annotation = field.getAnnotation(DoubleConfigurationProperty.class);
					
					if(annotation.key().equals(key))
					{
						field.set(obj, section.getDouble(key));
					}
				}
				else if(field.isAnnotationPresent(LongConfigurationProperty.class))
				{
					LongConfigurationProperty annotation = field.getAnnotation(LongConfigurationProperty.class);
					
					if(annotation.key().equals(key))
					{
						field.set(obj, section.getLong(key));
					}
				}
				else if(field.isAnnotationPresent(ListConfigurationProperty.class))
				{
					ListConfigurationProperty annotation = field.getAnnotation(ListConfigurationProperty.class);
					
					if(annotation.key().equals(key))
					{
						field.set(obj, section.getList(key));
					}
				}
				
				//Configuration element
				else if(field.isAnnotationPresent(ConfigurationElement.class))
				{
					if(!field.getType().isAssignableFrom(ConfigurationElement.class))
					{
						throw new ConfigurationParseException();
					}
					
					ConfigurationElement annotation = field.getAnnotation(ConfigurationElement.class);

					field.set(obj, parse(section.getConfigurationSection(key), Class.forName(annotation.elementClasspath())));
				}
				
				//Configuration element collection
				else if(field.isAnnotationPresent(ConfigurationElementCollection.class))
				{
					if(!field.getType().isAssignableFrom(ConfigurationElementCollection.class))
					{
						throw new ConfigurationParseException();
					}
					
					ConfigurationElementCollection annotation = field.getAnnotation(ConfigurationElementCollection.class);
					if(annotation.key().equals(key))
					{
						ParameterizedType genericListType = (ParameterizedType) field.getGenericType();
						Class<?> listType = (Class<?>) genericListType.getActualTypeArguments()[0];
						
						ConfigurationSection elementListSection = section.getConfigurationSection(key);
						
						ArrayList<Object> elements = new ArrayList<Object>();
						for(int i = 0 ; i < elementListSection.getKeys(false).size(); i++)
						{
							 elements.add(parse(elementListSection, listType));
						}
						
						field.set(obj, elements);
					}
				}
				
				//Configuration Map
				else if(field.isAnnotationPresent(ConfigurationElementMap.class))
				{
					ConfigurationElementMap annotation = field.getAnnotation(ConfigurationElementMap.class);
					if(annotation.key().equals(key))
					{
						ParameterizedType genericMapType = (ParameterizedType) field.getGenericType();
						Class<?> mapType = (Class<?>) genericMapType.getActualTypeArguments()[1];//HashMap<String, ?>
						
						ConfigurationSection elementMapSection = section.getConfigurationSection(key);
						
						HashMap<String, Object> elements = new HashMap<String, Object>();
						for(String elementKey : elementMapSection.getKeys(false))
						{
							ConfigurationSection elementSection = elementMapSection.getConfigurationSection(elementKey);
							
							elements.put(elementKey, parse(elementSection, mapType));
						}
												
						field.set(obj, elements);
					}
				}
			}
		}

		return obj;
	}
}
