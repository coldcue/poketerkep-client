package hu.poketerkep.client.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setReadTimeout(5000)
                .setConnectTimeout(5000)
                //.basicAuthorization("client", "6SkN4N9F4ZPUHsT")
                .build();
    }
}
