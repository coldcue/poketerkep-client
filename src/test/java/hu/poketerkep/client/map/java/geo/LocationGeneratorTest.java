package hu.poketerkep.client.map.java.geo;

import org.junit.Test;

import java.util.List;


public class LocationGeneratorTest {
    @Test
    public void generateSteps() throws Exception {

        LocationGenerator locationGenerator = new LocationGenerator(Coordinate.fromDegrees(47.497912, 19.040234), 2);

        List<Coordinate> coordinates = locationGenerator.generateSteps();

        System.out.println(coordinates);

    }

}