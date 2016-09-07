package hu.poketerkep.client.map;

import hu.poketerkep.client.map.scanner.MapScannerInstance;
import hu.poketerkep.client.service.ClientService;
import hu.poketerkep.client.service.UserService;
import hu.poketerkep.client.tor.TorInstance;
import hu.poketerkep.shared.config.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class MapManager implements SmartLifecycle {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    private final UserService userService;
    private final ClientService clientService;
    private final TorInstanceManager torInstanceManager;

    // The running state of the Instance Manager
    private boolean running;
    private Set<MapScannerInstance> instances = ConcurrentHashMap.newKeySet();
    private ScheduledExecutorService scheduledExecutorService;

    @Value("${scanner-instances:20}")
    private int scannerInstanceCount;
    @Value("${use-tor:false}")
    private boolean useTor;

    @Autowired
    public MapManager(UserService userService, ClientService clientService, TorInstanceManager torInstanceManager) {
        this.userService = userService;
        this.clientService = clientService;
        this.torInstanceManager = torInstanceManager;
    }

    @Override
    public void start() {

        // Wait while tor is running
        if (useTor) {
            log.info("Waiting for tor...");
            while (!torInstanceManager.isRunning()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // 10 user / scanner thread
        int scannerThreadsCount = scannerInstanceCount / 10;
        scheduledExecutorService = Executors.newScheduledThreadPool(scannerThreadsCount);
        for (int i = 0; i < scannerInstanceCount; i++) {
            createInstance();
        }
        running = true;
    }

    /**
     * Create a PokemonGo Map Instance
     */
    private void createInstance() {
        //Generate instance Id
        int id = generateUniqueInstanceId();

        MapScannerInstance instance = new MapScannerInstance(id, this);
        instances.add(instance);
        scheduledExecutorService.scheduleWithFixedDelay(instance, 0, Constants.SCAN_DELAY, TimeUnit.SECONDS);
    }


    /**
     * When the application stops, stop everything
     */
    @Override
    public void stop() {
        running = false;

        scheduledExecutorService.shutdown();

        log.info("Stopping instances...");
        instances.forEach(MapScannerInstance::shutdown);
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
     * Generate Unique Map Instance ID
     *
     * @return unique Map Instance ID
     */
    private int generateUniqueInstanceId() {
        List<Integer> instanceIds = instances.stream()
                .mapToInt(MapScannerInstance::getInstanceId)
                .boxed()
                .collect(Collectors.toList());

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!instanceIds.contains(i)) {
                return i;
            }
        }

        throw new RuntimeException("More than " + Integer.MAX_VALUE + " instances are not supported");
    }

    public ClientService getClientService() {
        return clientService;
    }

    public UserService getUserService() {
        return userService;
    }

    /**
     * Get a random proxy
     *
     * @return
     */
    public Proxy getProxy() {
        if (!useTor) {
            return Proxy.NO_PROXY;
        }

        Optional<TorInstance> optional = torInstanceManager.getRandomTorInstance();

        if (!optional.isPresent()) {
            return Proxy.NO_PROXY;
        } else {
            TorInstance torInstance = optional.get();
            return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", torInstance.getProxyPort()));
        }
    }
}
