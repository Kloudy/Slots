package com.antarescraft.kloudy.plugincore.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DoubleConfigurationProperty
{
	public String key();
	public double defaultValue() default 0;
	public double minValue() default Double.MIN_VALUE;
	public double maxValue() default Double.MAX_VALUE;
	public boolean isRequired() default true;
}