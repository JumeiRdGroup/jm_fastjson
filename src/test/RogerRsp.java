package test;

import com.alibaba.fastjson.annotation.JMIMG;



/*
 * Copyright (C) 2010-2015 Jumei Inc.All Rights Reserved.
 * FileName：RogerRsp.java
 * Description：
 * History：
 * 1.0 cdyf 2016-2-25 Create
 */

public class RogerRsp<T extends RogerRsp<T>>
{

	public static final String	testCase	= "{\n" + "    \"code\":0,\n" + "    \"action\":\"denver\",\n" + "    \"message\":\"\",\n" + "    \"data\":{\n"
													+ "        \"gateway\":\"TenpayWeixinMobile\",\n" + "        \"partnerSign\":{\n"
													+ "            \"sign\":\"4651e9278683fe5d6c9bd084611a0a32a920646b\",\n"
													+ "            \"timestamp\":1456034466,\n" + "            \"partnerid\":\"1216764201\",\n"
													+ "            \"noncestr\":\"lqaizn8b4niaasprahv357exmcvqwbzr\",\n"
													+ "            \"prepayid\":\"82010380001602216599176276e8fc89\",\n"
													+ "            \"package\":\"Sign=cft\",\n" + "            \"appid\":\"wx052b13e7ea19fb29\"\n"
													+ "        },\n" + "        \"order_code\":\"0000000000000393\"\n" + "    }\n" + "}\n";

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

	public int					code;
	@JMIMG
	public String				action;
	public String				message;
	/**
	 * gateway : TenpayWeixinMobile
	 * partnerSign :
	 * {"sign":"4651e9278683fe5d6c9bd084611a0a32a920646b","timestamp"
	 * :1456034466,
	 * "partnerid":"1216764201","noncestr":"lqaizn8b4niaasprahv357exmcvqwbzr"
	 * ,"prepayid"
	 * :"82010380001602216599176276e8fc89","package":"Sign=cft","appid"
	 * :"wx052b13e7ea19fb29"}
	 * order_code : 0000000000000393
	 */

	public DataEntity			data;

	/**
	 * 解析结束后的回调,用于处理某些包含图片适配的字段.
	 */
	public void onParsed()
	{

	}

	public static class DataEntity
	{
		public String				gateway;
		/**
		 * sign : 4651e9278683fe5d6c9bd084611a0a32a920646b
		 * timestamp : 1456034466
		 * partnerid : 1216764201
		 * noncestr : lqaizn8b4niaasprahv357exmcvqwbzr
		 * prepayid : 82010380001602216599176276e8fc89
		 * package : Sign=cft
		 * appid : wx052b13e7ea19fb29
		 */

		public PartnerSignEntity	partnerSign;
		public String				order_code;

		public static class PartnerSignEntity
		{
			public String	sign;
			public int		timestamp;
			public String	partnerid;
			public String	noncestr;
			public String	prepayid;

			public String	packageX;
			public String	appid;
		}
	}
}
