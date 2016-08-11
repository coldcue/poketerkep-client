package hu.poketerkep.client.pokemonGoMap;

import hu.poketerkep.client.dataservice.LocationConfigDataService;
import hu.poketerkep.client.dataservice.UserConfigDataService;
import hu.poketerkep.client.exception.NoMoreLocationException;
import hu.poketerkep.client.exception.NoMoreUsersException;
import hu.poketerkep.client.model.AllData;
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
    private HashSet<PGMInstance> pgmInstances = new HashSet<>();
    private HashSet<TorInstance> torInstances = new HashSet<>();
    @Value("${instance-count:3}")
    private int instanceCount;
    @Value("${use-tor:false}")
    private boolean useTor;
    @Value("${users-per-instace:15}")
    private int usersPerInstance;

    @Autowired
    public MapManager(LocationConfigDataService locationConfigDataService, UserConfigDataService userConfigDataService, UserConfigManagerService userConfigManagerService, LocationConfigManagerService locationConfigManagerService) {
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
            int emptySlots = instanceCount - pgmInstances.size();
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
            new ArrayList<>(pgmInstances).stream()
                    .filter(pgmInstance -> pgmInstance.getHealthAnalyzer().isShouldBeStopped())
                    .forEach(this::stopInstance);

        }
    }


    /**
     * Get the UserConfigs from all the instances
     *
     * @return List of UserConfigs
     */
    private List<UserConfig> getUserConfigs() {
        return pgmInstances.stream()
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
        return pgmInstances.stream()
                .map(PGMInstance::getConf)
                .map(PGMConfiguration::getLocation)
                .collect(Collectors.toList());
    }

    /**
     * Create a PokemonGo Map Instance
     *
     * @return the instance itself
     */
    private PGMInstance createInstance() throws NoMoreUsersException, NoMoreLocationException, IOException {
        PGMConfiguration conf = new PGMConfiguration();

        // Set location
        LocationConfig location = locationConfigManagerService.getUnusedLocation(new ArrayList<>(getLocationConfigs()));
        if (location == null) {
            throw new NoMoreLocationException();
        }
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
            conf.setProxyPort(Optional.of(torInstance.getProxyPort()));
            torInstances.add(torInstance);
        }

        //Set google maps key
        conf.setGoogleMapsKey("AIzaSyC4w7rMpg48S8u8eJBiEESCEc6cKj5iTyI");

        userConfigManagerService.updateLastUsedTimes(users);
        locationConfigManagerService.updateLastUsedTimes(location);

        PGMInstance pgmInstance = new PGMInstance(this, conf, id);

        pgmInstance.start();
        pgmInstances.add(pgmInstance);

        return pgmInstance;
    }

    /**
     * Get All data from all the instances
     *
     * @return the raw data
     */
    public List<AllData> getNewAllDataList() {
        return pgmInstances.stream()
                .map(PGMInstance::getNewAllData)
                .collect(Collectors.toList());
    }

    /**
     * Stops an instance
     *
     * @param pgmInstance the instance
     */
    private void stopInstance(PGMInstance pgmInstance) {

        log.info("Stopping instace: " + pgmInstance.getInstanceName());

        // Stop tor
        if (useTor) {
            int instanceId = pgmInstance.getInstanceId();
            Optional<TorInstance> torInstanceOptional = torInstances.stream()
                    .filter(ti -> ti.getTorId() == instanceId).findFirst();

            if (torInstanceOptional.isPresent()) {
                TorInstance torInstance = torInstanceOptional.get();
                torInstance.setStop(true);
                torInstances.remove(torInstance);
            }
        }

        // Stop the pgmInstance
        pgmInstance.stop();

        // Release users and locations
        userConfigManagerService.releaseUsers(pgmInstance.getConf().getUsers());
        locationConfigManagerService.releaseLocation(pgmInstance.getConf().getLocation());

        pgmInstances.remove(pgmInstance);
    }

    /**
     * When the application stops, stop everything
     */
    @Override
    public void stop() {
        running = false;

        log.info("Stopping instances...");
        pgmInstances.forEach(PGMInstance::stop);

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
        List<Integer> instanceIds = pgmInstances.stream()
                .mapToInt(PGMInstance::getInstanceId)
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
