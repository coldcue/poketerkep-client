package hu.poketerkep.client.config;


import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("default")
public class ProductionConfig {

    @Bean(autowire = Autowire.BY_NAME)
    public String masterAPIEndpoint() {
        return "http://master.poketerkep.hu";
    }
}