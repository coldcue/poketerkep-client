package hu.poketerkep.client.map.scanner;

import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import hu.poketerkep.shared.model.Pokemon;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MapPokemonMapperTest {
    @Test
    public void toPokemon() throws Exception {

        CatchablePokemon catchablePokemon = Mockito.mock(CatchablePokemon.class);

        long expectedDisappearTime = 567894L;
        Mockito.when(catchablePokemon.getExpirationTimestampMs()).thenReturn(expectedDisappearTime);

        Mockito.when(catchablePokemon.getEncounterId()).thenReturn(0x45676567L);

        double expectedLatitude = 47.1;
        Mockito.when(catchablePokemon.getLatitude()).thenReturn(expectedLatitude);

        double expectedLongitude = 19.2;
        Mockito.when(catchablePokemon.getLongitude()).thenReturn(expectedLongitude);

        int expectedPokemonId = 100;
        Mockito.when(catchablePokemon.getPokemonIdValue()).thenReturn(expectedPokemonId);

        // Convert
        Pokemon pokemon = MapPokemonMapper.toPokemon(catchablePokemon);

        Assert.assertEquals(expectedDisappearTime, pokemon.getDisappearTime());
        Assert.assertEquals("AAAAAEVnZWc=", pokemon.getEncounterId());
        Assert.assertEquals(expectedLatitude, pokemon.getLatitude(), 0.0000001);
        Assert.assertEquals(expectedLongitude, pokemon.getLongitude(), 0.0000001);
        Assert.assertEquals(expectedPokemonId, pokemon.getPokemonId());

    }

}