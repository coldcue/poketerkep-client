package hu.poketerkep.client.config;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import hu.poketerkep.client.pokemonGoMap.PokemonGoMapConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.*;
import java.util.logging.Logger;

@Configuration
@Profile("default")
public class ProductionConfig {
    private final Logger logger = Logger.getLogger(ProductionConfig.class.getName());

    @Value("${ec2-instanceid:@null}")
    private String ec2InstanceId;

    @Autowired
    private AWSCredentialsProvider awsCredentialsProvider;

    @Bean
    Instance ec2Instance() throws Exception {
        logger.info("Getting EC2 Instance details...");
        AmazonEC2 amazonEC2 = AmazonEC2ClientBuilder.standard().withRegion(Regions.EU_WEST_1).withCredentials(awsCredentialsProvider).build();
        List<Reservation> reservations = amazonEC2.describeInstances().getReservations();
        Optional<Instance> first = reservations.stream().map(Reservation::getInstances).flatMap(Collection::stream).filter(instance -> instance.getInstanceId().equals(ec2InstanceId)).findFirst();

        if (first.isPresent()) {
            return first.get();
        } else {
            throw new Exception("This is not an EC2 instance, try development profile");
        }
    }

    @Bean
    PokemonGoMapConfiguration pokemonGoMapConfiguration() throws Exception {
        logger.info("Getting PokemonGoMap Configuration...");
        List<Tag> tags = ec2Instance().getTags();
        Map<String, String> properties = new HashMap<>();

        for (Tag tag : tags) {
            properties.put(tag.getKey(), tag.getValue());
        }

        return new PokemonGoMapConfiguration(properties.get("Username"),
                properties.get("Password"),
                Integer.parseInt(properties.get("Steps")),
                "AIzaSyC4w7rMpg48S8u8eJBiEESCEc6cKj5iTyI",
                properties.get("Location"));
    }
}