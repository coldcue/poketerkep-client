package hu.poketerkep.json;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RawDataJsonDto {
    @JsonProperty(required = true)
    private List<GymJsonDto> gyms;
    @JsonProperty(required = true)
    private List<PokemonJsonDto> pokemons;
    @JsonProperty(required = true)
    private List<PokestopJsonDto> pokestops;
}
