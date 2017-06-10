/*
 * Copyright 1999-2101 Alibaba Group.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jumei.fastjson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jumei.fastjson.parser.Feature;
import com.jumei.fastjson.serializer.SerializerFeature;

/**
 * @author wenshao[szujobs@hotmail.com]
 * @author han_lian@foxmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
public @interface JSONField
{
	/**
	 * config encode/decode ordinal
	 * 
	 * @since 1.1.42
	 * @return
	 */
	int ordinal() default 0;

	String name() default "";

	String format() default "";

	/**
	 * 该字段用来在解析json的时候指定多个候选别名,因为坑爹的JM后台存在这样的API返回:
	 * 在两个功能类似的API返回一样的json结构,但是其中某些字段名不一样,仅仅是字段名的差别,其余类型和结构都是一样的,
	 * 这种可以使用AS插件工具重新生成Rsp,但是仅仅为了一个字段名重新生成,浪费安装包大小,浪费终端开发时间,所以还是定制fastjson一步到位.
	 * 注意,该alias仅仅针对解析有效,序列化时还是原来fastjon的逻辑,序列化时会忽略alias字段.
	 * 
	 * 用法如JSONField(name="alias0", alias={"alias1", "alias2"})
	 * 
	 * @author denverhan han_lian@foxmail.com
	 * @return
	 */
	String[] alias() default {};

	boolean serialize() default true;

	boolean deserialize() default true;

	SerializerFeature[] serialzeFeatures() default {};

	Feature[] parseFeatures() default {};
}
