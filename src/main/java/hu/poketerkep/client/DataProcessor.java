package hu.poketerkep.client;

import hu.poketerkep.client.json.RawDataJsonDto;
import hu.poketerkep.client.mapper.PokemonMapper;
import hu.poketerkep.client.model.Pokemon;
import hu.poketerkep.client.pokemonGoMap.PokemonGoMapDataService;
import hu.poketerkep.client.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This extracts data and
 */
@Component
public class DataProcessor {
    private final PokemonGoMapDataService dataService;
    private final DatabaseService databaseService;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private List<Pokemon> lastPokemons = new ArrayList<>();

    @Autowired
    public DataProcessor(PokemonGoMapDataService dataService, DatabaseService databaseService) {
        this.dataService = dataService;
        this.databaseService = databaseService;
    }

    @Scheduled(fixedDelay = 5 * 1000, initialDelay = 10 * 1000)
    public void processData() {
        logger.info("Fetching data...");
        RawDataJsonDto rawData = dataService.getRawData();

        logger.info("Data arrived: [pokemons: " + rawData.getPokemons().size() +
                ", pokestops: " + rawData.getPokestops().size() +
                ", gyms: " + rawData.getGyms().size() + "]");

        logger.info("Processing data...");
        List<Pokemon> pokemons = rawData.getPokemons().parallelStream()
                .map(PokemonMapper::mapFromJsonDto)
                .collect(Collectors.toList());

        List<Pokemon> pokemonsToSave = new ArrayList<>();

        for (Pokemon p1 : pokemons) {
            // Lets see if the pokemon is in the last pokemons
            boolean found = false;
            for (Pokemon p2 : lastPokemons) {
                if (p1.getEncounterId().equals(p2.getEncounterId())) {
                    found = true;
                    break;
                }
            }

            // If its not in the last pokemons, then it is a new pokemon
            if (!found) {
                pokemonsToSave.add(p1);
            }
        }

        if (pokemonsToSave.size() > 0) {
            logger.info("Saving " + pokemonsToSave.size() + " new pokemons");
            databaseService.putPokemons(pokemonsToSave);
        } else {
            logger.info("No pokemons to save.");
        }

        //Set the last pokemons to the new pokemons (this must be here, because of exceptions)
        lastPokemons = pokemons;

        logger.info("Done");
    }
}
