package hu.poketerkep.client.model;


import hu.poketerkep.client.model.helpers.LastUsed;

@SuppressWarnings("unused")
public class LocationConfig implements LastUsed {
    private String locationId;
    private Double latitude;
    private Double longitude;
    private Integer steps;
    private Long lastUsed;


    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
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

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public Long getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Long lastUsed) {
        this.lastUsed = lastUsed;
    }

    @Override
    public String toString() {
        return "LocationConfig{" +
                "locationId='" + locationId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", steps=" + steps +
                ", lastUsed=" + lastUsed +
                '}';
    }
}
