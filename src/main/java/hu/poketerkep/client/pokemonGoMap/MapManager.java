package hu.poketerkep.client.pokemonGoMap;

import hu.poketerkep.client.dataservice.LocationConfigDataService;
import hu.poketerkep.client.dataservice.UserConfigDataService;
import hu.poketerkep.client.exception.NoMoreLocationException;
import hu.poketerkep.client.json.RawDataJsonDto;
import hu.poketerkep.client.model.LocationConfig;
import hu.poketerkep.client.model.UserConfig;
import hu.poketerkep.client.pokemonGoMap.instance.PGMConfiguration;
import hu.poketerkep.client.pokemonGoMap.instance.PGMInstance;
import hu.poketerkep.client.service.LocationConfigManagerService;
import hu.poketerkep.client.service.UserConfigManagerService;
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
public class MapManager implements SmartLifecycle {
    private final UserConfigManagerService userConfigManagerService;
    private final UserConfigDataService userConfigDataService;
    private final LocationConfigDataService locationConfigDataService;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final LocationConfigManagerService locationConfigManagerService;
    // The running state of the Instance Manager
    private boolean running;
    private List<PGMInstance> PGMInstances = new ArrayList<>();
    private List<TorInstance> torInstances = new ArrayList<>();
    @Value("${instance-count:3}")
    private int instanceCount;
    @Value("${use-tor:false}")
    private boolean useTor;
    @Value("${users-per-instace:10}")
    private int usersPerInstance;

    @Autowired
    public MapManager(LocationConfigDataService locationConfigDataService, UserConfigDataService userConfigDataService, UserConfigManagerService userConfigManagerService, LocationConfigManagerService locationConfigManagerService) {
        this.locationConfigDataService = locationConfigDataService;
        this.userConfigDataService = userConfigDataService;
        this.userConfigManagerService = userConfigManagerService;
        this.locationConfigManagerService = locationConfigManagerService;
    }

    @Override
    public void start() {
        // Create instances (TODO intellingent instance creation)
        for (int i = 0; i < instanceCount; i++) {
            try {
                PGMInstance instance = createInstance(i);
                instance.start();
                PGMInstances.add(instance);
            } catch (NoMoreLocationException e) {
                logger.info("No more locations");
                break;
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        running = true;
    }


    /**
     * Update last used values for users and locations
     */
    @Scheduled(fixedRate = 30 * 1000)
    public void updateUserAndLocationLastUsed() {
        logger.info("Updating User and Location last used values...");
        if (isRunning()) {

            //Update users
            userConfigManagerService.updateLastUsedTimes(getUserConfigs());

            //Update locations
            locationConfigManagerService.updateLastUsedTimes(getLocationConfigs());

            logger.info("Done!");
        } else {
            logger.warning("No instances running!");
        }
    }


    /**
     * Get the UserConfigs from all the instances
     *
     * @return List of UserConfigs
     */
    private List<UserConfig> getUserConfigs() {
        return PGMInstances.stream()
                .map(PGMInstance::getConf)
                .map(PGMConfiguration::getUsers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Get the LocationConfigs from all the instances
     *
     * @return List of LocationConfigs
     */
    private List<LocationConfig> getLocationConfigs() {
        return PGMInstances.stream()
                .map(PGMInstance::getConf)
                .map(PGMConfiguration::getLocation)
                .collect(Collectors.toList());
    }

    /**
     * Create a PokemonGo Map Instance
     *
     * @param id the ID of the instance
     * @return the instance itself
     * @throws Exception when something bad happens
     */
    private PGMInstance createInstance(int id) throws Exception {
        PGMConfiguration conf = new PGMConfiguration();

        // If tor is used
        if (useTor) {
            TorInstance torInstance = new TorInstance(id);
            torInstance.start();
            conf.setProxyPort(Optional.of(torInstance.getProxyPort()));
            torInstances.add(torInstance);
        }

        // Set user
        List<UserConfig> users = userConfigDataService.getUnusedUsers(usersPerInstance);
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

        userConfigManagerService.updateLastUsedTimes(users);
        locationConfigManagerService.updateLastUsedTimes(location);

        return new PGMInstance(this, conf, id);
    }

    public List<RawDataJsonDto> getRawData() {
        return PGMInstances.parallelStream()
                .map(PGMInstance::getRawData)
                .collect(Collectors.toList());
    }


    @Override
    public void stop() {
        running = false;

        logger.info("Stopping instances...");
        PGMInstances.forEach(PGMInstance::stop);

        logger.info("Stopping tor instances...");
        torInstances.forEach(torInstance -> torInstance.setStop(true));

        logger.info("Releasing Users and Locations");

        //Release users
        userConfigManagerService.releaseUsers(getUserConfigs());

        //Release locations
        locationConfigManagerService.releaseLocations(getLocationConfigs());
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

    /**
     * When an user account is banned
     *
     * @param userConfig
     */
    public void onUserBanned(UserConfig userConfig) {
        if (!userConfig.getBanned()) {
            logger.warning("User " + userConfig.getUserName() + " was banned");
            userConfigDataService.setBanned(userConfig);
        }
    }
}
