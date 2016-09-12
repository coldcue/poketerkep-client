package hu.poketerkep.client.service;

import hu.poketerkep.shared.api.ClientAPIEndpoint;
import hu.poketerkep.shared.geo.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class ClientService {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final ClientAPIEndpoint clientAPIEndpoint;

    @Autowired
    public ClientService(ClientAPIEndpoint clientAPIEndpoint) {
        this.clientAPIEndpoint = clientAPIEndpoint;
    }

    public Optional<Collection<Coordinate>> nextScanLocations(int limit) {
        try {
            return Optional.of(Arrays.asList(clientAPIEndpoint.nextScanLocations(limit).getBody()));
        } catch (Exception e) {
            log.warning("Cannot get next scan location");
        }
        return Optional.empty();
    }
}
