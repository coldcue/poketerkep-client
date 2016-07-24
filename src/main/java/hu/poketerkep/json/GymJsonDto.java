package hu.poketerkep.json;


@SuppressWarnings("unused")
public class GymJsonDto {
    private Boolean enabled;
    private Integer guard_pokemon_id;
    private String gym_id;
    private Integer gym_points;
    private Long last_modified;
    private Double latitude;
    private Double longitude;
    private Integer team_id;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getGuard_pokemon_id() {
        return guard_pokemon_id;
    }

    public void setGuard_pokemon_id(Integer guard_pokemon_id) {
        this.guard_pokemon_id = guard_pokemon_id;
    }

    public String getGym_id() {
        return gym_id;
    }

    public void setGym_id(String gym_id) {
        this.gym_id = gym_id;
    }

    public Integer getGym_points() {
        return gym_points;
    }

    public void setGym_points(Integer gym_points) {
        this.gym_points = gym_points;
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

    public Integer getTeam_id() {
        return team_id;
    }

    public void setTeam_id(Integer team_id) {
        this.team_id = team_id;
    }
}
