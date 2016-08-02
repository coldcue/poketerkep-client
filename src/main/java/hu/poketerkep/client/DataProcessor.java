package hu.poketerkep.client;

import hu.poketerkep.client.json.RawDataJsonDto;
import hu.poketerkep.client.mapper.PokemonMapper;
import hu.poketerkep.client.model.Pokemon;
import hu.poketerkep.client.service.PokemonDataService;
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
    private final PokemonGoMapInstanceManager instanceManager;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private List<Pokemon> lastPokemons = new ArrayList<>();

    @Autowired
    public DataProcessor(PokemonDataService pokemonDataService, PokemonGoMapInstanceManager instanceManager) {
        this.pokemonDataService = pokemonDataService;
        this.instanceManager = instanceManager;
    }

    @Scheduled(fixedDelay = 5 * 1000, initialDelay = 10 * 1000)
    public void processData() {
        logger.info("Fetching data...");
        List<RawDataJsonDto> rawDatas = instanceManager.getRawData();

        logger.info("Processing data...");


        // Get pokemons
        List<Pokemon> pokemons = rawDatas.stream()
                .filter(rawDataJsonDto -> rawDataJsonDto.getPokemons() != null) // Null check
                .map(RawDataJsonDto::getPokemons)
                .flatMap(Collection::stream)
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
            pokemonDataService.putPokemons(pokemonsToSave);
        } else {
            logger.info("No pokemons to save.");
        }

        //Set the last pokemons to the new pokemons (this must be here, because of exceptions)
        lastPokemons = pokemons;

        logger.info("Done");
    }
}
