package hu.poketerkep.client.json;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawDataJsonDto {
    @JsonProperty(required = true)
    private HashSet<GymJsonDto> gyms = new HashSet<>();
    @JsonProperty(required = true)
    private HashSet<PokemonJsonDto> pokemons = new HashSet<>();
    @JsonProperty(required = true)
    private HashSet<PokestopJsonDto> pokestops = new HashSet<>();
    @JsonProperty(required = true)
    private HashSet<ScannedJsonDto> scanned = new HashSet<>();

    public HashSet<GymJsonDto> getGyms() {
        return gyms;
    }

    public void setGyms(HashSet<GymJsonDto> gyms) {
        this.gyms = gyms;
    }

    public HashSet<PokemonJsonDto> getPokemons() {
        return pokemons;
    }

    public void setPokemons(HashSet<PokemonJsonDto> pokemons) {
        this.pokemons = pokemons;
    }

    public HashSet<PokestopJsonDto> getPokestops() {
        return pokestops;
    }

    public void setPokestops(HashSet<PokestopJsonDto> pokestops) {
        this.pokestops = pokestops;
    }

    public HashSet<ScannedJsonDto> getScanned() {
        return scanned;
    }

    public void setScanned(HashSet<ScannedJsonDto> scanned) {
        this.scanned = scanned;
    }
}
