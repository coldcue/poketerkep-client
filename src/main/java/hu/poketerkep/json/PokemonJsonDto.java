package hu.poketerkep.json;


@SuppressWarnings("unused")
public class PokemonJsonDto {
    private Long disappear_time;
    private String encounter_id;
    private Double latitude;
    private Double longitude;
    private Integer pokemon_id;
    private String pokemon_name;
    private String spawnpoint_id;

    public Long getDisappear_time() {
        return disappear_time;
    }

    public void setDisappear_time(Long disappear_time) {
        this.disappear_time = disappear_time;
    }

    public String getEncounter_id() {
        return encounter_id;
    }

    public void setEncounter_id(String encounter_id) {
        this.encounter_id = encounter_id;
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

    public Integer getPokemon_id() {
        return pokemon_id;
    }

    public void setPokemon_id(Integer pokemon_id) {
        this.pokemon_id = pokemon_id;
    }

    public String getPokemon_name() {
        return pokemon_name;
    }

    public void setPokemon_name(String pokemon_name) {
        this.pokemon_name = pokemon_name;
    }

    public String getSpawnpoint_id() {
        return spawnpoint_id;
    }

    public void setSpawnpoint_id(String spawnpoint_id) {
        this.spawnpoint_id = spawnpoint_id;
    }
}
