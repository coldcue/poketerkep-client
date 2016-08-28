package hu.poketerkep.client.map;

import hu.poketerkep.client.dataservice.UserConfigDataService;
import hu.poketerkep.client.exception.NoMoreLocationException;
import hu.poketerkep.client.exception.NoMoreUsersException;
import hu.poketerkep.client.map.java.PokemonMapInstance;
import hu.poketerkep.client.map.python.PGMInstance;
import hu.poketerkep.client.model.AllData;
import hu.poketerkep.shared.model.LocationConfig;
import hu.poketerkep.shared.model.UserConfig;
import hu.poketerkep.client.service.LocationConfigManagerService;
import hu.poketerkep.client.service.UserConfigManagerService;
import hu.poketerkep.client.tor.TorInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class MapManager implements SmartLifecycle {
    private final UserConfigManagerService userConfigManagerService;
    private final UserConfigDataService userConfigDataService;
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final LocationConfigManagerService locationConfigManagerService;
    // The running state of the Instance Manager
    private boolean running;
    private HashSet<MapInstance> mapInstances = new HashSet<>();
    private HashSet<TorInstance> torInstances = new HashSet<>();
    @Value("${instance-count:3}")
    private int instanceCount;
    @Value("${use-tor:false}")
    private boolean useTor;
    @Value("${users-per-instace:15}")
    private int usersPerInstance;
    @Value("${use-pgm:false}")
    private boolean usePgm;

    @Autowired
    public MapManager(UserConfigDataService userConfigDataService, UserConfigManagerService userConfigManagerService, LocationConfigManagerService locationConfigManagerService) {
        this.userConfigDataService = userConfigDataService;
        this.userConfigManagerService = userConfigManagerService;
        this.locationConfigManagerService = locationConfigManagerService;
    }

    @Override
    public void start() {
        running = true;
    }


    /**
     * Update last used values for users and locations
     */
    @Scheduled(fixedRate = 30 * 1000)
    public void updateUserAndLocationLastUsed() {
        log.info("Updating User and Location last used values...");
        if (isRunning()) {

            //Update users
            userConfigManagerService.updateLastUsedTimes(getUserConfigs());

            //Update locations
            locationConfigManagerService.updateLastUsedTimes(getLocationConfigs());

            log.info("Done!");
        } else {
            log.warning("No instances running!");
        }
    }

    /**
     * Check if there are new locations available
     */
    @Scheduled(fixedRate = 15 * 1000, initialDelay = 0)
    public void checkLocations() {
        log.info("Checking locations");
        if (isRunning()) {

            // Fill empty slots
            int emptySlots = instanceCount - mapInstances.size();
            for (int i = 0; i < emptySlots; i++) {
                try {
                    createInstance();
                } catch (NoMoreLocationException e) {
                    log.fine("No more locations");
                    break;
                } catch (NoMoreUsersException e) {
                    log.warning("No more users");
                    break;
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }
    }

    /**
     * Check which instances should be stopped
     */
    @Scheduled(fixedRate = 5 * 1000, initialDelay = 0)
    public void checkInstances() {
        log.fine("Checking instances");
        if (isRunning()) {

            // Stop instances that should be stopped (with concurrency)
            new ArrayList<>(mapInstances).stream()
                    .peek(MapInstance::check)
                    .filter(MapInstance::isShouldBeStopped)
                    .forEach(this::stopInstance);

        }
    }


    /**
     * Get the UserConfigs from all the instances
     *
     * @return List of UserConfigs
     */
    private List<UserConfig> getUserConfigs() {
        return mapInstances.stream()
                .map(MapInstance::getConfiguration)
                .map(MapConfiguration::getUsers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Get the LocationConfigs from all the instances
     *
     * @return List of LocationConfigs
     */
    private List<LocationConfig> getLocationConfigs() {
        return mapInstances.stream()
                .map(MapInstance::getConfiguration)
                .map(MapConfiguration::getLocation)
                .collect(Collectors.toList());
    }

    /**
     * Create a PokemonGo Map Instance
     *
     * @return the instance itself
     */
    private MapInstance createInstance() throws NoMoreUsersException, NoMoreLocationException, IOException {
        MapConfiguration conf = new MapConfiguration();

        // Set location
        Optional<LocationConfig> optionalLocation = locationConfigManagerService.getUnusedLocation(new ArrayList<>(getLocationConfigs()));
        if (!optionalLocation.isPresent()) {
            throw new NoMoreLocationException();
        }

        LocationConfig location = optionalLocation.get();
        conf.setLocation(location);

        // Set user
        List<UserConfig> users = userConfigDataService.getUnusedUsers(usersPerInstance);
        if (users.size() == 0) {
            throw new NoMoreUsersException();
        }
        conf.setUsers(users);

        //Generate instance Id
        int id = generateUniqueInstanceId();

        // If tor is used
        if (useTor) {
            TorInstance torInstance = new TorInstance(id);
            torInstance.start();
            conf.setProxyPort(torInstance.getProxyPort());
            torInstances.add(torInstance);
        }

        //Set google maps key
        conf.setGoogleMapsKey("AIzaSyC4w7rMpg48S8u8eJBiEESCEc6cKj5iTyI");

        userConfigManagerService.forceUpdateLastUsedTimes(users);
        locationConfigManagerService.forceUpdateLastUsedTime(location);


        MapInstance mapInstance = usePgm ? new PGMInstance(this, conf, id) : new PokemonMapInstance(this,conf,id);

        mapInstance.start();
        mapInstances.add(mapInstance);

        return mapInstance;
    }

    /**
     * Get All data from all the instances
     *
     * @return the raw data
     */
    public HashSet<AllData> getNewAllData() {
        HashSet<AllData> result = new HashSet<>();
        mapInstances.stream()
                .map(MapInstance::getNewAllData)
                .forEach(result::add);

        return result;
    }

    /**
     * Stops an instance
     *
     * @param mapInstance the instance
     */
    private void stopInstance(MapInstance mapInstance) {

        log.info("Stopping instace: " + mapInstance.getInstanceName());

        // Stop tor
        if (useTor) {
            int instanceId = mapInstance.getInstanceId();
            Optional<TorInstance> torInstanceOptional = torInstances.stream()
                    .filter(ti -> ti.getTorId() == instanceId).findFirst();

            if (torInstanceOptional.isPresent()) {
                TorInstance torInstance = torInstanceOptional.get();
                torInstance.setStop(true);
                torInstances.remove(torInstance);
            }
        }

        // Stop the mapInstance
        mapInstance.stop();

        // Release users and locations
        userConfigManagerService.releaseUsers(mapInstance.getConfiguration().getUsers());
        locationConfigManagerService.releaseLocation(mapInstance.getConfiguration().getLocation());

        mapInstances.remove(mapInstance);
    }

    /**
     * When the application stops, stop everything
     */
    @Override
    public void stop() {
        running = false;

        log.info("Stopping instances...");
        mapInstances.forEach(MapInstance::stop);

        log.info("Stopping tor instances...");
        torInstances.forEach(torInstance -> torInstance.setStop(true));

        log.info("Releasing Users and Locations");

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
            log.warning("User " + userConfig.getUserName() + " was banned");
            userConfigDataService.setBanned(userConfig);
        }
    }

    private int generateUniqueInstanceId() {
        List<Integer> instanceIds = mapInstances.stream()
                .mapToInt(MapInstance::getInstanceId)
                .boxed()
                .collect(Collectors.toList());

        for (int i = 0; i < 1024; i++) {
            if (!instanceIds.contains(i)) {
                return i;
            }
        }

        throw new RuntimeException("More than 1024 instances are not supported");
    }
}
