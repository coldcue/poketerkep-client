package hu.poketerkep.client.model;

@SuppressWarnings("unused")
public class Pokemon {
    private String encounterId;
    private Long disappearTime;
    private Double latitude;
    private Double longitude;
    private Integer pokemonId;
    private String spawnpointId;

    public String getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public Long getDisappearTime() {
        return disappearTime;
    }

    public void setDisappearTime(Long disappearTime) {
        this.disappearTime = disappearTime;
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

    public Integer getPokemonId() {
        return pokemonId;
    }

    public void setPokemonId(Integer pokemonId) {
        this.pokemonId = pokemonId;
    }

    public String getSpawnpointId() {
        return spawnpointId;
    }

    public void setSpawnpointId(String spawnpointId) {
        this.spawnpointId = spawnpointId;
    }
}
