package hu.poketerkep.client.service;

import hu.poketerkep.client.dataservice.LocationConfigDataService;
import hu.poketerkep.client.model.LocationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        locationConfigs.forEach(locationConfigDataService::updateLocationLastUsed);
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
}
