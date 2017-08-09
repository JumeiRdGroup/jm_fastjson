package com.jumei.fastjson.util;

import java.util.regex.Pattern;

import com.jumei.fastjson.JMJSON;
import com.jumei.fastjson.JSONObject;
import com.jumei.fastjson.parser.JSONToken;


/*
 * Copyright (C) 2010-2015 Jumei Inc.All Rights Reserved.
 * FileName：JMUtil.java
 * Description：
 * History：
 * 1.0 denverhan 2016-4-23 Create
 */

public class JMUtil
{
	public static String parseImageJson(String img, boolean unitPx)
	{
		if (isEmpty(img))
			return "";

		int start = img.indexOf(JSONToken.name(JSONToken.LBRACE));
		int end = img.lastIndexOf(JSONToken.name(JSONToken.RBRACE));

		if (start == -1 || end == -1)
			return img;

		JSONObject json = JMJSON.parseObject(img);

		if (json == null)
			return img;

		if (json.keySet() == null || json.size() == 0)
			return "";
		float matchRate = Float.MAX_VALUE;
		String result = img;
		for (String key : json.keySet())
		{

			Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
			if (isEmpty(key) || !pattern.matcher(key).matches())
				continue;

			int width = unitPx ? JMJSON.SCREEN_WIDTH_IN_PX : JMJSON.SCREEN_WIDTH_IN_DP;
			if (width == 0)
			{
				return img;
			}
			
			float cur = Float.valueOf(key) / width;
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
