package hu.poketerkep.client;

import hu.poketerkep.client.dataservice.PokemonDataService;
import hu.poketerkep.client.model.AllData;
import hu.poketerkep.client.model.Pokemon;
import hu.poketerkep.client.pokemonGoMap.MapManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        List<AllData> allDataList = instanceManager.getNewAllDataList();

        logger.info("Processing data...");

        // Get pokemons
        List<Pokemon> pokemons = allDataList.stream()
                .filter(allData -> allData.getPokemons() != null) // Null check
                .map(AllData::getPokemons) // Get pokemons from All data
                .flatMap(Collection::stream)
                .collect(Collectors.toList());


        if (pokemons.size() > 0) {
            logger.info("Saving " + pokemons.size() + " new pokemons");
            pokemonDataService.putPokemons(new ArrayList<>(pokemons));
        } else {
            logger.info("No pokemons to save.");
        }

        logger.info("Done");
    }
}
