package hu.poketerkep.client.service;

import hu.poketerkep.client.dataservice.UserConfigDataService;
import hu.poketerkep.client.model.UserConfig;
import hu.poketerkep.client.model.helpers.LastUsedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserConfigManagerService {

    private final UserConfigDataService userConfigDataService;

    @Autowired
    public UserConfigManagerService(UserConfigDataService userConfigDataService) {
        this.userConfigDataService = userConfigDataService;
    }

    /**
     * Update the last used times for the User Configs
     *
     * @param userConfigs the list of the users you want to update
     */
    public void updateLastUsedTimes(List<UserConfig> userConfigs) {
        // Go back  seconds and compare it with the last used value
        long time = Instant.now().toEpochMilli();

        userConfigs.stream()
                .filter(uc -> LastUsedUtils.isBefore(uc, time)) // Just update the outdated ones
                .forEach(userConfigDataService::updateLastUsed); // Update database

    }

    /**
     * Release the users (set the lastUsed field to 0)
     *
     * @param userConfigs users to release
     */
    public void releaseUsers(List<UserConfig> userConfigs) {
        userConfigs.stream()
                .map(UserConfig::getUserName)
                .forEach(userConfigDataService::releaseUser);
    }

}
