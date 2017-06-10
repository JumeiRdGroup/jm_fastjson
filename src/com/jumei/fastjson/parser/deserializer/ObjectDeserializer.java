package com.jumei.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.jumei.fastjson.parser.DefaultJSONParser;

public interface ObjectDeserializer
{
	<T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName);
}
