package com.cqmike.base.mvc;

import com.cqmike.base.enums.EnumConvertFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: common
 * @description: mvc
 * @author: chen qi
 * @create: 2020-11-24 22:11
 **/
@Configuration
public class BaseAppConfigurer implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new EnumConvertFactory());
    }
}
