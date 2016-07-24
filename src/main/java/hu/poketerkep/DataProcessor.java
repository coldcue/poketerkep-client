package hu.poketerkep;

import hu.poketerkep.json.RawDataJsonDto;
import hu.poketerkep.pokemonGoMap.PokemonGoMapDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * This extracts data and
 */
@Component
public class DataProcessor {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private PokemonGoMapDataService dataService;

    @Scheduled(fixedDelay = 1000)
    public void processData() {
        logger.info("Fetching data...");
        RawDataJsonDto rawData = dataService.getRawData();
        logger.info("Processing data...");
    }
}
