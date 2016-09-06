package hu.poketerkep.client.map.scanner;

import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import hu.poketerkep.client.map.MapManager;
import hu.poketerkep.shared.geo.Coordinate;
import hu.poketerkep.shared.model.UserConfig;

import java.net.Proxy;
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
    private AtomicBoolean isShutdown = new AtomicBoolean(false);
    private boolean isLoggedIn = false;
    private boolean nextUserNeeded = true;
    private MapScannerWorker mapScannerWorker;
    private UserConfig userConfig;

    public MapScannerInstance(int instanceId, MapManager mapManager) {
        this.instanceId = instanceId;
        this.mapManager = mapManager;

        log = Logger.getLogger("MSI-" + instanceId);
    }

    public int getInstanceId() {
        return instanceId;
    }

    @Override
    public void run() {
        try {
            if (!isLoggedIn) {
                Proxy proxy = mapManager.getProxy();
                log.info("Using proxy: " + proxy);

                // If next user is needed
                if (nextUserNeeded) {
                    if (!getNextUser()) return;
                }

                // Create a worker
                mapScannerWorker = new MapScannerWorker(userConfig, proxy, mapManager.getClientService(), log);

                log.info("Connecting to Niantic...");
                mapScannerWorker.connect();

                // Set logged in flag
                isLoggedIn = true;

            } else {
                // Scan location
                Optional<Coordinate> coordinateOptional = mapManager.getClientService().nextScanLocation();

                if (!coordinateOptional.isPresent()) {
                    return;
                }

                log.info("Scanning location: " + coordinateOptional.get());

                try {
                    mapScannerWorker.scan(coordinateOptional.get());
                } catch (Exception e) {
                    log.warning("Scanning was unsuccessful: " + e.getMessage());
                }
            }

        } catch (LoginFailedException e) {
            log.warning("Cannot log in with user :" + userConfig + ", it is banned");
            mapManager.getUserService().banUser(userConfig);
            isLoggedIn = false;
            nextUserNeeded = true;
        } catch (RemoteServerException e) {
            log.info("Server is busy");
        } catch (Exception e) {
            log.warning("Something bad happened: " + e.getMessage());
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
        log.info("Using user: " + userConfig);

        //Reset flag
        nextUserNeeded = false;

        return true;
    }

    public void shutdown() {
        isShutdown.set(true);
    }
}
