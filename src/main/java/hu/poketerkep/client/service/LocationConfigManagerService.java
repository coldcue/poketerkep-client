package hu.poketerkep.client.service;

import hu.poketerkep.client.config.Constants;
import hu.poketerkep.client.dataservice.LocationConfigDataService;
import hu.poketerkep.client.model.LocationConfig;
import hu.poketerkep.client.model.helpers.LastUsedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class LocationConfigManagerService {
    private final LocationConfigDataService locationConfigDataService;

    @Autowired
    public LocationConfigManagerService(LocationConfigDataService locationConfigDataService) {
        this.locationConfigDataService = locationConfigDataService;
    }

    /**
     * Update last used times for locations
     *
     * @param locationConfigs list of {@link LocationConfig}
     */
    public void updateLastUsedTimes(List<LocationConfig> locationConfigs) {
        // Go back  seconds and compare it with the last used value
        long time = Instant.now().minusSeconds(Constants.UNUSED_LOCATION_UPDATE_TIME).toEpochMilli();

        locationConfigs.stream()
                .filter(lc -> LastUsedUtils.isBefore(lc, time)) // Just update the outdated ones
                .forEach(locationConfigDataService::updateLocationLastUsed); // Update database
    }

    /**
     * Release locations
     *
     * @param locationConfigs list of {@link LocationConfig}
     */
    public void releaseLocations(List<LocationConfig> locationConfigs) {
        locationConfigs.forEach(locationConfigDataService::releaseLocation);
    }

    /**
     * Release one location
     *
     * @param location a {@link LocationConfig}
     */
    public void updateLastUsedTimes(LocationConfig location) {
        locationConfigDataService.updateLocationLastUsed(location);
    }

    public void releaseLocation(LocationConfig location) {
        locationConfigDataService.releaseLocation(location);
    }
}
