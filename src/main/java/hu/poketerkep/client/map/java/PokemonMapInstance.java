package hu.poketerkep.client.map.java;

import hu.poketerkep.client.map.MapConfiguration;
import hu.poketerkep.client.map.MapInstance;
import hu.poketerkep.client.map.MapManager;
import hu.poketerkep.client.map.java.geo.Coordinate;
import hu.poketerkep.client.map.java.geo.LocationGenerator;
import hu.poketerkep.client.model.AllData;
import hu.poketerkep.client.model.Pokemon;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class PokemonMapInstance implements MapInstance {
    private final MapManager mapManager;
    private final MapConfiguration conf;
    private final int id;
    private final List<Coordinate> coordinates;
    private final String instanceName;
    private final Set<Pokemon> pokemons;

    public PokemonMapInstance(MapManager mapManager, MapConfiguration conf, int id) {
        this.mapManager = mapManager;
        this.conf = conf;
        this.id = id;


        coordinates = new LocationGenerator(
                Coordinate.fromDegrees(
                        conf.getLocation().getLatitude(),
                        conf.getLocation().getLongitude()
                ),
                conf.getLocation().getSteps())
                .generateSteps();

        instanceName = "MapInstance-" + conf.getLocation().getLocationId();
        pokemons = Collections.newSetFromMap(new ConcurrentHashMap<Pokemon, Boolean>());
    }

    @Override
    public void check() {
        //TODO delete old pokemons

    }

    @Override
    public boolean isShouldBeStopped() {
        return false;
    }

    @Override
    public MapConfiguration getConfiguration() {
        return conf;
    }

    @Override
    public String getInstanceName() {
        return instanceName;
    }

    @Override
    public int getInstanceId() {
        return id;
    }

    @Override
    public void start() throws IOException {

    }

    @Override
    public void stop() {

    }

    @Override
    public AllData getNewAllData() {
        return null;
    }

    public void addPokemon(Pokemon pokemon) {
        pokemons.add(pokemon);
    }
}
