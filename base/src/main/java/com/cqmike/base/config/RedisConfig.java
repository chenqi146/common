package com.cqmike.base.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @program: common
 * @description: redis
 * @author: chen qi
 * @create: 2020-11-08 12:01
 **/
@Configuration
@ConditionalOnClass({RedisOperations.class})
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {
    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);
    /**
     * 自定義 redisTemplate （方法名一定要叫 redisTemplate 因為 @Bean 是根據方法名配置這個bean的name的）
     * 默認的 RedisTemplate<K,V> 為泛型，使用時不太方便，自定義為 <String, Object>
     * 默認序列化方式為 JdkSerializationRedisSerializer 序列化後的內容不方便閱讀，改為序列化成 json
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("redis初始化序列方式！");
        // 配置 json 序列化器 - Jackson2JsonRedisSerializer
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY);
        jacksonSerializer.setObjectMapper(objectMapper);
        // 創建並配置自定義 RedisTemplateRedisOperator
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        // 將 key 序列化成字符串
        template.setKeySerializer(new StringRedisSerializer());
        // 將 hash 的 key 序列化成字符串
        template.setHashKeySerializer(new StringRedisSerializer());
        // 將 value 序列化成 json
        template.setValueSerializer(jacksonSerializer);
        // 將 hash 的 value 序列化成 json
        template.setHashValueSerializer(jacksonSerializer);
        template.afterPropertiesSet();
        return template;
    }
}

