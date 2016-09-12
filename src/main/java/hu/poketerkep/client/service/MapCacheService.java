package hu.poketerkep.client.service;

import hu.poketerkep.client.config.LocalConstants;
import hu.poketerkep.shared.api.ClientAPIEndpoint;
import hu.poketerkep.shared.model.Pokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service
public class MapCacheService {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final ClientAPIEndpoint clientAPIEndpoint;
    private final Collection<Pokemon> pokemonsCache;

    @Autowired
    public MapCacheService(ClientAPIEndpoint clientAPIEndpoint) {
        this.clientAPIEndpoint = clientAPIEndpoint;

        pokemonsCache = ConcurrentHashMap.newKeySet();
    }

    @Scheduled(fixedRate = LocalConstants.SEND_POKEMONS_TO_MASTER_RATE)
    public void sendPokemonsToMaster() {
        if (!pokemonsCache.isEmpty()) {
            Pokemon[] pokemons = pokemonsCache.stream()
                    .toArray(Pokemon[]::new);

            try {
                clientAPIEndpoint.addPokemons(pokemons);
                pokemonsCache.removeAll(Arrays.asList(pokemons));
                log.info("Sent " + pokemons.length + " Pokemons to the master");
            } catch (Exception e) {
                log.severe("Cannot send pokemons to the master");
            }

        }
    }

    public void addPokemons(Collection<Pokemon> pokemons) {
        pokemonsCache.addAll(pokemons);
    }
}
