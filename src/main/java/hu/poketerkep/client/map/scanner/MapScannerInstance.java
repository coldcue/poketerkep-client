package hu.poketerkep.client.map.scanner;

import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import hu.poketerkep.client.config.LocalConstants;
import hu.poketerkep.client.map.MapManager;
import hu.poketerkep.client.service.ClientService;
import hu.poketerkep.client.service.ScanCoordinatesService;
import hu.poketerkep.shared.geo.Coordinate;
import hu.poketerkep.shared.model.UserConfig;

import java.net.Proxy;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * This runs a MapScannerWorker and asks for new users and locations
 */
public class MapScannerInstance extends Thread {
    private final Logger log;
    private final int instanceId;
    private final MapManager mapManager;

    //Services
    private final ScanCoordinatesService scanCoordinatesService;
    private final ClientService clientService;

    //Scanner
    private MapScannerWorker mapScannerWorker;
    private UserConfig userConfig;
    private Instant usingUserSince;
    private MapScannerHealth mapScannerHealth;

    //Flags
    private boolean isLoggedIn = false;
    private boolean nextUserNeeded = true;
    private AtomicBoolean isShutdown = new AtomicBoolean(false);

    public MapScannerInstance(int instanceId, MapManager mapManager) {
        this.instanceId = instanceId;
        this.mapManager = mapManager;

        log = Logger.getLogger("MSI-" + instanceId);

        scanCoordinatesService = mapManager.getScanCoordinatesService();
        clientService = mapManager.getClientService();

        mapScannerHealth = new MapScannerHealth(this);
    }

    public int getInstanceId() {
        return instanceId;
    }

    @Override
    public void run() {
        try {
            if (!isLoggedIn) {
                Proxy proxy = mapManager.getProxy();
                log.fine("Using proxy: " + proxy);

                // If next user is needed
                if (nextUserNeeded) {
                    if (!getNextUser()) return;
                }

                // Create a worker
                mapScannerWorker = new MapScannerWorker(userConfig, mapManager.getMapCacheService(), log, proxy);
                mapScannerWorker.connect();

                // Set logged in flag
                isLoggedIn = true;

            } else {
                // Scan location
                Coordinate coordinate = scanCoordinatesService.poll();

                log.info("Scanning location: " + coordinate);

                try {
                    mapScannerWorker.scan(coordinate);
                    mapScannerHealth.onSuccess();
                } catch (Exception e) {
                    //Put back the coordinate if it was unsuccessful
                    scanCoordinatesService.push(coordinate);
                    mapScannerHealth.onError();
                    log.warning("Scanning was unsuccessful: " + e.getMessage());
                }
            }

        } catch (LoginFailedException e) {
            log.warning("Cannot log in with user :" + userConfig + ", it is banned");
            //mapManager.getUserService().banUser(userConfig); // do not ban, because it is not banned
            isLoggedIn = false;
            nextUserNeeded = true;
        } catch (RemoteServerException e) {
            log.info("Server is busy");
        } catch (Exception e) {
            log.warning("Something bad happened: " + e.getMessage());
            mapScannerHealth.onError();
        }

        checkUserExpire();
    }

    /**
     * Check if a user is used for a given amount of time, this is because of user over usage
     */
    private void checkUserExpire() {
        Instant instant = usingUserSince.plus(Duration.ofSeconds(LocalConstants.MAX_USED_USER_TIME_SECONDS));

        if (isLoggedIn && !nextUserNeeded && usingUserSince != null && Instant.now().isAfter(instant)) {
            log.info("User was expired: " + userConfig);
            nextUserNeeded = true;
        }
    }

    private boolean getNextUser() {
        Optional<UserConfig> userConfigOptional = mapManager.getUserService().nextUser();

        // Check if are there enough users

        if (!userConfigOptional.isPresent()) {
            log.warning("No more users!");
            return false;
        }

        userConfig = userConfigOptional.get();
        usingUserSince = Instant.now();

        //Reset flag
        nextUserNeeded = false;

        //Reset health
        mapScannerHealth = new MapScannerHealth(this);

        return true;
    }

    public void shutdown() {
        isShutdown.set(true);
    }

    public void setNextUserNeeded(boolean nextUserNeeded) {
        this.nextUserNeeded = nextUserNeeded;
    }
}
