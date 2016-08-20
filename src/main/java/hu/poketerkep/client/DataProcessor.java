package hu.poketerkep.client;

import hu.poketerkep.client.dataservice.PokemonDataService;
import hu.poketerkep.client.model.AllData;
import hu.poketerkep.client.model.Pokemon;
import hu.poketerkep.client.map.MapManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * This extracts data and
 */
@Component
public class DataProcessor {
    private final PokemonDataService pokemonDataService;
    private final MapManager instanceManager;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    public DataProcessor(PokemonDataService pokemonDataService, MapManager instanceManager) {
        this.pokemonDataService = pokemonDataService;
        this.instanceManager = instanceManager;
    }

    @Scheduled(fixedDelay = 5 * 1000, initialDelay = 10 * 1000)
    public void processData() {
        logger.info("Fetching data...");
        HashSet<AllData> allDataList = instanceManager.getNewAllData();

        logger.info("Processing data...");

        // Get pokemons
        HashSet<Pokemon> pokemons = new HashSet<>();
        allDataList.stream()
                .filter(allData -> allData.getPokemons() != null) // Null check
                .map(AllData::getPokemons) // Get pokemons from All data
                .flatMap(Collection::stream)
                .forEach(pokemons::add);


        if (pokemons.size() > 0) {
            logger.info("Saving " + pokemons.size() + " new pokemons");
            pokemonDataService.putPokemons(new ArrayList<>(pokemons));
        } else {
            logger.info("No pokemons to save.");
        }

        logger.info("Done");
    }
}
