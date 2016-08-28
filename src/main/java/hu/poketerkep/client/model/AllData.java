package hu.poketerkep.client.model;

import hu.poketerkep.shared.model.Pokemon;

import java.util.HashSet;

public class AllData {
    private HashSet<Pokemon> pokemons = new HashSet<>();

    public HashSet<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(HashSet<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }
}
