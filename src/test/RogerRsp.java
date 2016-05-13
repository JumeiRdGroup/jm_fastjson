package test;

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


	//"{\"int_alias_test\":22,\"denver\":21 }";
	
	@JSONField(name="message", alias= {"denver", "abc"})
	public int				mMessage;
	@JSONField(name="Xessage_int",  alias= {"int_alias_test", "a"})
	public int  			mInt ;
	
}
