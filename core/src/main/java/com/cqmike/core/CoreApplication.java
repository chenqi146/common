package com.cqmike.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "com.cqmike")
@ComponentScan(basePackages = "com.cqmike")
@EnableJpaRepositories(basePackages = "com.cqmike")
@SpringBootApplication(scanBasePackages = {"com.cqmike"})
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

}
