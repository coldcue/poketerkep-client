package hu.poketerkep.client.map.java.geo;

import hu.poketerkep.client.model.LocationConfig;

import java.util.ArrayList;
import java.util.List;

public class LocationGenerator {
    private static final double NORTH = 0;
    private static final double EAST = 90;
    private static final double SOUTH = 180;
    private static final double WEST = 270;

    private static final double PULSE_RADIUS = 0.7; // km

    private static final double X_DIST = Math.sqrt(3) * PULSE_RADIUS;
    private static final double Y_DIST = 3 * (PULSE_RADIUS / 2);

    private final Coordinate initialLocation;
    private final int stepCount;

    public LocationGenerator(Coordinate initialLocation, int stepCount) {
        this.initialLocation = initialLocation;
        this.stepCount = stepCount;
    }

    public LocationGenerator(LocationConfig locationConfig) {
        this.initialLocation = Coordinate.fromLocationConfig(locationConfig);
        this.stepCount = locationConfig.getSteps();
    }

    public List<Coordinate> generateSteps() {
        ArrayList<Coordinate> locations = new ArrayList<>();

        Coordinate loc = initialLocation;
        locations.add(loc); // Add initial location

        for (int ring = 0; ring < stepCount; ring++) {
            loc = loc.getNew(Y_DIST, NORTH).getNew(X_DIST / 2, WEST);

            for (int dir = 0; dir < 6; dir++) {
                for (Direction direction : Direction.values()) {
                    switch (direction) {
                        case RIGTH:
                            loc = loc.getNew(X_DIST, EAST);
                            break;
                        case DOWN_RIGHT:
                            loc = loc.getNew(Y_DIST, SOUTH).getNew(X_DIST / 2, EAST);
                            break;
                        case DOWN_LEFT:
                            loc = loc.getNew(Y_DIST, SOUTH).getNew(X_DIST / 2, WEST);
                            break;
                        case LEFT:
                            loc = loc.getNew(X_DIST, WEST);
                            break;
                        case UP_LEFT:
                            loc = loc.getNew(Y_DIST, NORTH).getNew(X_DIST / 2, WEST);
                            break;
                        case UP_RIGHT:
                            loc = loc.getNew(Y_DIST, NORTH).getNew(X_DIST / 2, EAST);
                            break;
                    }

                    // Add location
                    locations.add(loc);
                }
            }
        }

        return locations;
    }

}
