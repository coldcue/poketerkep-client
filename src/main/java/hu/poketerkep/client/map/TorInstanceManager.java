package hu.poketerkep.client.map;


import hu.poketerkep.client.tor.TorInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TorInstanceManager implements SmartLifecycle {
    // The running state of the Instance Manager
    private boolean running;
    private Set<TorInstance> instances = ConcurrentHashMap.newKeySet();

    @Value("${tor-instance-count:1}")
    private int torInstanceCount;
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
    }

    @Override
    public void stop() {
        running = false;
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
