package hu.poketerkep;

import hu.poketerkep.json.RawDataJsonDto;
import hu.poketerkep.model.Pokemon;
import hu.poketerkep.pokemonGoMap.PokemonGoMapDataService;
import hu.poketerkep.service.DatabaseService;
import hu.poketerkep.support.PokemonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This extracts data and
 */
@Component
public class DataProcessor {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private PokemonGoMapDataService dataService;

    @Autowired
    private DatabaseService databaseService;

    @Scheduled(fixedDelay = 15000, initialDelay = 10000)
    public void processData() {
        logger.info("Fetching data...");
        RawDataJsonDto rawData = dataService.getRawData();

        logger.info("Data arrived: [pokemons: "+rawData.getPokemons().size()+"]");

        logger.info("Processing data...");
        List<Pokemon> pokemons = rawData.getPokemons().parallelStream().map(PokemonMapper::mapFromJsonDto).collect(Collectors.toList());

        logger.info("Saving data...");
        databaseService.putPokemons(pokemons);

        logger.info("Done");
    }
}
