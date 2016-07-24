package hu.poketerkep.pokemonGoMap;

/**
 * This configuration stores the values for the PokemonGoMap
 */
@SuppressWarnings("unused")
public class PokemonGoMapConfiguration {
    private String username;
    private String password;
    private int steps;
    private String googleMapsKey;
    private String location;

    public PokemonGoMapConfiguration(String username, String password, int steps, String googleMapsKey, String location) {
        this.username = username;
        this.password = password;
        this.steps = steps;
        this.googleMapsKey = googleMapsKey;
        this.location = location;
    }

    public String getGoogleMapsKey() {
        return googleMapsKey;
    }

    public void setGoogleMapsKey(String googleMapsKey) {
        this.googleMapsKey = googleMapsKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
