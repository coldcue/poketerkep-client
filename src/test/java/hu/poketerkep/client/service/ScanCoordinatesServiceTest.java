package hu.poketerkep.client.service;


import hu.poketerkep.shared.geo.Coordinate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ScanCoordinatesServiceTest {
    private Random random = new Random();

    @Test
    public void test() throws Exception {
        ClientService clientService = Mockito.mock(ClientService.class);
        List<Coordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            coordinates.add(Coordinate.fromDegrees(random.nextDouble(), random.nextDouble()));
        }

        //Next scan locations return 100 new coordinates
        Mockito.when(clientService.nextScanLocations(100)).thenReturn(Optional.of(coordinates));

        ScanCoordinatesService scanCoordinatesService = new ScanCoordinatesService(clientService);
        //Set scannerInstanceCount to 100
        Field scannerInstanceCount = ScanCoordinatesService.class.getDeclaredField("scannerInstanceCount");
        scannerInstanceCount.setAccessible(true);
        scannerInstanceCount.set(scanCoordinatesService, 100);


        ///////// It should work like a LIFO

        Coordinate expected = scanCoordinatesService.poll();
        scanCoordinatesService.push(expected);
        Coordinate result = scanCoordinatesService.poll();

        Assert.assertEquals(expected, result);
    }
}