package hu.poketerkep.client.mapper;

import hu.poketerkep.client.json.RawDataJsonDto;
import hu.poketerkep.client.model.AllData;
import hu.poketerkep.shared.model.Pokemon;

import java.util.HashSet;
import java.util.stream.Collectors;


public class RawDataToAllDataMapper {
    public static AllData fromRawData(RawDataJsonDto rawDataJsonDto) {
        if (rawDataJsonDto == null) return null;

        AllData allData = new AllData();

        // Get pokemons
        HashSet<Pokemon> pokemons = new HashSet<>(rawDataJsonDto.getPokemons().stream()
                .map(PokemonMapper::mapFromJsonDto) // Map json dto to model
                .collect(Collectors.toList()));

        allData.setPokemons(pokemons);

        return allData;
    }
}
