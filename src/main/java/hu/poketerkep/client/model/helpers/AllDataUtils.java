package hu.poketerkep.client.model.helpers;

import hu.poketerkep.client.model.AllData;
import hu.poketerkep.shared.model.Pokemon;


public class AllDataUtils {
    public static AllData getNew(AllData oldAllData, AllData newAllData) {

        // If there were no old data
        if (oldAllData == null) return newAllData;

        AllData result = new AllData();

        for (Pokemon newPokemon : newAllData.getPokemons()) {
            // Lets see if the new pokemon is in the old pokemons
            boolean found = false;
            for (Pokemon oldPokemon : oldAllData.getPokemons()) {
                if (newPokemon.getEncounterId().equals(oldPokemon.getEncounterId())) {
                    found = true;
                    break;
                }
            }

            // If its not in the last pokemons, then it is a new pokemon
            if (!found) {
                result.getPokemons().add(newPokemon);
            }
        }

        return result;
    }
}
