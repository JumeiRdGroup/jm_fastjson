package com.alibaba.fastjson.parser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.alibaba.fastjson.annotation.JMIMG;
import com.alibaba.fastjson.annotation.JMIMG.Unit;
import com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask;
import com.alibaba.fastjson.parser.deserializer.FieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.JMUtil;

public class DefaultFieldDeserializer extends FieldDeserializer
{

	private ObjectDeserializer	fieldValueDeserilizer;

	public DefaultFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo)
	{
		super(clazz, fieldInfo, JSONToken.LITERAL_INT);
	}

	@Override
	public void parseField(DefaultJSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues)
	{
		if (fieldValueDeserilizer == null)
		{
			fieldValueDeserilizer = parser.config.getDeserializer(fieldInfo.fieldClass, fieldInfo.fieldType);
		}

		if (objectType instanceof ParameterizedType)
		{
			ParseContext objContext = parser.contex;
			objContext.type = objectType;
		}

		Object value = fieldValueDeserilizer.deserialze(parser, fieldInfo.fieldType, fieldInfo.name);
		if (parser.resolveStatus == DefaultJSONParser.NeedToResolve)
		{
			ResolveTask task = parser.getLastResolveTask();
			task.fieldDeserializer = this;
			task.ownerContext = parser.contex;
			parser.resolveStatus = DefaultJSONParser.NONE;
		}
		else
		{

			if (value != null
					&& (fieldInfo.field != null && fieldInfo.field.isAnnotationPresent(JMIMG.class) || fieldInfo.method != null
							&& fieldInfo.method.isAnnotationPresent(JMIMG.class)))
			{
				Unit unit = Unit.PX;
				JMIMG fieldAnotation = fieldInfo.field != null ? fieldInfo.field.getAnnotation(JMIMG.class) : null;
				JMIMG methodAnotation = fieldInfo.method != null ? fieldInfo.method.getAnnotation(JMIMG.class) : null;
				unit = fieldAnotation != null ? fieldAnotation.value() : (methodAnotation != null ? methodAnotation.value() : Unit.PX);

				value = JMUtil.parseImageJson(value.toString(), unit);
			}

			if (object == null)
			{
				fieldValues.put(fieldInfo.name, value);
			}
			else
			{
				if (value == null)
				{
					Class<?> fieldClass = fieldInfo.fieldClass;
					if (fieldClass == byte.class //
							|| fieldClass == short.class //
							|| fieldClass == float.class //
							|| fieldClass == double.class)
					{
						return;
					}
				}

				setValue(object, value);
			}
		}
	}
}
