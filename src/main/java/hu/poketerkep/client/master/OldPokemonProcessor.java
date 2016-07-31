package hu.poketerkep.client.master;

import hu.poketerkep.client.model.Pokemon;
import hu.poketerkep.client.service.PokemonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

@Component
public class OldPokemonProcessor {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final PokemonDataService pokemonDataService;

    @Value("${master-client:false}")
    private boolean master;

    @Autowired
    public OldPokemonProcessor(PokemonDataService pokemonDataService) {
        this.pokemonDataService = pokemonDataService;
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void processData() {
        //If the instance is master
        if (master) {
            logger.info("Searching for old pokemons...");
            List<Pokemon> oldPokemons = pokemonDataService.getOldPokemons();

            int oldPokemonsCount = oldPokemons.size();
            logger.info("Found " + oldPokemonsCount + " old pokemons...");

            if (oldPokemonsCount > 0) {
                logger.info("Deleting old pokemons...");
                oldPokemons.parallelStream()
                        .forEach(pokemonDataService::deletePokemon);
                logger.info("Deleted " + oldPokemonsCount + " old pokemons...");
            } else {
                logger.info("No old pokemons found");
            }
        }
    }
}
