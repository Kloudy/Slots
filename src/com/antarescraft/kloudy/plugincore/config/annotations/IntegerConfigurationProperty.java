package com.antarescraft.kloudy.plugincore.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegerConfigurationProperty
{
	public String key();
	public int defaultValue() default 0;
	public int minValue() default Integer.MIN_VALUE;
	public int maxValue() default Integer.MAX_VALUE;
	public boolean isRequired() default true;
}