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


	public static String url="{\"1080\":\"http://showlive-10012585.image.myqcloud.com/92dd7b2f-7e8f-4bcb-9b29-202789ec4e9a/1280x640\",\"1280\":\"http://showlive-10012585.image.myqcloud.com/92dd7b2f-7e8f-4bcb-9b29-202789ec4e9a/1280x640\",\"640\":\"http://showlive-10012585.image.myqcloud.com/92dd7b2f-7e8f-4bcb-9b29-202789ec4e9a/750x375\",\"720\":\"http://showlive-10012585.image.myqcloud.com/92dd7b2f-7e8f-4bcb-9b29-202789ec4e9a/750x375\"}";

	public static String		jmimg		= "{\"1080\":\"http://images2.jumei.com/user_avatar/092/909/92909501-200.jpg?1458828594\",\"1280\":\"http://images2.jumei.com/user_avatar/092/909/92909501-200.jpg?1458828594\",\"640\":\"http://images2.jumei.com/user_avatar/092/909/92909501-100.jpg?1458828647\",\"720\":\"http://images2.jumei.com/user_avatar/092/909/92909501-100.jpg?1458828647\"}";
	public static final String	testCase	= "{\"int_alias_test\":22,\"denver\":21, \"data\":{\"value\":"+  url +  "}   			 }";

	static String				imgTest		= "{\"1080\":\"http://mp2.jmstatic.com/product/001/564/1564334_std/1564334_350_350.jpg?imageView2/2/w/540&v=1462858128\",\"480\":\"http://mp1.jmstatic.com/c_zoom,w_240,q_70/product/001/564/1564334_std/1564334_350_350.jpg?v=1462858128\",\"320\":\"http://mp1.jmstatic.com/c_zoom,w_160,q_70/product/001/564/1564334_std/1564334_350_350.jpg?v=1462858128\",\"640\":\"http://mp1.jmstatic.com/c_zoom,w_320,q_70/product/001/564/1564334_std/1564334_350_350.jpg?v=1462858128\"}";

	public static void main(String[] args)
	{
		RogerRsp p = JSON.parseObject(testCase,  RogerRsp.class);
		System.out.println("value:" + p.mData.getValue());
		String s = JMUtil.parseImageJson(url);
		System.out.println("s:" + s);
	}
}
