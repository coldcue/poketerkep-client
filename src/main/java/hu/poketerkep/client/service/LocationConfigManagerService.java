package hu.poketerkep.client.service;

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
        long time = Instant.now().toEpochMilli();

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

    public LocationConfig getUnusedLocation(List<LocationConfig> usedLocations) {
        List<LocationConfig> unusedLocations = locationConfigDataService.getUnusedLocations();

        // Only return location that is not locally used
        for (LocationConfig unusedFromDb : unusedLocations) {

            boolean found = false;

            for (LocationConfig used : usedLocations) {
                String unusedFromDbLocationId = unusedFromDb.getLocationId();
                String usedLocationId = used.getLocationId();

                if (unusedFromDbLocationId.equals(usedLocationId)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return unusedFromDb;
            }
        }

        return null;
    }
}
