package hu.poketerkep.client.map;


import hu.poketerkep.client.config.LocalConstants;
import hu.poketerkep.client.tor.TorInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class TorInstanceManager implements SmartLifecycle {
    // The running state of the Instance Manager
    private boolean running;
    private Set<TorInstance> instances = ConcurrentHashMap.newKeySet();
    private Random random = new Random();

    @Value("${scanner-instances}")
    private int scannerInstanceCount;
    @Value("${use-tor:false}")
    private boolean useTor;

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
    public void start() {
        if (useTor) {

            // 10 user / tor
            int torInstanceCount = scannerInstanceCount / LocalConstants.CLIENTS_PER_TOR;
            if (torInstanceCount == 0) torInstanceCount = 1;

            for (int i = 0; i < torInstanceCount; i++) {
                TorInstance torInstance = new TorInstance(i);
                instances.add(torInstance);
                torInstance.start();
            }

            running = true;

        }
    }

    @Override
    public void stop() {
        instances.forEach(TorInstance::interrupt);
        running = false;
    }

    public Optional<TorInstance> getRandomTorInstance() {

        int size = instances.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for (TorInstance torInstance : instances) {
            if (i == item)
                return Optional.of(torInstance);
            i = i + 1;
        }

        return Optional.empty();
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
        List<Long> instanceIds = instances.stream()
                .mapToLong(TorInstance::getId)
                .boxed()
                .collect(Collectors.toList());

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!instanceIds.contains(i)) {
                return i;
            }
        }

        throw new RuntimeException("More than " + Long.MAX_VALUE + " instances are not supported");
    }
}
