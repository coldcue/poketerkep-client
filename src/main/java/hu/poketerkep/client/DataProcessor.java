package hu.poketerkep.client;

import hu.poketerkep.client.json.RawDataJsonDto;
import hu.poketerkep.client.model.Pokemon;
import hu.poketerkep.client.pokemonGoMap.PokemonGoMapDataService;
import hu.poketerkep.client.service.DatabaseService;
import hu.poketerkep.client.mapper.PokemonMapper;
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
    private final PokemonGoMapDataService dataService;
    private final DatabaseService databaseService;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    public DataProcessor(PokemonGoMapDataService dataService, DatabaseService databaseService) {
        this.dataService = dataService;
        this.databaseService = databaseService;
    }

    @Scheduled(fixedDelay = 15000, initialDelay = 10000)
    public void processData() {
        logger.info("Fetching data...");
        RawDataJsonDto rawData = dataService.getRawData();

        logger.info("Data arrived: [pokemons: " + rawData.getPokemons().size() +
                ", pokestops: " + rawData.getPokestops().size() +
                ", gyms: " + rawData.getGyms().size() + "]");

        logger.info("Processing data...");
        List<Pokemon> pokemons = rawData.getPokemons().parallelStream().map(PokemonMapper::mapFromJsonDto).collect(Collectors.toList());

        logger.info("Saving data...");
        databaseService.putPokemons(pokemons);

        logger.info("Done");
    }
}
