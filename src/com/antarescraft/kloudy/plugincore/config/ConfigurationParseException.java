package com.antarescraft.kloudy.plugincore.config;

public class ConfigurationParseException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ConfigurationParseException(){}
	
	public ConfigurationParseException(String message)
	{
		super(message);
	}
}