package com.antarescraft.kloudy.plugincore.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringConfigurationProperty 
{
	public String key();
	public String defaultValue() default "";
	public boolean isRequired() default true;
}