package com.antarescraft.kloudy.plugincore.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BooleanConfigurationProperty
{
	public String key();
	public boolean defaultValue() default false;
	public boolean isRequired() default true;
}