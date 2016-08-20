package hu.poketerkep.client.map.java;

import hu.poketerkep.client.model.Pokemon;

import java.util.Set;

public class SearchResult {
    private final Set<Pokemon> pokemons;

    public SearchResult(Set<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }
}
