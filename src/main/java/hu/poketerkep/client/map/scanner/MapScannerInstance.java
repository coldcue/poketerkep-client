package hu.poketerkep.client.map.scanner;

import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import hu.poketerkep.client.config.Constants;
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
            while (!isShutdown.get()) {
                Optional<UserConfig> userConfig = mapManager.getUserService().nextUser();
                Proxy proxy = mapManager.getProxy();

                log.info("Using proxy: " + proxy);

                // Check if are there enough users

                if (!userConfig.isPresent()) {
                    log.warning("No more users!");
                    Thread.sleep(Constants.LOGIN_DELAY);
                    continue;
                }

                log.info("Using user: " + userConfig);

                // Create a worker
                MapScannerWorker mapScannerWorker = new MapScannerWorker(userConfig.get(), proxy, mapManager.getClientService(), log);

                try {
                    log.info("Connecting to Niantic...");
                    mapScannerWorker.connect();

                    Thread.sleep(Constants.SCAN_DELAY);

                    // Scan locations
                    while (!isShutdown.get()) {
                        Optional<Coordinate> coordinateOptional = mapManager.getClientService().nextScanLocation();

                        if (!coordinateOptional.isPresent()) {
                            break;
                        }

                        log.info("Scanning location: " + coordinateOptional.get());

                        try {
                            mapScannerWorker.scan(coordinateOptional.get());
                        } catch (Exception e) {
                            log.warning("Scanning was unsuccessful: " + e.getMessage());
                        }


                        Thread.sleep(Constants.SCAN_DELAY);
                    }

                } catch (LoginFailedException e) {
                    log.warning("Cannot log in with user :" + userConfig + ", it is banned");
                    mapManager.getUserService().banUser(userConfig.get());

                } catch (RemoteServerException e) {
                    log.info("Server is busy");
                } catch (Exception e) {
                    log.warning("Something bad happened: " + e.getMessage());
                }

                Thread.sleep(Constants.LOGIN_DELAY);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        isShutdown.set(true);
    }
}
