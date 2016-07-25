package hu.poketerkep.mapper;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import hu.poketerkep.json.PokemonJsonDto;
import hu.poketerkep.model.Pokemon;

import java.util.HashMap;
import java.util.Map;

/**
 * This maps json dto to Pokemon
 */
public class PokemonMapper {
    public static Pokemon mapFromJsonDto(PokemonJsonDto jsonDto) {
        Pokemon pokemon = new Pokemon();

        pokemon.setEncounterId(jsonDto.getEncounter_id());
        pokemon.setDisappearTime(jsonDto.getDisappear_time());
        pokemon.setLatitude(jsonDto.getLatitude());
        pokemon.setLongitude(jsonDto.getLongitude());
        pokemon.setPokemonId(jsonDto.getPokemon_id());
        pokemon.setPokemonName(jsonDto.getPokemon_name());
        pokemon.setSpawnpointId(jsonDto.getSpawnpoint_id());

        return pokemon;
    }

    public static Map<String, AttributeValue> mapToDynamoDb(Pokemon pokemon) {
        Map<String, AttributeValue> valueMap = new HashMap<>();

        valueMap.put("encounterId", new AttributeValue().withS(pokemon.getEncounterId()));
        valueMap.put("disappearTime", new AttributeValue().withN(String.valueOf(pokemon.getDisappearTime())));
        valueMap.put("latitude", new AttributeValue().withN(String.valueOf(pokemon.getLatitude())));
        valueMap.put("longitude", new AttributeValue().withN(String.valueOf(pokemon.getLongitude())));
        valueMap.put("pokemonId", new AttributeValue().withN(String.valueOf(pokemon.getPokemonId())));
        valueMap.put("pokemonName", new AttributeValue().withS(pokemon.getPokemonName()));
        valueMap.put("spawnpointId", new AttributeValue().withS(pokemon.getSpawnpointId()));

        return valueMap;
    }
}
