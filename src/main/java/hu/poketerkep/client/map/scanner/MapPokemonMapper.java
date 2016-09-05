package hu.poketerkep.client.map.scanner;

import com.google.common.primitives.Longs;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import hu.poketerkep.shared.geo.Coordinate;
import hu.poketerkep.shared.model.Pokemon;
import org.apache.commons.codec.binary.Base64;


class MapPokemonMapper {
    static Pokemon toPokemon(CatchablePokemon catchablePokemon) {
        Pokemon pokemon = new Pokemon();

        pokemon.setDisappearTime(catchablePokemon.getExpirationTimestampMs());
        pokemon.setEncounterId(Base64.encodeBase64String(Longs.toByteArray(catchablePokemon.getEncounterId())));
        pokemon.setCoordinate(Coordinate.fromDegrees(catchablePokemon.getLatitude(), catchablePokemon.getLongitude()));
        pokemon.setPokemonId(catchablePokemon.getPokemonIdValue());

        return pokemon;
    }
}
