package hu.poketerkep.client.service;


import hu.poketerkep.shared.api.ClientAPIEndpoint;
import hu.poketerkep.shared.model.Pokemon;
import hu.poketerkep.shared.model.RandomPokemonGenerator;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collection;

public class ClientServiceTest {

    @Test
    public void addPokemons() {
        ClientAPIEndpoint clientAPIEndpoint = Mockito.mock(ClientAPIEndpoint.class);
        ClientService clientService = new ClientService(clientAPIEndpoint);

        Collection<Pokemon> pokemons = RandomPokemonGenerator.generateN(50);

        clientService.addPokemons(pokemons);

        Mockito.verify(clientAPIEndpoint).addPokemons(pokemons.toArray(new Pokemon[50]));
    }

}