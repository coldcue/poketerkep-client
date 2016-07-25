package hu.poketerkep.json;

@SuppressWarnings("unused")
public class ScannedJsonDto {
    private Long last_modified;
    private Double latitude;
    private Double longitude;
    private String scanned_id;

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

    public String getScanned_id() {
        return scanned_id;
    }

    public void setScanned_id(String scanned_id) {
        this.scanned_id = scanned_id;
    }
}
