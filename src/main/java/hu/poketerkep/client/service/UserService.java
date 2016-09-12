package hu.poketerkep.client.service;

import hu.poketerkep.shared.api.UserAPIEndpoint;
import hu.poketerkep.shared.model.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

@Service
public class UserService {
    private static final int limit = 50;
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final UserAPIEndpoint userAPIEndpoint;
    private final Queue<UserConfig> userConfigs = new ConcurrentLinkedQueue<>();
    private final Semaphore refillPermit = new Semaphore(1);

    @Autowired
    public UserService(UserAPIEndpoint userAPIEndpoint) {
        this.userAPIEndpoint = userAPIEndpoint;
    }

    public Optional<UserConfig> nextUser() {
        if (userConfigs.isEmpty()) {
            refill();
        }

        return Optional.ofNullable(userConfigs.poll());
    }

    private void refill() {
        if (refillPermit.tryAcquire()) {
            try {
                ResponseEntity<UserConfig[]> responseEntity = userAPIEndpoint.nextUser(limit);
                UserConfig[] body = responseEntity.getBody();

                userConfigs.addAll(Arrays.asList(body));
            } catch (Exception e) {
                log.severe("UserConfig Queue cannot be filled: " + e.getMessage());
            }


            refillPermit.release();

            log.info("UserConfig Queue filled! userConfigs: " + userConfigs.size());
        } else {
            try {
                refillPermit.acquire();
                refillPermit.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void banUser(UserConfig userConfig) {
        try {
            userAPIEndpoint.banUser(userConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
