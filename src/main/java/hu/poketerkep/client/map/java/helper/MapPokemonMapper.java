package hu.poketerkep.client.map.java.helper;

import POGOProtos.Map.Pokemon.MapPokemonOuterClass;
import com.google.common.primitives.Longs;
import hu.poketerkep.client.model.Pokemon;
import org.apache.commons.codec.binary.Base64;


public class MapPokemonMapper {
    public static Pokemon toPokemon(MapPokemonOuterClass.MapPokemon mapPokemon) {
        Pokemon pokemon = new Pokemon();

        pokemon.setDisappearTime(mapPokemon.getExpirationTimestampMs());
        pokemon.setEncounterId(Base64.encodeBase64String(Longs.toByteArray(mapPokemon.getEncounterId())));
        pokemon.setLatitude(mapPokemon.getLatitude());
        pokemon.setLongitude(mapPokemon.getLongitude());
        pokemon.setPokemonId(mapPokemon.getPokemonIdValue());
        pokemon.setSpawnpointId(mapPokemon.getSpawnPointId());

        return pokemon;
    }
}
