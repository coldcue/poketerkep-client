package hu.poketerkep.client.config;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.util.EC2MetadataUtils;
import hu.poketerkep.client.pokemonGoMap.PokemonGoMapConfiguration;
import org.apache.commons.codec.digest.DigestUtils;
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

    @Value("${ec2-instance-id:null}")
    private String ec2InstanceIdParam;

    @Autowired
    private AWSCredentialsProvider awsCredentialsProvider;

    @Bean
    Instance ec2Instance() throws Exception {
        logger.info("Getting EC2 Instance ID... ");
        String instanceId = "null".equals(ec2InstanceIdParam) ? EC2MetadataUtils.getInstanceId() : ec2InstanceIdParam;
        logger.info("Instance ID is [" + instanceId + "]");

        logger.info("Getting EC2 Instance details...");
        AmazonEC2 amazonEC2 = AmazonEC2ClientBuilder.standard().withRegion(Regions.EU_WEST_1).withCredentials(awsCredentialsProvider).build();
        List<Reservation> reservations = amazonEC2.describeInstances().getReservations();
        Optional<Instance> first = reservations.stream().map(Reservation::getInstances).flatMap(Collection::stream).filter(instance -> instance.getInstanceId().equals(instanceId)).findFirst();

        if (first.isPresent()) {
            return first.get();
        } else {
            throw new Exception("This EC2 instance is not found");
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

        String username = "poketk" + properties.get("ClientId");
        String password = DigestUtils.md5Hex(username).substring(0, 8);
        String location = properties.get("Location");
        String steps = properties.get("Steps");

        logger.info("Username:"+username+" Password:"+password+" Location:"+location+" Steps:"+steps);

        return new PokemonGoMapConfiguration(username,
                password,
                Integer.parseInt(steps),
                "AIzaSyC4w7rMpg48S8u8eJBiEESCEc6cKj5iTyI",
                location);
    }
}