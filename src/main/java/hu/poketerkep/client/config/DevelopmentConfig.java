package hu.poketerkep.client.config;


import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("development")
public class DevelopmentConfig {

    @Bean(autowire = Autowire.BY_NAME)
    public String masterAPIEndpoint() {
        return "http://localhost:8090";
    }

}