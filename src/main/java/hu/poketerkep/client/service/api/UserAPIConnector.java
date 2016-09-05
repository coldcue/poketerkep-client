package hu.poketerkep.client.service.api;

import hu.poketerkep.shared.api.UserAPIEndpoint;
import hu.poketerkep.shared.model.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserAPIConnector implements UserAPIEndpoint {
    private final RestTemplate restTemplate;
    private final String masterAPIEndpoint;

    @Autowired
    public UserAPIConnector(RestTemplate restTemplate, String masterAPIEndpoint) {
        this.restTemplate = restTemplate;
        this.masterAPIEndpoint = masterAPIEndpoint;
    }

    @Override
    public ResponseEntity<UserConfig> nextUser() {
        return restTemplate.getForEntity(masterAPIEndpoint + "/user/nextUser", UserConfig.class);
    }

    @Override
    public ResponseEntity<UserConfig> banUser(UserConfig userConfig) {
        return restTemplate.postForEntity(masterAPIEndpoint + "/user/banUser", userConfig, UserConfig.class);
    }

    @Override
    public ResponseEntity<UserConfig> releaseUser(UserConfig userConfig) {
        return restTemplate.postForEntity(masterAPIEndpoint + "/user/releaseUser", userConfig, UserConfig.class);
    }

    @Override
    public ResponseEntity<UserConfig> addUser(UserConfig userConfig) {
        throw new UnsupportedOperationException();
    }
}
