package hu.poketerkep.client.service;

import hu.poketerkep.client.service.api.UserAPIConnector;
import hu.poketerkep.shared.model.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserService {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final UserAPIConnector userAPIConnector;

    @Autowired
    public UserService(UserAPIConnector userAPIConnector) {
        this.userAPIConnector = userAPIConnector;
    }

    public Optional<UserConfig> nextUser() {
        try {
            ResponseEntity<UserConfig> userConfigResponseEntity = userAPIConnector.nextUser();

            if (userConfigResponseEntity.getStatusCode() == HttpStatus.OK) {
                return Optional.of(userConfigResponseEntity.getBody());
            }
        } catch (Exception e) {
            log.warning("Cannot get next user: " + e.getMessage());
        }

        return Optional.empty();
    }

    public void banUser(UserConfig userConfig) {
        try {
            userAPIConnector.banUser(userConfig);
        } catch (Exception e) {

        }
    }
}
