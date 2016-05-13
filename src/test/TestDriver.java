package test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.JMUtil;


/*
 * Copyright (C) 2010-2015 Jumei Inc.All Rights Reserved.
 * FileName：TestDriver.java
 * Description：
 * History：
 * 1.0 denverhan 2016-2-25 Create
 */

public class TestDriver
{

	public static String		jmimg		= "{\"1080\":\"http://images2.jumei.com/user_avatar/092/909/92909501-200.jpg?1458828594\",\"1280\":\"http://images2.jumei.com/user_avatar/092/909/92909501-200.jpg?1458828594\",\"640\":\"http://images2.jumei.com/user_avatar/092/909/92909501-100.jpg?1458828647\",\"720\":\"http://images2.jumei.com/user_avatar/092/909/92909501-100.jpg?1458828647\"}";
	public static final String	testCase	= "{\"int_alias_test\":22,\"denver\":21, \"data\":{\"value\":\"avlua\"} }";

	public static void main(String[] args)
	{
		RogerRsp rsp = JSON.parseObject(testCase, RogerRsp.class);
		System.out.println(JSON.toJSONString(rsp));


		String s = JMUtil.parseImageJson(jmimg);
		System.out.println("s:" + s);
	}
}
