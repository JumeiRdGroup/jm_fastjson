package com.alibaba.fastjson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Copyright (C) 2010-2015 Jumei Inc.All Rights Reserved.
 * FileName：JMIMG.java
 * Description：用来标示当前字段是一个JM图片字典,需要按照手机分辨率自动解析为对应的图片url.
 * History：
 * @author han_lian@foxmail.com
 * 1.0 denverhan 2016-4-23 Create
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface JMIMG
{
	public enum Unit
	{
		PX,//按按照像素解析
		DP//按照分辨率解析
	};

	//默认按照分辨率解析
	Unit value() default Unit.PX;
}
