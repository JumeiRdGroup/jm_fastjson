package test;

import com.alibaba.fastjson.annotation.JMIMG;
import com.alibaba.fastjson.annotation.JSONField;



/*
 * Copyright (C) 2010-2015 Jumei Inc.All Rights Reserved.
 * FileName：RogerRsp.java
 * Description：
 * History：
 * 1.0 denverhan 2016-2-25 Create
 */

public class RogerRsp<T extends RogerRsp<T>>
{

	public static final String	testCase	= "{\"abc\":\"denver_alias\"}";

	/**
	 * code : 0
	 * action :
	 * message :
	 * data : {"gateway":"TenpayWeixinMobile","partnerSign":{"sign":
	 * "4651e9278683fe5d6c9bd084611a0a32a920646b"
	 * ,"timestamp":1456034466,"partnerid"
	 * :"1216764201","noncestr":"lqaizn8b4niaasprahv357exmcvqwbzr"
	 * ,"prepayid":"82010380001602216599176276e8fc89"
	 * ,"package":"Sign=cft","appid"
	 * :"wx052b13e7ea19fb29"},"order_code":"0000000000000393"}
	 */


	
	@JSONField(name="message", alias= {"denver", "abc"})
	public String				mMessage;
}
