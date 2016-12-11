package com.antarescraft.kloudy.plugincore.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@RangedConfigProperty
public @interface DoubleConfigProperty
{
	public double defaultValue();
	public double minValue();
	public double maxValue();
}