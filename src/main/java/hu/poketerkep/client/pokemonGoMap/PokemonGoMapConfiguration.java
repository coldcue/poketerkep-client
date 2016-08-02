package hu.poketerkep.client.pokemonGoMap;

import hu.poketerkep.client.model.LocationConfig;
import hu.poketerkep.client.model.UserConfig;

import java.util.Optional;

/**
 * This configuration stores the values for the PokemonGoMap
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class PokemonGoMapConfiguration {
    private UserConfig user;
    private LocationConfig location;
    private String googleMapsKey;
    private int threads;
    private Optional<Integer> proxyPort;

    public PokemonGoMapConfiguration() {

    }

    public PokemonGoMapConfiguration(UserConfig user, LocationConfig location, String googleMapsKey) {
        this.user = user;
        this.location = location;
        this.googleMapsKey = googleMapsKey;
    }

    public UserConfig getUser() {
        return user;
    }

    public void setUser(UserConfig user) {
        this.user = user;
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

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public Optional<Integer> getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Optional<Integer> proxyPort) {
        this.proxyPort = proxyPort;
    }
}
