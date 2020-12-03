package com.cqmike.base.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @program: iot
 * @ClassName: JsonUtils
 * @Description: jackson工具类  todo 异常日志打印需要处理
 * @Author: chen qi
 * @Date: 2020/2/23 12:06
 * @Version: 1.0
 **/
public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper om = new ObjectMapper();

    static {

        // 对象的所有字段全部列入，还是其他的选项，可以忽略null等
        om.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 设置Date类型的序列化及反序列化格式
        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 忽略空Bean转json的错误
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 忽略未知属性，防止json字符串中存在，java对象中不存在对应属性的情况出现错误
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 注册一个时间序列化及反序列化的处理模块，用于解决jdk8中localDateTime等的序列化问题
        om.registerModule(new JavaTimeModule());
    }

    /**
     * 对象 => json字符串
     *
     * @param obj 源对象
     */
    public static <T> String toJson(T obj) {

        String json = null;
        if (obj != null) {
            try {
                json = om.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                log.warn(e.getMessage(), e);
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        return json;
    }

    /**
     * json字符串 => 对象
     *
     * @param json  源json串
     * @param clazz 对象类
     * @param <T>   泛型
     */
    public static <T> T parse(String json, Class<T> clazz) {

        return parse(json, clazz, null);
    }

    /**
     * json字符串 => 对象
     *
     * @param json 源json串
     * @param type 对象类型
     * @param <T>  泛型
     */
    public static <T> T parse(String json, TypeReference<T> type) {

        return parse(json, null, type);
    }


    /**
     * json => 对象处理方法
     * <br>
     * 参数clazz和type必须一个为null，另一个不为null
     * <br>
     * 此方法不对外暴露，访问权限为private
     *
     * @param json  源json串
     * @param clazz 对象类
     * @param type  对象类型
     * @param <T>   泛型
     */
    private static <T> T parse(String json, Class<T> clazz, TypeReference<T> type) {

        T obj = null;
        if (!StringUtils.isEmpty(json)) {
            try {
                if (clazz != null) {
                    obj = om.readValue(json, clazz);
                } else {
                    obj = om.readValue(json, type);
                }
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        return obj;
    }
}
