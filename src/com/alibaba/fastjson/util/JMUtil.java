package com.alibaba.fastjson.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.JSONToken;


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
	public static String parseImageJson(String img)
	{
		if (isEmpty(img))
			return "";
		img = img.trim();
		int start = img.indexOf(JSONToken.name(JSONToken.LBRACE));
		int end = img.lastIndexOf(JSONToken.name(JSONToken.RBRACE));

		if (start == -1 || end == -1)
			return img;

		img = img.substring(start + 1, end);

		String[] unit = img.split(",\"");

		float matchRate = Float.MAX_VALUE;

		for (String item : unit)
		{
			if (item == null || item.length() == 0)
				continue;


			int spliter = item.indexOf("\":");
			if (spliter == -1)
			{
				continue;
			}

			String key = item.substring(0, spliter);
			String value = (spliter + 1 >= item.length()) ? null : item.substring(spliter + 1, item.length());
			if (value == null)
			{
				continue;
			}

			try
			{
				key = delQuto(key);
				value = delQuto(value);
				img = value;

				float cur = Float.valueOf(key) / JSON.SCREEN_WIDTH;
				if (Math.abs(cur - 1.0F) < Math.abs(matchRate - 1.0F))// much
																		// better.
				{
					matchRate = cur;
					img = value;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			// sData.get().put(v[0], v[1]);
		}
		return img;
	}

	private static String delQuto(String key)
	{
		if (isEmpty(key))
			return "";

		int pos = key.indexOf("\"");
		if (pos != -1 && key.length() > 1)
		{
			key = key.substring(pos + 1);
		}

		pos = key.lastIndexOf("\"");
		if (pos != -1 && key.length() > 1)
		{
			key = key.substring(0, pos);
		}

		return key;
	}

	public static boolean isEmpty(CharSequence str)
	{
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}
}
