package com.kob.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {   //将RestTemplate注入到Ioc容器，用于在多个springboot中通信
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
