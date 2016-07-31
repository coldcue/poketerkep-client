package hu.poketerkep.client;

import hu.poketerkep.client.json.RawDataJsonDto;
import hu.poketerkep.client.model.LocationConfig;
import hu.poketerkep.client.model.UserConfig;
import hu.poketerkep.client.pokemonGoMap.PokemonGoMapConfiguration;
import hu.poketerkep.client.pokemonGoMap.PokemonGoMapInstance;
import hu.poketerkep.client.service.LocationConfigDataService;
import hu.poketerkep.client.service.UserConfigDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class PokemonGoMapInstanceManager implements SmartLifecycle {
    private final UserConfigDataService userConfigDataService;
    private final LocationConfigDataService locationConfigDataService;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private boolean running;
    private List<PokemonGoMapInstance> pokemonGoMapInstances = new ArrayList<>();

    @Autowired
    public PokemonGoMapInstanceManager(LocationConfigDataService locationConfigDataService, UserConfigDataService userConfigDataService) {
        this.locationConfigDataService = locationConfigDataService;
        this.userConfigDataService = userConfigDataService;
    }

    @Override
    public void start() {
        // Create instances (TODO intellingent instance creation)
        for (int i = 0; i < 3; i++) {
            try {
                PokemonGoMapInstance instance = createInstance(i);
                instance.start();
                pokemonGoMapInstances.add(instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        running = true;
    }

    @Scheduled(fixedRate = 30 * 1000)
    public void updateUserAndLocationLastUsed() {
        logger.info("Updating User and Location last used values...");
        if (isRunning()) {

            //Update users
            pokemonGoMapInstances.stream()
                    .map(PokemonGoMapInstance::getConf)
                    .map(PokemonGoMapConfiguration::getUser)
                    .map(UserConfig::getUserName)
                    .forEach(userConfigDataService::updateUserLastUsed);

            //Update locations
            pokemonGoMapInstances.stream()
                    .map(PokemonGoMapInstance::getConf)
                    .map(PokemonGoMapConfiguration::getLocation)
                    .map(LocationConfig::getLocationId)
                    .forEach(locationConfigDataService::updateUserLastUsed);

            logger.info("Done!");
        } else {
            logger.warning("No instances running!");
        }
    }

    private PokemonGoMapInstance createInstance(int id) throws Exception {
        PokemonGoMapConfiguration conf = new PokemonGoMapConfiguration();

        // Set user
        UserConfig user = userConfigDataService.getUnusedUser();
        if (user == null) {
            throw new Exception("No user found");
        }
        conf.setUser(user);

        // Set location
        LocationConfig location = locationConfigDataService.getUnusedLocation();
        if (location == null) {
            throw new Exception("No location found");
        }
        conf.setLocation(location);

        //Set google maps key
        conf.setGoogleMapsKey("AIzaSyC4w7rMpg48S8u8eJBiEESCEc6cKj5iTyI");

        return new PokemonGoMapInstance(conf, id);
    }

    public List<RawDataJsonDto> getRawData() {
        return pokemonGoMapInstances.parallelStream()
                .map(PokemonGoMapInstance::getRawData)
                .collect(Collectors.toList());
    }


    @Override
    public void stop() {
        running = false;
        pokemonGoMapInstances.forEach(PokemonGoMapInstance::stop);
    }


    @Override
    public boolean isAutoStartup() {
        return true;
    }


    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }


    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
