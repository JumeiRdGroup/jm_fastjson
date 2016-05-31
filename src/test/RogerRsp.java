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

public class RogerRsp
{

	public int  			mInt ;

	@JMIMG
	public String				imgUrl;
	
	@JSONField( alias= {"data", "kabc"})
	public Data		mData;

	public static class Data
	{
		private  String mValue;
		
		@JMIMG
		@JSONField(name="good_value", alias= {"goodvalue", "kabc"})
		public void setValue(String v)
		{
			mValue = v;
		}
		
		public String getValue()
		{
			return mValue;
		}
	}
}
