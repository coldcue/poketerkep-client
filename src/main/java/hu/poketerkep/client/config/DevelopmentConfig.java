package hu.poketerkep.client.config;


import hu.poketerkep.client.pokemonGoMap.PokemonGoMapConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("development")
public class DevelopmentConfig {

    @Bean
    PokemonGoMapConfiguration pokemonGoMapConfiguration() {
        return new PokemonGoMapConfiguration("csicskacsalo",
                "jelszo123",
                10,
                "AIzaSyC4w7rMpg48S8u8eJBiEESCEc6cKj5iTyI",
                "47.426195 19.041047");
    }
}