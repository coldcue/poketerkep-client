package hu.poketerkep.client.map;

import hu.poketerkep.client.model.LocationConfig;
import hu.poketerkep.client.model.UserConfig;

import java.util.List;
import java.util.Optional;

/**
 * This configuration stores the values for the PokemonGoMap
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MapConfiguration {
    private List<UserConfig> users;
    private LocationConfig location;
    private String googleMapsKey;
    private Integer proxyPort;

    public MapConfiguration() {

    }

    public MapConfiguration(List<UserConfig> users, LocationConfig location, String googleMapsKey) {
        this.users = users;
        this.location = location;
        this.googleMapsKey = googleMapsKey;
    }

    public List<UserConfig> getUsers() {
        return users;
    }

    public void setUsers(List<UserConfig> users) {
        this.users = users;
    }

    public LocationConfig getLocation() {
        return location;
    }

    public void setLocation(LocationConfig location) {
        this.location = location;
    }

    public String getGoogleMapsKey() {
        return googleMapsKey;
    }

    public void setGoogleMapsKey(String googleMapsKey) {
        this.googleMapsKey = googleMapsKey;
    }

    public Optional<Integer> getProxyPort() {
        return Optional.of(proxyPort);
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }
}
