package com.wl.weblog.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description Json工具类
 * @Author WL
 * @Date 2020/11/19 21:59
 * @Version 1.0
 **/
@Slf4j
public class JsonUtil {
    public static final ObjectMapper INSTANCE = new ObjectMapper();

    public static String toJSonString(Object obj) {
        try {
            return INSTANCE.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
    }
}
