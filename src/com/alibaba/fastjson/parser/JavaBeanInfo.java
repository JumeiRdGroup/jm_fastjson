package com.alibaba.fastjson.parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;

class JavaBeanInfo {
    final Constructor<?> defaultConstructor;
    final int            defaultConstructorParameterSize;
    final Constructor<?> creatorConstructor;
    final Method         factoryMethod;

    final FieldInfo[]    fields;
    final FieldInfo[]    sortedFields;
    final JSONType       jsonType;
    boolean              ordered = false;

    JavaBeanInfo(Class<?> clazz, //
                 Constructor<?> defaultConstructor, //
                 Constructor<?> creatorConstructor, //
                 Method factoryMethod, //
                 FieldInfo[] fields, //
                 FieldInfo[] sortedFields, //
                 JSONType jsonType
                 ){

        this.defaultConstructor = defaultConstructor;
        this.creatorConstructor = creatorConstructor;
        this.factoryMethod = factoryMethod;
        this.fields = fields;
        this.jsonType = jsonType; 

        sortedFields = computeSortedFields(fields, sortedFields);
        this.sortedFields = (Arrays.equals(fields, sortedFields)) ? fields : sortedFields;

        defaultConstructorParameterSize = defaultConstructor != null ? defaultConstructor.getParameterTypes().length : 0;
    }

    private FieldInfo[] computeSortedFields(FieldInfo[] fields, FieldInfo[] sortedFields) {
        if (jsonType == null) {
            return sortedFields;
        }
        
        String[] orders = jsonType.orders();
        if (orders != null && orders.length != 0) {
            boolean containsAll = true;
            for (int i = 0; i < orders.length; ++i) {
                boolean got = false;
                for (int j = 0; j < sortedFields.length; ++j) {
                    if (sortedFields[j].name.equals(orders[i])) {
                        got = true;
                        break;
                    }
                }
                if (!got) {
                    containsAll = false;
                    break;
                }
            }
            
            if (!containsAll) {
                return sortedFields;
            }

            if (orders.length == fields.length) {
                boolean orderMatch = true;
                for (int i = 0; i < orders.length; ++i) {
                    if (!sortedFields[i].name.equals(orders[i])) {
                        orderMatch = false;
                        break;
                    }
                }
                
                if (orderMatch) {
                    return sortedFields;
                }
                
                FieldInfo[] newSortedFields = new FieldInfo[sortedFields.length];
                for (int i = 0; i < orders.length; ++i) {
                    for (int j = 0; j < sortedFields.length; ++j) {
                        if (sortedFields[j].name.equals(orders[i])) {
                            newSortedFields[i] = sortedFields[j];
                            break;
                        }
                    }
                }
                sortedFields = newSortedFields;
                ordered = true;
                return newSortedFields;
            }
            
            FieldInfo[] newSortedFields = new FieldInfo[sortedFields.length];
            for (int i = 0; i < orders.length; ++i) {
                for (int j = 0; j < sortedFields.length; ++j) {
                    if (sortedFields[j].name.equals(orders[i])) {
                        newSortedFields[i] = sortedFields[j];
                        break;
                    }
                }
            }
            
            int fieldIndex = orders.length;
            for (int i = 0; i < sortedFields.length; ++i) {
                boolean contains = false;
                for (int j = 0; j < newSortedFields.length && j < fieldIndex; ++j) {
                    if (newSortedFields[i].equals(sortedFields[j])) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    newSortedFields[fieldIndex] = sortedFields[i];
                    fieldIndex++;
                }
            }
            ordered = true;
        }

        return sortedFields;
    }

    static boolean addField(List<FieldInfo> fields, FieldInfo field, boolean fieldOnly) {
        if (!fieldOnly) {
            for (int i = 0, size = fields.size(); i < size; ++i) {
                FieldInfo item = fields.get(i);
                if (item.name.equals(field.name)) {
                    if (item.getOnly && !field.getOnly) {
                        continue;
                    }
    
                    return false;
                }
            }
        }

        fields.add(field);

        return true;
    }

    public static JavaBeanInfo build(Class<?> clazz, //
                                     int classModifiers, //
                                     Type type, //
                                     boolean fieldOnly, //
                                     boolean jsonTypeSupport, //
                                     boolean jsonFieldSupport, //
                                     boolean fieldGenericSupport
    ) {
        List<FieldInfo> fieldList = new ArrayList<FieldInfo>();

        // DeserializeBeanInfo beanInfo = null;
        Constructor<?> defaultConstructor = null;
        if ((classModifiers & Modifier.ABSTRACT) == 0) {
            try {
                defaultConstructor = clazz.getDeclaredConstructor();
            } catch (Exception e) {
                // skip
            }

            if (defaultConstructor == null) {
                if (clazz.isMemberClass() && (classModifiers & Modifier.STATIC) == 0) { // for inner none static class
                    for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                        Class<?>[] parameterTypes = constructor.getParameterTypes();
                        if (parameterTypes.length == 1 && parameterTypes[0].equals(clazz.getDeclaringClass())) {
                            defaultConstructor = constructor;
                            break;
                        }
                    }
                }
            }
        }
        
        Constructor<?> creatorConstructor = null;
        Method[] methods = fieldOnly //
            ? null //
            : clazz.getMethods();
        
        final Field[] declaredFields = fieldOnly //
            ? null //
            : clazz.getDeclaredFields();

        if (defaultConstructor == null // 
                && !(clazz.isInterface() || (classModifiers & Modifier.ABSTRACT) != 0) //
                ) {
            creatorConstructor = null;
            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                JSONCreator annotation = constructor.getAnnotation(JSONCreator.class);
                if (annotation != null) {
                    if (creatorConstructor != null) {
                        throw new JSONException("multi-json creator");
                    }

                    creatorConstructor = constructor;
                    break;
                }
            }
            
            if (creatorConstructor != null) {
                TypeUtils.setAccessible(clazz, creatorConstructor, classModifiers);

                Class<?>[] parameterTypes = creatorConstructor.getParameterTypes();
                Type[] getGenericParameterTypes = fieldGenericSupport //
                    ? creatorConstructor.getGenericParameterTypes() //
                    : parameterTypes;
                    
                for (int i = 0; i < parameterTypes.length; ++i) {
                    Annotation[] paramAnnotations = creatorConstructor.getParameterAnnotations()[i];
                    JSONField fieldAnnotation = null;
                    for (Annotation paramAnnotation : paramAnnotations) {
                        if (paramAnnotation instanceof JSONField) {
                            fieldAnnotation = (JSONField) paramAnnotation;
                            break;
                        }
                    }
                    if (fieldAnnotation == null) {
                        throw new JSONException("illegal json creator");
                    }

                    Class<?> fieldClass = parameterTypes[i];
                    Type fieldType = getGenericParameterTypes[i];
                    Field field = TypeUtils.getField(clazz, fieldAnnotation.name(), declaredFields);

                    if (field != null) {
                        TypeUtils.setAccessible(clazz, field, classModifiers);
                    }

                    final int ordinal = fieldAnnotation.ordinal();
                    final int serialzeFeatures = SerializerFeature.of(fieldAnnotation.serialzeFeatures());
                    FieldInfo fieldInfo = new FieldInfo(fieldAnnotation.name(), //
                                                        clazz, //
                                                        fieldClass, //
                                                        fieldType, //
                                                        field, //
                                                        ordinal, //
                                                        serialzeFeatures);
                    addField(fieldList, fieldInfo, fieldOnly);
                }

                FieldInfo[] fields = new FieldInfo[fieldList.size()];
                fieldList.toArray(fields);

                FieldInfo[] sortedFields = new FieldInfo[fields.length];
                System.arraycopy(fields, 0, sortedFields, 0, fields.length);
                Arrays.sort(sortedFields);

                JSONType jsonType = jsonTypeSupport ? clazz.getAnnotation(JSONType.class) : null;
                return new JavaBeanInfo(clazz, null, creatorConstructor, null, fields, sortedFields, jsonType);
            }

            Method factoryMethod = null;
            {
                for (Method method : methods) {
                    if ((!Modifier.isStatic(method.getModifiers())) //
                        || !clazz.isAssignableFrom(method.getReturnType()) //
                    ) {
                        continue;
                    }

                    JSONCreator annotation = method.getAnnotation(JSONCreator.class);
                    if (annotation != null) {
                        if (factoryMethod != null) {
                            throw new JSONException("multi-json creator");
                        }

                        factoryMethod = method;
                        break;
                    }
                }
            }
            
            if (factoryMethod != null) {
                TypeUtils.setAccessible(clazz, factoryMethod, classModifiers);

                Class<?>[] parameterTypes = factoryMethod.getParameterTypes();
                Type[] genericParameterTypes = fieldGenericSupport //
                    ? factoryMethod.getGenericParameterTypes() //
                    : parameterTypes;

                for (int i = 0; i < parameterTypes.length; ++i) {
                    Annotation[] paramAnnotations = factoryMethod.getParameterAnnotations()[i];
                    JSONField fieldAnnotation = null;
                    for (Annotation paramAnnotation : paramAnnotations) {
                        if (paramAnnotation instanceof JSONField) {
                            fieldAnnotation = (JSONField) paramAnnotation;
                            break;
                        }
                    }
                    if (fieldAnnotation == null) {
                        throw new JSONException("illegal json creator");
                    }

                    Class<?> fieldClass = parameterTypes[i];
                    Type fieldType = genericParameterTypes[i];
                    Field field = TypeUtils.getField(clazz, fieldAnnotation.name(), declaredFields);
                    final int ordinal = fieldAnnotation.ordinal();
                    final int serialzeFeatures = SerializerFeature.of(fieldAnnotation.serialzeFeatures());
                    FieldInfo fieldInfo = new FieldInfo(fieldAnnotation.name() //
                                                        , clazz //
                                                        , fieldClass //
                                                        , fieldType //
                                                        , field //
                                                        , ordinal //
                                                        , serialzeFeatures);
                    addField(fieldList, fieldInfo, fieldOnly);
                }

                FieldInfo[] fields = new FieldInfo[fieldList.size()];
                fieldList.toArray(fields);

                FieldInfo[] sortedFields = new FieldInfo[fields.length];
                System.arraycopy(fields, 0, sortedFields, 0, fields.length);
                Arrays.sort(sortedFields);

                if (Arrays.equals(fields, sortedFields)) {
                    sortedFields = fields;
                }

                JSONType jsonType = jsonTypeSupport ? clazz.getAnnotation(JSONType.class) : null;
                JavaBeanInfo beanInfo = new JavaBeanInfo(clazz, null, null, factoryMethod, fields, sortedFields, jsonType);
                return beanInfo;
            }

            throw new JSONException("default constructor not found. " + clazz);
        }

        if (defaultConstructor != null) {
            TypeUtils.setAccessible(clazz, defaultConstructor, classModifiers);
        }

        if (!fieldOnly) {
            for (Method method : methods) {
                int ordinal = 0, serialzeFeatures = 0;
                String methodName = method.getName();
                if (methodName.length() < 4 //
                        || Modifier.isStatic(method.getModifiers())
                        ) {
                    continue;
                }

                // support builder set

                Class<?> returnType = method.getReturnType();
                if ((!(returnType == Void.TYPE || returnType == clazz)) //
                        || method.getParameterTypes().length != 1 //
                        || method.getDeclaringClass() == Object.class //
                        ) {
                    continue;
                }

                JSONField annotation = jsonFieldSupport ? method.getAnnotation(JSONField.class) : null;

                if (annotation == null && jsonFieldSupport) {
                    annotation = TypeUtils.getSupperMethodAnnotation(clazz, method);
                }

                if (annotation != null) {
                    if (!annotation.deserialize()) {
                        continue;
                    }

                    ordinal = annotation.ordinal();
                    serialzeFeatures = SerializerFeature.of(annotation.serialzeFeatures());

                    if (annotation.name().length() != 0) {
                        String propertyName = annotation.name();
                        addField(fieldList,
                                 new FieldInfo(propertyName, //
                                               method, //
                                               null, //
                                               clazz, //
                                               type, //
                                               ordinal, //
                                               serialzeFeatures, //
                                               annotation, //
                                               null, //
                                               fieldGenericSupport), //
                                 fieldOnly);
                        TypeUtils.setAccessible(clazz, method, classModifiers);
                        continue;
                    }
                }

                if (!methodName.startsWith("set")) {
                    continue;
                }

                char c3 = methodName.charAt(3);

                String propertyName;
                if (Character.isUpperCase(c3)) {
                    if (TypeUtils.compatibleWithJavaBean) {
                        propertyName = TypeUtils.decapitalize(methodName.substring(3));
                    } else {
                        propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                    }
                } else if (c3 == '_') {
                    propertyName = methodName.substring(4);
                } else if (c3 == 'f') {
                    propertyName = methodName.substring(3);
                } else if (methodName.length() >= 5 && Character.isUpperCase(methodName.charAt(4))) {
                    propertyName = TypeUtils.decapitalize(methodName.substring(3));
                } else {
                    continue;
                }

                Field field = TypeUtils.getField(clazz, propertyName, declaredFields);
                if (field == null && method.getParameterTypes()[0] == boolean.class) {
                    String isFieldName = "is" + Character.toUpperCase(propertyName.charAt(0))
                                         + propertyName.substring(1);
                    field = TypeUtils.getField(clazz, isFieldName, declaredFields);
                }

                if (field != null) {
                    JSONField fieldAnnotation = jsonFieldSupport ? field.getAnnotation(JSONField.class) : null;

                    if (fieldAnnotation != null) {
                        ordinal = fieldAnnotation.ordinal();
                        serialzeFeatures = SerializerFeature.of(fieldAnnotation.serialzeFeatures());

                        if (fieldAnnotation.name().length() != 0) {
                            propertyName = fieldAnnotation.name();
                            addField(fieldList, //
                                     new FieldInfo(propertyName, method, field, clazz, type, //
                                                   ordinal, serialzeFeatures, annotation, fieldAnnotation,
                                                   fieldGenericSupport), //
                                     fieldOnly);
                            continue;
                        }
                    }

                }
                addField(fieldList, //
                         new FieldInfo(propertyName, method, null, clazz, type, ordinal, serialzeFeatures, annotation,
                                       null, fieldGenericSupport), //
                         fieldOnly);
                TypeUtils.setAccessible(clazz, method, classModifiers);
            }
        }

        for (Field field : clazz.getFields()) {
            final String fieldName = field.getName();
            boolean contains = false;
            for (int i = 0, size = fieldList.size(); i < size; ++i) {
                FieldInfo item = fieldList.get(i);
                if (item.name.equals(fieldName)) {
                    contains = true;
                    continue;
                }
            }

            if (contains) {
                continue;
            }

            int ordinal = 0, serialzeFeatures = 0;
            String propertyName = fieldName;

            JSONField fieldAnnotation = jsonFieldSupport ? field.getAnnotation(JSONField.class) : null;

            if (fieldAnnotation != null) {
                ordinal = fieldAnnotation.ordinal();
                serialzeFeatures = SerializerFeature.of(fieldAnnotation.serialzeFeatures());

                if (fieldAnnotation.name().length() != 0) {
                    propertyName = fieldAnnotation.name();
                }
            }
            TypeUtils.setAccessible(clazz, field, classModifiers);
            addField(fieldList, //
                     new FieldInfo(propertyName, //
                                   null, //
                                   field, //
                                   clazz, //
                                   type, //
                                   ordinal, //
                                   serialzeFeatures, //
                                   null, //
                                   fieldAnnotation, // 
                                   fieldGenericSupport), // 
                     fieldOnly);
        }

        if (!fieldOnly) {
            for (Method method : clazz.getMethods()) {
                String methodName = method.getName();
                if (methodName.length() < 4 //
                    || Modifier.isStatic(method.getModifiers()) //
                ) {
                    continue;
                }
    
    
                if (methodName.startsWith("get") && Character.isUpperCase(methodName.charAt(3))) {
                    if (method.getParameterTypes().length != 0) {
                        continue;
                    }
    
                    Class<?> methodReturnType = method.getReturnType();
                    if (Collection.class.isAssignableFrom(methodReturnType) //
                        || Map.class.isAssignableFrom(methodReturnType) //
                    ) {
                        String propertyName;
    
                        JSONField annotation = jsonFieldSupport ? method.getAnnotation(JSONField.class) : null;
                        String annotationName;
                        
                        propertyName = annotation != null //
                                       && (annotationName = annotation.name()).length() > 0 //
                                           ? annotationName //
                                           : Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
    
                        addField(fieldList, //
                                 new FieldInfo(propertyName, method, null, clazz, type, 0, 0, annotation, null,
                                               fieldGenericSupport), //
                                 fieldOnly);
                        TypeUtils.setAccessible(clazz, method, classModifiers);
                    }
                }
            }
        }

        FieldInfo[] fields = new FieldInfo[fieldList.size()];
        fieldList.toArray(fields);

        FieldInfo[] sortedFields = new FieldInfo[fields.length];
        System.arraycopy(fields, 0, sortedFields, 0, fields.length);
        Arrays.sort(sortedFields);

        JSONType jsonType = jsonTypeSupport ? clazz.getAnnotation(JSONType.class) : null;
        return new JavaBeanInfo(clazz, defaultConstructor, null, null, fields, sortedFields, jsonType);
    }
}
