/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.fastjson.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessControlException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JavaBeanDeserializer;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author wenshao[szujobs@hotmail.com]
 */
public class TypeUtils {
	
    public static boolean compatibleWithJavaBean = false;
    private static boolean setAccessibleEnable    = true;
    
    public static final String castToString(Object value) {
        if (value == null) {
            return null;
        }

        return value.toString();
    }

    public static final Byte castToByte(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 // 
                    || "null".equals(strVal)) {
                return null;
            }
            
            return Byte.parseByte(strVal);
        }

        throw new JSONException("can not cast to byte, value : " + value);
    }

    public static final Character castToChar(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Character) {
            return (Character) value;
        }

        if (value instanceof String) {
            String strVal = (String) value;

            if (strVal.length() == 0) {
                return null;
            }

            if (strVal.length() != 1) {
                throw new JSONException("can not cast to byte, value : " + value);
            }

            return strVal.charAt(0);
        }

        throw new JSONException("can not cast to byte, value : " + value);
    }

    public static final Short castToShort(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                || "null".equals(strVal)) {
                return null;
            }
            
            return Short.parseShort(strVal);
        }

        throw new JSONException("can not cast to short, value : " + value);
    }

    public static final BigDecimal castToBigDecimal(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }

        String strVal = value.toString();
        if (strVal.length() == 0 //
            || "null".equals(strVal)) {
            return null;
        }

        return new BigDecimal(strVal);
    }

    public static final BigInteger castToBigInteger(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof BigInteger) {
            return (BigInteger) value;
        }

        if (value instanceof Float || value instanceof Double) {
            return BigInteger.valueOf(((Number) value).longValue());
        }

        String strVal = value.toString();
        if (strVal.length() == 0 //
            || "null".equals(strVal)) {
            return null;
        }

        return new BigInteger(strVal);
    }

    public static final Float castToFloat(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }

        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() == 0 //
                || "null".equals(strVal)) {
                return null;
            }

            return Float.parseFloat(strVal);
        }

        throw new JSONException("can not cast to float, value : " + value);
    }

    public static final Double castToDouble(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() == 0 //
                || "null".equals(strVal)) {
                return null;
            }
            
            return Double.parseDouble(strVal);
        }

        throw new JSONException("can not cast to double, value : " + value);
    }

    public static final Date castToDate(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Calendar) {
            return ((Calendar) value).getTime();
        }

        if (value instanceof Date) {
            return (Date) value;
        }

        long longValue = -1;

        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        }

        if (value instanceof String) {
            String strVal = (String) value;

            if (strVal.indexOf('-') != -1) {
                String format;
                if (strVal.length() == JSON.DEFFAULT_DATE_FORMAT.length()) {
                    format = JSON.DEFFAULT_DATE_FORMAT;
                } else if (strVal.length() == 10) {
                    format = "yyyy-MM-dd";
                } else if (strVal.length() == "yyyy-MM-dd HH:mm:ss".length()) {
                    format = "yyyy-MM-dd HH:mm:ss";
                } else {
                    format = "yyyy-MM-dd HH:mm:ss.SSS";
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat(format, JSON.defaultLocale);
                dateFormat.setTimeZone(JSON.defaultTimeZone);
                try {
                    return (Date) dateFormat.parse(strVal);
                } catch (ParseException e) {
                    throw new JSONException("can not cast to Date, value : " + strVal);
                }
            }

            if (strVal.length() == 0 //
                || "null".equals(strVal)) {
                return null;
            }

            longValue = Long.parseLong(strVal);
        }

        if (longValue < 0) {
            throw new JSONException("can not cast to Date, value : " + value);
        }

        return new Date(longValue);
    }

    public static final Long castToLong(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                || "null".equals(strVal)) {
                return null;
            }

            try {
                return Long.parseLong(strVal);
            } catch (NumberFormatException ex) {
                //
            }

            JSONLexer dateParser = new JSONLexer(strVal);
            Calendar calendar = null;
            if (dateParser.scanISO8601DateIfMatch(false)) {
                calendar = dateParser.getCalendar();
            }
            dateParser.close();

            if (calendar != null) {
                return calendar.getTimeInMillis();
            }
        }

        throw new JSONException("can not cast to long, value : " + value);
    }

    public static final Integer castToInt(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                || "null".equals(strVal)) {
                return null;
            }

            return Integer.parseInt(strVal);
        }

        throw new JSONException("can not cast to int, value : " + value);
    }

    public static final byte[] castToBytes(Object value) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        }

        if (value instanceof String) {
            return JSONLexer.decodeFast((String) value);
        }
        throw new JSONException("can not cast to int, value : " + value);
    }

    public static final Boolean castToBoolean(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 //
                || "null".equals(strVal)) {
                return null;
            }

            if ("true".equalsIgnoreCase(strVal) //
                    || "1".equals(strVal)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(strVal) //
                || "0".equals(strVal)) {
                return Boolean.FALSE;
            }
        }

        throw new JSONException("can not cast to int, value : " + value);
    }

    public static final <T> T castToJavaBean(Object obj, Class<T> clazz) {
        return cast(obj, clazz, ParserConfig.global);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final <T> T cast(Object obj, Class<T> clazz, ParserConfig mapping) {
        if (obj == null) {
            return null;
        }

        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }

        if (clazz == obj.getClass()) {
            return (T) obj;
        }

        if (obj instanceof Map) {
            if (clazz == Map.class) {
                return (T) obj;
            }

            Map map = (Map) obj;
            if (clazz == Object.class && !map.containsKey(JSON.DEFAULT_TYPE_KEY)) {
                return (T) obj;
            }

            return castToJavaBean((Map<String, Object>) obj, clazz, mapping);
        }

        if (clazz.isArray()) {
            if (obj instanceof Collection) {

                Collection collection = (Collection) obj;
                int index = 0;
                Object array = Array.newInstance(clazz.getComponentType(), collection.size());
                for (Object item : collection) {
                    Object value = cast(item, clazz.getComponentType(), mapping);
                    Array.set(array, index, value);
                    index++;
                }

                return (T) array;
            }
            
            if (clazz == byte[].class) {
                return (T) castToBytes(obj);
            }
        }

        if (clazz.isAssignableFrom(obj.getClass())) {
            return (T) obj;
        }

        if (clazz == boolean.class || clazz == Boolean.class) {
            return (T) castToBoolean(obj);
        }

        if (clazz == byte.class || clazz == Byte.class) {
            return (T) castToByte(obj);
        }

        // if (clazz == char.class || clazz == Character.class) {
        // return (T) castToCharacter(obj);
        // }

        if (clazz == short.class || clazz == Short.class) {
            return (T) castToShort(obj);
        }

        if (clazz == int.class || clazz == Integer.class) {
            return (T) castToInt(obj);
        }

        if (clazz == long.class || clazz == Long.class) {
            return (T) castToLong(obj);
        }

        if (clazz == float.class || clazz == Float.class) {
            return (T) castToFloat(obj);
        }

        if (clazz == double.class || clazz == Double.class) {
            return (T) castToDouble(obj);
        }

        if (clazz == String.class) {
            return (T) castToString(obj);
        }

        if (clazz == BigDecimal.class) {
            return (T) castToBigDecimal(obj);
        }

        if (clazz == BigInteger.class) {
            return (T) castToBigInteger(obj);
        }

        if (clazz == Date.class) {
            return (T) castToDate(obj);
        }

        if (clazz.isEnum()) {
            return (T) castToEnum(obj, clazz, mapping);
        }

        if (Calendar.class.isAssignableFrom(clazz)) {
            Date date = castToDate(obj);
            Calendar calendar;
            if (clazz == Calendar.class) {
                calendar = Calendar.getInstance(JSON.defaultTimeZone, JSON.defaultLocale);
            } else {
                try {
                    calendar = (Calendar) clazz.newInstance();
                } catch (Exception e) {
                    throw new JSONException("can not cast to : " + clazz.getName(), e);
                }
            }
            calendar.setTime(date);
            return (T) calendar;
        }

        if (obj instanceof String) {
            String strVal = (String) obj;
            if (strVal.length() == 0 //
                || "null".equals(strVal)) {
                return null;
            }
            
            if (clazz == java.util.Currency.class) {
                return (T) java.util.Currency.getInstance(strVal);
            }
        }

        throw new JSONException("can not cast to : " + clazz.getName());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final <T> T castToEnum(Object obj, Class<T> clazz, ParserConfig mapping) {
        try {
            if (obj instanceof String) {
                String name = (String) obj;
                if (name.length() == 0) {
                    return null;
                } else {
                    return (T) Enum.valueOf((Class<? extends Enum>) clazz, name);
                }
            } else if (obj instanceof Number) {
                int ordinal = ((Number) obj).intValue();
                Object[] values = clazz.getEnumConstants();
                if (ordinal < values.length) {
                    return (T) values[ordinal];
                }
            }
        } catch (Exception ex) {
            throw new JSONException("can not cast to : " + clazz.getName(), ex);
        }

        throw new JSONException("can not cast to : " + clazz.getName());
    }

    @SuppressWarnings("unchecked")
    public static final <T> T cast(Object obj, Type type, ParserConfig mapping) {
        if (obj == null) {
            return null;
        }

        if (type instanceof Class) {
            return (T) cast(obj, (Class<T>) type, mapping);
        }

        if (type instanceof ParameterizedType) {
            return (T) cast(obj, (ParameterizedType) type, mapping);
        }

        if (obj instanceof String) {
            String strVal = (String) obj;
            if (strVal.length() == 0 //
                || "null".equals(strVal)) {
                return null;
            }
        }

        if (type instanceof TypeVariable) {
            return (T) obj;
        }

        throw new JSONException("can not cast to : " + type);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final <T> T cast(Object obj, ParameterizedType type, ParserConfig mapping) {
        Type rawTye = type.getRawType();

        if (rawTye == Set.class 
                || rawTye == HashSet.class //
                ||  rawTye == TreeSet.class //
                || rawTye == List.class //
                || rawTye == ArrayList.class) {
            Type itemType = type.getActualTypeArguments()[0];

            if (obj instanceof Iterable) {
                Collection collection; 
                if (rawTye == Set.class || rawTye == HashSet.class) {
                    collection = new HashSet();
                } else if (rawTye == TreeSet.class) {
                    collection = new TreeSet();
                } else {
                    collection = new ArrayList();    
                }
                
                for (Iterator it = ((Iterable) obj).iterator(); it.hasNext();) {
                    Object item = it.next();
                    collection.add(cast(item, itemType, mapping));
                }

                return (T) collection;
            }
        }

        if (rawTye == Map.class || rawTye == HashMap.class) {
            Type keyType = type.getActualTypeArguments()[0];
            Type valueType = type.getActualTypeArguments()[1];

            if (obj instanceof Map) {
                Map map = new HashMap();

                for (Map.Entry entry : ((Map<?, ?>) obj).entrySet()) {
                    Object key = cast(entry.getKey(), keyType, mapping);
                    Object value = cast(entry.getValue(), valueType, mapping);

                    map.put(key, value);
                }

                return (T) map;
            }
        }

        if (obj instanceof String) {
            String strVal = (String) obj;
            if (strVal.length() == 0 //
                || "null".equals(strVal)) {
                return null;
            }
        }

        if (type.getActualTypeArguments().length == 1) {
            Type argType = type.getActualTypeArguments()[0];
            if (argType instanceof WildcardType) {
                return (T) cast(obj, rawTye, mapping);
            }
        }

        throw new JSONException("can not cast to : " + type);
    }

    @SuppressWarnings({ "unchecked" })
    public static final <T> T castToJavaBean(Map<String, Object> map, Class<T> clazz, ParserConfig config) {
        try {
            if (clazz == StackTraceElement.class) {
                String declaringClass = (String) map.get("className");
                String methodName = (String) map.get("methodName");
                String fileName = (String) map.get("fileName");
                int lineNumber;
                {
                    Number value = (Number) map.get("lineNumber");
                    if (value == null) {
                        lineNumber = 0;
                    } else {
                        lineNumber = value.intValue();
                    }
                }

                return (T) new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
            }

            {
                Object iClassObject = map.get(JSON.DEFAULT_TYPE_KEY);
                if (iClassObject instanceof String) {
                    String className = (String) iClassObject;

                    Class<?> loadClazz = (Class<T>) loadClass(className, null);

                    if (loadClazz == null) {
                        throw new ClassNotFoundException(className + " not found");
                    }

                    if (!loadClazz.equals(clazz)) {
                        return (T) castToJavaBean(map, loadClazz, config);
                    }
                }
            }

            if (clazz.isInterface()) {
                JSONObject object;

                if (map instanceof JSONObject) {
                    object = (JSONObject) map;
                } else {
                    object = new JSONObject(map);
                }

                return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                                                  new Class<?>[] { clazz }, object);
            }

            if (config == null) {
                config = ParserConfig.global;
            }

            JavaBeanDeserializer javaBeanDeser = null;
            ObjectDeserializer deserizer = config.getDeserializer(clazz);
            if (deserizer instanceof JavaBeanDeserializer) {
                javaBeanDeser = (JavaBeanDeserializer) deserizer;
            }
            
            if (javaBeanDeser == null) {
                throw new JSONException("can not get javaBeanDeserializer");
            }
            
            return (T) javaBeanDeser.createInstance(map, config);
        } catch (Exception e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    private static ConcurrentMap<String, Class<?>> mappings = new ConcurrentHashMap<String, Class<?>>();
    static {
        mappings.put("byte", byte.class);
        mappings.put("short", short.class);
        mappings.put("int", int.class);
        mappings.put("long", long.class);
        mappings.put("float", float.class);
        mappings.put("double", double.class);
        mappings.put("boolean", boolean.class);
        mappings.put("char", char.class);

        mappings.put("[byte", byte[].class);
        mappings.put("[short", short[].class);
        mappings.put("[int", int[].class);
        mappings.put("[long", long[].class);
        mappings.put("[float", float[].class);
        mappings.put("[double", double[].class);
        mappings.put("[boolean", boolean[].class);
        mappings.put("[char", char[].class);

        mappings.put(HashMap.class.getName(), HashMap.class);
    }

    public static Class<?> loadClass(String className, ClassLoader classLoader) {
        if (className == null || className.length() == 0) {
            return null;
        }
        
        Class<?> clazz = mappings.get(className);

        if (clazz != null) {
            return clazz;
        }

        if (className.charAt(0) == '[') {
            Class<?> componentType = loadClass(className.substring(1), classLoader);
            return Array.newInstance(componentType, 0).getClass();
        }

        if (className.startsWith("L") && className.endsWith(";")) {
            String newClassName = className.substring(1, className.length() - 1);
            return loadClass(newClassName, classLoader);
        }
        
        try {
            if (classLoader != null) {
                clazz = classLoader.loadClass(className);

                mappings.put(className, clazz);

                return clazz;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // skip
        }

        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

            if (contextClassLoader != null) {
                clazz = contextClassLoader.loadClass(className);

                mappings.put(className, clazz);

                return clazz;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // skip
        }

        try {
            clazz = Class.forName(className);

            mappings.put(className, clazz);

            return clazz;
        } catch (Exception e) {
            e.printStackTrace();
            // skip
        }

        return clazz;
    }

    public static List<FieldInfo> computeGetters(Class<?> clazz, //
                                                 int modifiers, // class modifier, Class.getModifiers is very slow
                                                 boolean fieldOnly, //
                                                 JSONType jsonType, //
                                                 Map<String, String> aliasMap, //
                                                 boolean sorted,  //
                                                 boolean jsonFieldSupport, //
                                                 boolean fieldGenericSupport) {
        Map<String, FieldInfo> fieldInfoMap = new LinkedHashMap<String, FieldInfo>();
        
        if (!fieldOnly) {
            Field[] declaredFields = clazz.getDeclaredFields();
            
            for (Method method : clazz.getMethods()) {
                String methodName = method.getName();
                int ordinal = 0, serialzeFeatures = 0;

                if ((method.getModifiers() & Modifier.STATIC) != 0 //
                    || method.getReturnType().equals(Void.TYPE) //
                    || method.getParameterTypes().length != 0 //
                    || method.getReturnType() == ClassLoader.class //
                    || method.getDeclaringClass() == Object.class //
                    || (methodName.equals("getMetaClass") //
                        && method.getReturnType().getName().equals("groovy.lang.MetaClass")) //
                        ) {
                    continue;
                }

                JSONField annotation = jsonFieldSupport ? method.getAnnotation(JSONField.class) : null;

                if (annotation == null && jsonFieldSupport) {
                    annotation = getSupperMethodAnnotation(clazz, method);
                }

                if (annotation != null) {
                    if (!annotation.serialize()) {
                        continue;
                    }

                    ordinal = annotation.ordinal();
                    serialzeFeatures = SerializerFeature.of(annotation.serialzeFeatures());

                    if (annotation.name().length() != 0) {
                        String propertyName = annotation.name();

                        if (aliasMap != null) {
                            propertyName = aliasMap.get(propertyName);
                            if (propertyName == null) {
                                continue;
                            }
                        }

                        TypeUtils.setAccessible(clazz, method, modifiers);
                        fieldInfoMap.put(propertyName, new FieldInfo(propertyName, method, null, clazz, null, ordinal,
                                                                     serialzeFeatures, annotation, null, true));
                        continue;
                    }
                }

                if (methodName.startsWith("get")) {
                    if (methodName.length() < 4 //
                        || methodName.equals("getClass") //
                            ) {
                        continue;
                    }

                    char c3 = methodName.charAt(3);

                    String propertyName;
                    if (Character.isUpperCase(c3)) {
                        if (compatibleWithJavaBean) {
                            propertyName = decapitalize(methodName.substring(3));
                        } else {
                            propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                        }
                    } else if (c3 == '_') {
                        propertyName = methodName.substring(4);
                    } else if (c3 == 'f') {
                        propertyName = methodName.substring(3);
                    } else if (methodName.length() >= 5 && Character.isUpperCase(methodName.charAt(4))) {
                        propertyName = decapitalize(methodName.substring(3));
                    } else {
                        continue;
                    }

                    if (isJSONTypeIgnore(clazz, jsonType, propertyName)) {
                        continue;
                    }

                    Field field = getField(clazz, propertyName, declaredFields);
                    JSONField fieldAnnotation = null;
                    if (field != null) {
                        fieldAnnotation = jsonFieldSupport ? field.getAnnotation(JSONField.class) : null;

                        if (fieldAnnotation != null) {
                            if (!fieldAnnotation.serialize()) {
                                continue;
                            }

                            ordinal = fieldAnnotation.ordinal();
                            serialzeFeatures = SerializerFeature.of(fieldAnnotation.serialzeFeatures());

                            if (fieldAnnotation.name().length() != 0) {
                                propertyName = fieldAnnotation.name();

                                if (aliasMap != null) {
                                    propertyName = aliasMap.get(propertyName);
                                    if (propertyName == null) {
                                        continue;
                                    }
                                }
                            }
                        }
                    }

                    if (aliasMap != null) {
                        propertyName = aliasMap.get(propertyName);
                        if (propertyName == null) {
                            continue;
                        }
                    }

                    TypeUtils.setAccessible(clazz, method, modifiers);
                    fieldInfoMap.put(propertyName,
                                     new FieldInfo(propertyName, method, field, clazz, null, ordinal, serialzeFeatures,
                                                   annotation, fieldAnnotation, fieldGenericSupport));
                }

                if (methodName.startsWith("is")) {
                    if (methodName.length() < 3) {
                        continue;
                    }

                    char c2 = methodName.charAt(2);

                    String propertyName;
                    if (Character.isUpperCase(c2)) {
                        if (compatibleWithJavaBean) {
                            propertyName = decapitalize(methodName.substring(2));
                        } else {
                            propertyName = Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
                        }
                    } else if (c2 == '_') {
                        propertyName = methodName.substring(3);
                    } else if (c2 == 'f') {
                        propertyName = methodName.substring(2);
                    } else {
                        continue;
                    }

                    Field field = getField(clazz, propertyName, declaredFields);

                    if (field == null) {
                        field = getField(clazz, methodName, declaredFields);
                    }

                    JSONField fieldAnnotation = null;
                    if (field != null) {
                        fieldAnnotation = jsonFieldSupport ? field.getAnnotation(JSONField.class) : null;

                        if (fieldAnnotation != null) {
                            if (!fieldAnnotation.serialize()) {
                                continue;
                            }

                            ordinal = fieldAnnotation.ordinal();
                            serialzeFeatures = SerializerFeature.of(fieldAnnotation.serialzeFeatures());

                            if (fieldAnnotation.name().length() != 0) {
                                propertyName = fieldAnnotation.name();

                                if (aliasMap != null) {
                                    propertyName = aliasMap.get(propertyName);
                                    if (propertyName == null) {
                                        continue;
                                    }
                                }
                            }
                        }
                    }

                    if (aliasMap != null) {
                        propertyName = aliasMap.get(propertyName);
                        if (propertyName == null) {
                            continue;
                        }
                    }

                    TypeUtils.setAccessible(clazz, field, modifiers);
                    TypeUtils.setAccessible(clazz, method, modifiers);
                    fieldInfoMap.put(propertyName,
                                     new FieldInfo(propertyName, method, field, clazz, null, ordinal, serialzeFeatures,
                                                   annotation, fieldAnnotation, fieldGenericSupport));
                }
            }
        }
        
        //
        for (Field field : clazz.getFields()) {
            if ((field.getModifiers() & Modifier.STATIC) != 0) {
                continue;
            }

            JSONField fieldAnnotation = jsonFieldSupport ? field.getAnnotation(JSONField.class) : null;

            int ordinal = 0, serialzeFeatures = 0;
            String propertyName = field.getName();
            if (fieldAnnotation != null) {
                if (!fieldAnnotation.serialize()) {
                    continue;
                }

                ordinal = fieldAnnotation.ordinal();
                serialzeFeatures = SerializerFeature.of(fieldAnnotation.serialzeFeatures());
                
                if (fieldAnnotation.name().length() != 0) {
                    propertyName = fieldAnnotation.name();
                }
            }

            if (aliasMap != null) {
                propertyName = aliasMap.get(propertyName);
                if (propertyName == null) {
                    continue;
                }
            }

            if (!fieldInfoMap.containsKey(propertyName)) {
                TypeUtils.setAccessible(clazz, field, modifiers);
                fieldInfoMap.put(propertyName, //
                                 new FieldInfo(propertyName, //
                                               null, //
                                               field, //
                                               clazz, //
                                               null, //
                                               ordinal, //
                                               serialzeFeatures, //
                                               null, //
                                               fieldAnnotation, //
                                               fieldGenericSupport));
            }
        }

        List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();

        boolean containsAll = false;
        String[] orders = null;

        if (jsonType != null) {
            orders = jsonType.orders();

            if (orders != null && orders.length == fieldInfoMap.size()) {
                containsAll = true;
                for (String item : orders) {
                    if (!fieldInfoMap.containsKey(item)) {
                        containsAll = false;
                        break;
                    }
                }
            } else {
                containsAll = false;
            }
        }

        if (containsAll) {
            for (String item : orders) {
                FieldInfo fieldInfo = fieldInfoMap.get(item);
                fieldInfoList.add(fieldInfo);
            }
        } else {
            for (FieldInfo fieldInfo : fieldInfoMap.values()) {
                fieldInfoList.add(fieldInfo);
            }

            if (sorted) {
                Collections.sort(fieldInfoList);
            }
        }

        return fieldInfoList;
    }

    public static JSONField getSupperMethodAnnotation(Class<?> clazz, Method method) {
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            for (Method interfaceMethod : interfaceClass.getMethods()) {
                if (!interfaceMethod.getName().equals(method.getName())) {
                    continue;
                }

                Class<?>[] interfaceParameterTypes = interfaceMethod.getParameterTypes();
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (interfaceParameterTypes.length != parameterTypes.length) {
                    continue;
                }

                boolean match = true;
                for (int i = 0; i < interfaceParameterTypes.length; ++i) {
                    if (!interfaceParameterTypes[i].equals(parameterTypes[i])) {
                        match = false;
                        break;
                    }
                }

                if (!match) {
                    continue;
                }

                JSONField annotation = interfaceMethod.getAnnotation(JSONField.class);
                if (annotation != null) {
                    return annotation;
                }
            }
        }

        return null;
    }

    private static boolean isJSONTypeIgnore(Class<?> clazz, JSONType jsonType, String propertyName) {
        if (jsonType != null && jsonType.ignores() != null) {
            for (String item : jsonType.ignores()) {
                if (propertyName.equalsIgnoreCase(item)) {
                    return true;
                }
            }
        }

        Class<?> superClass = clazz.getSuperclass();
        return superClass != Object.class //
               && superClass != null //
               && isJSONTypeIgnore(superClass, //
                                   superClass.getAnnotation(JSONType.class), //
                                   propertyName);
    }
    
    public static boolean isGenericParamType(Type type) {
        if (type instanceof ParameterizedType) {
            return true;
        }
        
        if (type instanceof Class) {
            Type superType = ((Class<?>) type).getGenericSuperclass();
            return superType != Object.class //
                    && isGenericParamType(superType);
        }
        
        return false;
    }
    
    public static Type getGenericParamType(Type type) {
        return type instanceof Class //
            ? getGenericParamType(((Class<?>) type).getGenericSuperclass()) //
            : type;
    }
    
    public static Class<?> getClass(Type type) {
        if (type.getClass() == Class.class) {
            return (Class<?>) type;
        }

        if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        }
        
        if (type instanceof TypeVariable) {
            return (Class<?>) ((TypeVariable<?>) type).getBounds()[0];
        }

        return Object.class;
    }
    
    public static String decapitalize(String name) {
        if (name == null //
            || name.length() == 0 //
            || (name.length() > 1 && Character.isUpperCase(name.charAt(1)) //
                && Character.isUpperCase(name.charAt(0))) //
        ) {
            return name;
        }
        
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
    
    public static boolean setAccessible(Class<?> clazz, Member member, int classMofifiers) {
        if (member == null //
            || !setAccessibleEnable //
        ) {
            return false;
        }
        
        Class<?> supperClass = clazz.getSuperclass();
        
        if ((supperClass == null || supperClass == Object.class) //
            && (member.getModifiers() & Modifier.PUBLIC) != 0 //
            && (classMofifiers & Modifier.PUBLIC) != 0 //
        ) {
            return false;
        }
        
        AccessibleObject obj = (AccessibleObject) member;
        
        try {
            obj.setAccessible(true);
            return true;
        } catch (AccessControlException error) {
            setAccessibleEnable = false;
            return false;
        }
    }
    
    public static Field getField(Class<?> clazz, String fieldName, Field[] declaredFields) {
        Field field = getField0(clazz, fieldName, declaredFields);
        if (field == null) {
            field = getField0(clazz, "_" + fieldName, declaredFields);
        }
        
        if (field == null) {
            field = getField0(clazz, "m_" + fieldName, declaredFields);
        }
        
        if (field == null) {
            String mName = "m" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            field = getField0(clazz, mName, declaredFields);
        }
        
        return field;
    }

    private static Field getField0(Class<?> clazz, String fieldName, Field[] declaredFields) {
        for (Field item : declaredFields) {
            if (fieldName.equals(item.getName())) {
                return item;
            }
        }
        
        Class<?> superClass = clazz.getSuperclass();
        return superClass != null //
               && superClass != Object.class //
                   ? getField(superClass, fieldName, superClass.getDeclaredFields()) //
                   : null;
    }
}
