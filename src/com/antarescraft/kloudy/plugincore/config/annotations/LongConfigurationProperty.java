package com.antarescraft.kloudy.plugincore.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LongConfigurationProperty 
{
	public String key();
	public long defaultValue() default 0;
	public long minValue() default Long.MIN_VALUE;
	public long maxValue() default Long.MAX_VALUE;
	public boolean isRequired() default false;
}
