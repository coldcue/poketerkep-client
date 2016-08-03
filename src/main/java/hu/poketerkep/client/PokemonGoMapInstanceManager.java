package hu.poketerkep.client;

import hu.poketerkep.client.exception.NoMoreLocationException;
import hu.poketerkep.client.json.RawDataJsonDto;
import hu.poketerkep.client.model.LocationConfig;
import hu.poketerkep.client.model.UserConfig;
import hu.poketerkep.client.pokemonGoMap.PokemonGoMapConfiguration;
import hu.poketerkep.client.pokemonGoMap.PokemonGoMapInstance;
import hu.poketerkep.client.service.LocationConfigDataService;
import hu.poketerkep.client.service.UserConfigDataService;
import hu.poketerkep.client.tor.TorInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class PokemonGoMapInstanceManager implements SmartLifecycle {
    private static final int NUM_USERS = 10;
    private final UserConfigDataService userConfigDataService;
    private final LocationConfigDataService locationConfigDataService;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private boolean running;
    private List<PokemonGoMapInstance> pokemonGoMapInstances = new ArrayList<>();
    private List<TorInstance> torInstances = new ArrayList<>();

    @Value("${instance-count:3}")
    private int instanceCount;

    @Value("${pokemap-threads:3}")
    private int pokemapThreads;

    @Value("${use-tor:false}")
    private boolean useTor;

    @Autowired
    public PokemonGoMapInstanceManager(LocationConfigDataService locationConfigDataService, UserConfigDataService userConfigDataService) {
        this.locationConfigDataService = locationConfigDataService;
        this.userConfigDataService = userConfigDataService;
    }

    @Override
    public void start() {
        // Create instances (TODO intellingent instance creation)
        for (int i = 0; i < instanceCount; i++) {
            try {
                PokemonGoMapInstance instance = createInstance(i);
                instance.start();
                pokemonGoMapInstances.add(instance);
            } catch (NoMoreLocationException e) {
                logger.info("No more locations");
                break;
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
                    .map(PokemonGoMapConfiguration::getUsers)
                    .flatMap(Collection::stream)
                    .map(UserConfig::getUserName)
                    .forEach(userConfigDataService::updateUserLastUsed);

            //Update locations
            pokemonGoMapInstances.stream()
                    .map(PokemonGoMapInstance::getConf)
                    .map(PokemonGoMapConfiguration::getLocation)
                    .map(LocationConfig::getLocationId)
                    .forEach(locationConfigDataService::updateLocationLastUsed);

            logger.info("Done!");
        } else {
            logger.warning("No instances running!");
        }
    }

    private PokemonGoMapInstance createInstance(int id) throws Exception {
        PokemonGoMapConfiguration conf = new PokemonGoMapConfiguration();

        // If tor is used
        if (useTor) {
            TorInstance torInstance = new TorInstance(id);
            torInstance.start();
            conf.setProxyPort(Optional.of(torInstance.getProxyPort()));
            torInstances.add(torInstance);
        }

        // Set user
        List<UserConfig> users = userConfigDataService.getUnusedUsers(NUM_USERS);
        if (users.size() == 0) {
            throw new Exception("No user found");
        }
        conf.setUsers(users);

        // Set location
        LocationConfig location = locationConfigDataService.getUnusedLocation();
        if (location == null) {
            throw new NoMoreLocationException();
        }
        conf.setLocation(location);

        //Set google maps key
        conf.setGoogleMapsKey("AIzaSyC4w7rMpg48S8u8eJBiEESCEc6cKj5iTyI");

        //Set threads
        conf.setThreads(pokemapThreads);

        users.stream()
                .map(UserConfig::getUserName)
                .forEach(userConfigDataService::updateUserLastUsed);
        locationConfigDataService.updateLocationLastUsed(location.getLocationId());

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
        torInstances.forEach(torInstance -> torInstance.setStop(true));
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
