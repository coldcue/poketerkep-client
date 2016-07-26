package hu.poketerkep.client.master;

import hu.poketerkep.client.config.support.InstanceConfiguration;
import hu.poketerkep.client.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

@Component
public class OldPokemonProcessor {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final DatabaseService databaseService;
    private final InstanceConfiguration instanceConfiguration;

    @Autowired
    public OldPokemonProcessor(DatabaseService databaseService, InstanceConfiguration instanceConfiguration) {
        this.databaseService = databaseService;
        this.instanceConfiguration = instanceConfiguration;
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void processData() {
        //If the instance is master
        if (instanceConfiguration.isMaster()) {
            logger.info("Searching for old pokemons...");
            List<String> oldPokemonEncounterIds = databaseService.getOldPokemonEncounterIds();

            int oldPokemonsCount = oldPokemonEncounterIds.size();
            logger.info("Found " + oldPokemonsCount + " old pokemons...");

            if (oldPokemonsCount > 0) {
                logger.info("Deleting old pokemons...");
                oldPokemonEncounterIds.parallelStream()
                        .forEach(databaseService::deletePokemonByEncounterId);
                logger.info("Deleted " + oldPokemonsCount + " old pokemons...");
            } else {
                logger.info("No old pokemons found");
            }
        }
    }
}
