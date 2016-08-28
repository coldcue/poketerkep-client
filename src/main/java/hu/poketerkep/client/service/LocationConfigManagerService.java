package hu.poketerkep.client.service;

import hu.poketerkep.client.config.Constants;
import hu.poketerkep.client.dataservice.LocationConfigDataService;
import hu.poketerkep.shared.model.LocationConfig;
import hu.poketerkep.shared.model.helpers.LastUsedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
        long time = Instant.now().minusSeconds(Constants.UNUSED_LOCATION_TIME_SECONDS - 30).toEpochMilli();

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
    public void forceUpdateLastUsedTime(LocationConfig location) {
        locationConfigDataService.updateLocationLastUsed(location);
    }

    public void releaseLocation(LocationConfig location) {
        locationConfigDataService.releaseLocation(location);
    }

    public Optional<LocationConfig> getUnusedLocation(List<LocationConfig> usedLocations) {
        Optional<List<LocationConfig>> unusedLocations = locationConfigDataService.getUnusedLocations();

        if (usedLocations == null) {
            if (unusedLocations.isPresent()) {
                return unusedLocations.get().stream().findFirst();
            }
        }

        if (!unusedLocations.isPresent()) {
            return Optional.empty();
        }

        // Only return location that is not locally used
        for (LocationConfig unusedFromDb : unusedLocations.get()) {
            boolean found = false;

            assert usedLocations != null;
            for (LocationConfig used : usedLocations) {
                String unusedFromDbLocationId = unusedFromDb.getLocationId();
                String usedLocationId = used.getLocationId();

                if (unusedFromDbLocationId.equals(usedLocationId)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return Optional.of(unusedFromDb);
            }
        }

        return Optional.empty();
    }
}
