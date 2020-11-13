package com.cqmike.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author chen qi
 * @date 2020-11-13 17:04
 **/

@EntityScan(basePackages = "com.cqmike")
@ComponentScan(basePackages = "com.cqmike")
@EnableJpaRepositories(basePackages = "com.cqmike")
@SpringBootApplication(scanBasePackages = {"com.cqmike"})
@RestController
public class TestApplication {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @GetMapping("test")
    public String set(String k) {
        redisTemplate.opsForValue().set(k, "aaa");
        return "aaass";
    }

}
