/**
 * Copyright © 2018
 *
 * @Package: JsonUtils.java
 * @author: Administrator
 * @date: 2018年5月30日 下午10:55:45
 */
package com.kenhome.util;
/**
 * @Description:FastJson工具类
 * @author: cmk
 * @date: 2018年5月30日 下午10:55:45
 */

import java.util.List;
import java.util.Map;
import javax.xml.crypto.dsig.keyinfo.KeyValue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;


public class JsonUtils {

    private static final SerializeConfig config;

    static {
        config = new SerializeConfig();
        config.put(java.util.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格式
        config.put(java.sql.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格式
    }

    private static final SerializerFeature[] features = {SerializerFeature.WriteMapNullValue, // 输出空置字段
            SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null
            SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null
            SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullStringAsEmpty // 字符类型字段如果为null，输出为""，而不是null
    };


    public static String toJSONString(Object object) {
        return JSON.toJSONString(object, config, features);
    }

    public static String toJSONNoFeatures(Object object) {
        return JSON.toJSONString(object, config, SerializerFeature.DisableCircularReferenceDetect);
    }


    /**
     * 转化为JSONObject
     * @param text
     * @return
     */
    public static JSONObject toJsonObject(String text) {
        return JSONObject.parseObject(text);
    }

    /**
     * 转化为object
     * @param text
     * @return
     */
    public static Object toObject(String text) {
        return JSON.parse(text);
    }


    /**
     * 获得Long值
     * @param text
     * @param param
     * @return
     */
    public static Long getLong(String text, String param) {
        return JSON.parseObject(text).getLong(param);
    }

    /**
     * 获得String
     * @param text
     * @param param
     * @return
     */
    public static String getString(String text, String param) {
        return JSON.parseObject(text).getString(param);
    }

    /**
     * 转化为javaBean
     * @param text
     * @param clazz
     * @return
     */
    public static <T> T toBean(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    /**
     * 转换为数组
     * @param text
     * @return
     */
    public static <T> Object[] toArray(String text) {
        return toArray(text, null);
    }

    /**
     * 转换为数组
     * @param text
     * @return
     */
    public static <T> Object[] toArray(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz).toArray();
    }

    /**
     * 转化为list集合
     * @param text
     * @param clazz
     * @return
     */
    public static <T> List<T> toList(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz);
    }

    /**
     * 将javabean转化为序列化的json字符串
     * @param keyvalue
     * @return
     */
    public static Object beanToJson(KeyValue keyvalue) {
        String textJson = JSON.toJSONString(keyvalue);
        Object objectJson = JSON.parse(textJson);
        return objectJson;
    }

    /**
     * 将string转化为序列化的json字符串
     * @param
     * @return
     */
    public static Object textToJson(String text) {
        Object objectJson = JSON.parse(text);
        return objectJson;
    }

    /**
     * json字符串转化为map
     * @param s
     * @return
     */
    public static Map<String, Object> stringToMap(String s) {
        Map<String, Object> m = JSONObject.parseObject(s);
        return m;
    }

    /**
     * 将map转化为string
     * @param m
     * @return
     */
    public static String collectToString(Map<String, Object> m) {
        String s = JSONObject.toJSONString(m);
        return s;
    }


    /**
     * 把对象转换为指定对象
     */
    public static <T> T toObjectFromSource(Object source, Class<T> target) {
        return toBean(toJSONString(source), target);
    }

}
