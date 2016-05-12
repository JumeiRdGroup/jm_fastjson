package test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.util.JMUtil;


/*
 * Copyright (C) 2010-2015 Jumei Inc.All Rights Reserved.
 * FileName：TestDriver.java
 * Description：
 * History：
 * 1.0 cdyf 2016-2-25 Create
 */

public class TestDriver
{

	@JSONField(name = "notify-channel")
	int		a;

	String	b;

	public static enum Tab
	{
		A("a"),
		B("b");

		String	value;

		Tab(String s)
		{
			value = s;
		}
	}

	public static void main(String[] args)
	{

		// int a = Tab.valueOf("A").ordinal();
		// int b = Tab.valueOf("B").ordinal();
		// System.out.println(a);
		//
		// Color color = Color.RED ;
		// color.ordinal();
		// System.out.println(color==Color.RED);
		// System.out.println(color.ordinal());
		// System.out.println(Arrays.toString(Color.values()));


		RogerRsp rsp = JSON.parseObject(RogerRsp.testCase, RogerRsp.class);
	System.out.print(JSON.toJSONString(rsp));
		
		
		String s= JMUtil.parseImageJson(Jmimg.a);
		System.out.println("s:" + s);
		
	}
}
