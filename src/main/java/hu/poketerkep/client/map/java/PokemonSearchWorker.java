package hu.poketerkep.client.map.java;

import hu.poketerkep.client.map.java.geo.Coordinate;

/**
 * Pokemon Search Thread
 */
public class PokemonSearchWorker {
    private final int proxyPort;
    private final PokemonMapInstance mapInstance;

    public PokemonSearchWorker(int proxyPort, PokemonMapInstance mapInstance) {
        this.proxyPort = proxyPort;
        this.mapInstance = mapInstance;


    }

    public SearchResult search(Coordinate location) {

    }
}
