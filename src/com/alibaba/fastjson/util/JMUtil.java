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
			if (isEmpty(key))
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
