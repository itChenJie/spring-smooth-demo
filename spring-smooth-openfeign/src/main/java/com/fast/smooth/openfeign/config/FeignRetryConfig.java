package com.fast.smooth.openfeign.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignRetryConfig {

    @Bean
    public Retryer feignRetryer() {
        // 参数: 初始间隔时间(ms), 最大间隔时间(ms), 最大重试次数
        return new Retryer.Default(100, 1000, 1);
    }
}
