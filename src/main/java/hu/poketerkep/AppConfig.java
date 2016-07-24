package hu.poketerkep;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import hu.poketerkep.pokemonGoMap.PokemonGoMapConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    PokemonGoMapConfiguration pokemonGoMapConfiguration() {
        return new PokemonGoMapConfiguration("csicskacsalo",
                "jelszo123",
                10,
                "AIzaSyC4w7rMpg48S8u8eJBiEESCEc6cKj5iTyI",
                "47.426195 19.041047");
    }

    @Bean
    AmazonDynamoDBAsync amazonDynamoDBAsync() {
        return AmazonDynamoDBAsyncClientBuilder.standard()
                .withCredentials(awsCredentialsProvider())
                .withRegion(Regions.EU_WEST_1).build();
    }


    private AWSCredentialsProvider awsCredentialsProvider() {
        return new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return awsCredentials();
            }

            @Override
            public void refresh() {

            }
        };
    }

    private AWSCredentials awsCredentials() {
        return new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return "AKIAJWPMWPIZHNKY3FMQ";
            }

            @Override
            public String getAWSSecretKey() {
                return "tPIb7uFWlELKxs3l38yUYOCc6fqg6DH5ahZcjfqd";
            }
        };
    }
}
