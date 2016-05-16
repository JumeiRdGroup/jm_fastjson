package com.alibaba.fastjson.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/*
 * Copyright (C) 2010-2015 Jumei Inc.All Rights Reserved.
 * FileName：JMUtil.java
 * Description：
 * History：
 * 1.0 denverhan 2016-4-23 Create
 */

public class JMUtil
{


	// private static final ThreadLocal<IdentityHashMap<String, String>> sData =
	// new ThreadLocal<IdentityHashMap<String, String>>()
	// {
	// @Override
	// protected IdentityHashMap<String, String> initialValue()
	// {
	// return new IdentityHashMap<>(4);
	// }
	// };



	// {
	// "640":"http://showlive-10012585.image.myqcloud.com/eccba269-8417-4fff-a1a3-3b4c2b10203a/640",
	// "720":"http://showlive-10012585.image.myqcloud.com/eccba269-8417-4fff-a1a3-3b4c2b10203a/640",
	// "1080":"http://showlive-10012585.image.myqcloud.com/eccba269-8417-4fff-a1a3-3b4c2b10203a/1280",
	// "1280":"http://showlive-10012585.image.myqcloud.com/eccba269-8417-4fff-a1a3-3b4c2b10203a/1280"
	// }

	public static String parJmImg(String img)
	{
		if (isEmpty(img))
			return "";

		JSONObject json = JSON.parseObject(img);

		if (json == null)
			return img;

		if (json.keySet() == null || json.size() == 0)
			return img;
		float matchRate = Float.MAX_VALUE;
		String result = img;
		for (String key : json.keySet())
		{
			float cur = Float.valueOf(key) / JSON.SCREEN_WIDTH;
			if (Math.abs(cur - 1.0F) < Math.abs(matchRate - 1.0F))// much
																	// better.
			{
				matchRate = cur;
				img = json.getString(key);
			}
		}
		return result;
	}

	public static String parseImageJson(String img)
	{
		if (isEmpty(img))
			return "";

		JSONObject json = JSON.parseObject(img);

		if (json == null)
			return img;

		if (json.keySet() == null || json.size() == 0)
			return img;
		float matchRate = Float.MAX_VALUE;
		String result = img;
		for (String key : json.keySet())
		{
			if(isEmpty(key))
				continue;
			
			float cur = Float.valueOf(key) / JSON.SCREEN_WIDTH;
			if (Math.abs(cur - 1.0F) < Math.abs(matchRate - 1.0F))// much
																	// better.
			{
				matchRate = cur;
				result = json.getString(key);
			}
		}
		return result;
	}

	public static boolean isEmpty(CharSequence str)
	{
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}
}
