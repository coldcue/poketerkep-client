package hu.poketerkep.json;

@SuppressWarnings("unused")
public class PokestopJsonDto {
    private Integer active_pokemon_id;
    private Boolean enabled;
    private Long last_modified;
    private Double latitude;
    private Double longitude;
    private Long lure_expiration;
    private String pokestop_id;

    public Integer getActive_pokemon_id() {
        return active_pokemon_id;
    }

    public void setActive_pokemon_id(Integer active_pokemon_id) {
        this.active_pokemon_id = active_pokemon_id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(Long last_modified) {
        this.last_modified = last_modified;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getLure_expiration() {
        return lure_expiration;
    }

    public void setLure_expiration(Long lure_expiration) {
        this.lure_expiration = lure_expiration;
    }

    public String getPokestop_id() {
        return pokestop_id;
    }

    public void setPokestop_id(String pokestop_id) {
        this.pokestop_id = pokestop_id;
    }
}
