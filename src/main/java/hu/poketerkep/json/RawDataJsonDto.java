package hu.poketerkep.json;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawDataJsonDto {
    @JsonProperty(required = true)
    private List<GymJsonDto> gyms;
    @JsonProperty(required = true)
    private List<PokemonJsonDto> pokemons;
    @JsonProperty(required = true)
    private List<PokestopJsonDto> pokestops;

    public List<GymJsonDto> getGyms() {
        return gyms;
    }

    public void setGyms(List<GymJsonDto> gyms) {
        this.gyms = gyms;
    }

    public List<PokemonJsonDto> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<PokemonJsonDto> pokemons) {
        this.pokemons = pokemons;
    }

    public List<PokestopJsonDto> getPokestops() {
        return pokestops;
    }

    public void setPokestops(List<PokestopJsonDto> pokestops) {
        this.pokestops = pokestops;
    }
}
