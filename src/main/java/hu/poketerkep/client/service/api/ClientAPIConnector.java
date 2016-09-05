package hu.poketerkep.client.service.api;

import hu.poketerkep.shared.api.ClientAPIEndpoint;
import hu.poketerkep.shared.geo.Coordinate;
import hu.poketerkep.shared.model.Pokemon;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class ClientAPIConnector implements ClientAPIEndpoint {
    private final RestTemplate restTemplate;
    private final String masterAPIEndpoint;

    @Autowired
    public ClientAPIConnector(RestTemplate restTemplate, String masterAPIEndpoint) {
        this.restTemplate = restTemplate;
        this.masterAPIEndpoint = masterAPIEndpoint;
    }

    @Override
    public ResponseEntity<Void> addPokemons(Pokemon[] pokemons) {
        return restTemplate.postForEntity(masterAPIEndpoint + "/client/addPokemons", pokemons, Void.class);
    }

    @Override
    public ResponseEntity<Coordinate[]> nextScanLocations(int limit) {
        try {

            URI uri = new URIBuilder(masterAPIEndpoint + "/client/nextScanLocations")
                    .addParameter("limit", String.valueOf(limit))
                    .build();

            ResponseEntity<Coordinate[]> forEntity = restTemplate.getForEntity(uri, Coordinate[].class);
            return forEntity;

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
