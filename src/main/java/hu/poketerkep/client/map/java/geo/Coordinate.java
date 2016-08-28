package hu.poketerkep.client.map.java.geo;

import hu.poketerkep.client.model.LocationConfig;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Coordinate {
    private static final double R = 6378.1;
    private double latitude;
    private double longitude;

    private Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Coordinate fromDegrees(double latitudeDegrees, double longitudeDegrees) {
        return new Coordinate(latitudeDegrees, longitudeDegrees);
    }

    private static Coordinate fromRadians(double latitudeRadians, double longitudeRadians) {
        return new Coordinate(Math.toDegrees(latitudeRadians), Math.toDegrees(longitudeRadians));
    }

    public static Coordinate fromLocationConfig(LocationConfig locationConfig) {
        return new Coordinate(locationConfig.getLatitude(), locationConfig.getLongitude());
    }

    /**
     * Get a new Coordinate which in a distance and bearing
     *
     * @param distance      distance
     * @param bearingDegree bearing
     * @return a new coordinate
     */
    public Coordinate getNew(double distance, double bearingDegree) {
        final double bearing = Math.toRadians(bearingDegree);

        double newLatitude = Math.asin(
                Math.sin(this.getLatitudeRadians()) * Math.cos(distance / R) +
                        Math.cos(this.getLatitudeRadians()) * Math.sin(distance / R) * Math.cos(bearing));

        double newLongitude = this.getLongitudeRadians() + Math.atan2(
                Math.sin(bearing) * Math.sin(distance / R) * Math.cos(this.getLatitudeRadians()),
                Math.cos(distance / R) - Math.sin(this.getLatitudeRadians()) * Math.sin(newLatitude));

        return Coordinate.fromRadians(newLatitude, newLongitude);
    }


    private double getLatitudeRadians() {
        return Math.toRadians(latitude);
    }

    private double getLongitudeRadians() {
        return Math.toRadians(longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("0.0########", DecimalFormatSymbols.getInstance());
        return "[" + decimalFormat.format(latitude) + "," + decimalFormat.format(longitude) + "]";
    }
}
