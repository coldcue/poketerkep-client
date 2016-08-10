package hu.poketerkep.client.json;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawDataJsonDto {
    @JsonProperty(required = true)
    private List<GymJsonDto> gyms = new ArrayList<>();
    @JsonProperty(required = true)
    private List<PokemonJsonDto> pokemons = new ArrayList<>();
    @JsonProperty(required = true)
    private List<PokestopJsonDto> pokestops = new ArrayList<>();
    @JsonProperty(required = true)
    private List<ScannedJsonDto> scanned = new ArrayList<>();

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

    public List<ScannedJsonDto> getScanned() {
        return scanned;
    }

    public void setScanned(List<ScannedJsonDto> scanned) {
        this.scanned = scanned;
    }
}
