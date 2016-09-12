package hu.poketerkep.client.service;

import hu.poketerkep.shared.api.ClientAPIEndpoint;
import hu.poketerkep.shared.model.Pokemon;
import hu.poketerkep.shared.model.RandomPokemonGenerator;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collection;


public class MapCacheServiceTest {

    @Test
    public void test() throws Exception {
        ClientAPIEndpoint clientAPIEndpoint = Mockito.mock(ClientAPIEndpoint.class);
        MapCacheService mapCacheService = new MapCacheService(clientAPIEndpoint);

        Collection<Pokemon> pokemons = RandomPokemonGenerator.generateN(100);

        mapCacheService.addPokemons(pokemons);
    }

}