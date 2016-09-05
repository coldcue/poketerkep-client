package hu.poketerkep.client.map.scanner;

import POGOProtos.Map.Pokemon.MapPokemonOuterClass;
import com.google.common.primitives.Longs;
import hu.poketerkep.shared.geo.Coordinate;
import hu.poketerkep.shared.model.Pokemon;
import org.apache.commons.codec.binary.Base64;


class MapPokemonMapper {
    static Pokemon toPokemon(MapPokemonOuterClass.MapPokemon mapPokemon) {
        Pokemon pokemon = new Pokemon();

        pokemon.setDisappearTime(mapPokemon.getExpirationTimestampMs());
        pokemon.setEncounterId(Base64.encodeBase64String(Longs.toByteArray(mapPokemon.getEncounterId())));
        pokemon.setCoordinate(Coordinate.fromDegrees(mapPokemon.getLatitude(), mapPokemon.getLongitude()));
        pokemon.setPokemonId(mapPokemon.getPokemonIdValue());

        return pokemon;
    }
}
