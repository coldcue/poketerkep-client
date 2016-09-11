package hu.poketerkep.client.service;

import hu.poketerkep.shared.geo.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

@Service
public class ScanCoordinatesService {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final ClientService clientService;
    private Deque<Coordinate> coordinates = new LinkedBlockingDeque<>();

    @Value("${scanner-instances}")
    private int scannerInstanceCount;

    @Autowired
    public ScanCoordinatesService(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Refill the coordinates queue
     */
    private synchronized void refillQueue() {
        if (coordinates.size() == 0) {
            log.warning("Getting new coordinates from master...");
            int limit = this.scannerInstanceCount > 1000 ? 1000 : this.scannerInstanceCount;
            Optional<Collection<Coordinate>> nextScanLocations = clientService.nextScanLocations(limit);
            if (!nextScanLocations.isPresent()) {
                log.warning("Cannot get new coordinates");
            } else {
                coordinates.addAll(nextScanLocations.get());
            }
        }

    }

    /**
     * Get a coordinate
     *
     * @return a coordinate
     */
    public Coordinate poll() {
        while (coordinates.size() == 0) {
            refillQueue();
        }
        return coordinates.poll();
    }

    /**
     * Push the coordinate back head of the queue
     *
     * @param coordinate the coordinate
     */
    public void push(Coordinate coordinate) {
        coordinates.push(coordinate);
    }
}
