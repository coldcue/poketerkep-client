package hu.poketerkep.client.map.scanner;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.MapObjects;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import hu.poketerkep.client.model.AllData;
import hu.poketerkep.client.support.UserConfigHelper;
import hu.poketerkep.shared.geo.Coordinate;
import hu.poketerkep.shared.model.UserConfig;
import okhttp3.OkHttpClient;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * Pokemon Search Thread
 */
class MapScannerWorker {
    private final UserConfig userConfig;
    private final OkHttpClient okHttpClient;
    private PokemonGo pokemonGo;

    MapScannerWorker(UserConfig userConfig, Proxy proxy) {
        this.userConfig = userConfig;

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .proxy(proxy)
                .build();
    }

    void connect() throws LoginFailedException, RemoteServerException {
        PtcCredentialProvider ptcCredentialProvider = new PtcCredentialProvider(okHttpClient, userConfig.getUserName(), UserConfigHelper.getPassword(userConfig));
        pokemonGo = new PokemonGo(okHttpClient);
        pokemonGo.login(ptcCredentialProvider);
        //TODO Enable profile automatically
        //PlayerProfile playerProfile = pokemonGo.getPlayerProfile();
        //playerProfile.enableAccount();
        //playerProfile.updateProfile();
    }

    public AllData search(Coordinate location) throws LoginFailedException, RemoteServerException {
        AllData allData = new AllData();
        MapObjects mapObjects;

        try {
            // Get all map objects
            pokemonGo.setLocation(location.getLatitude(), location.getLongitude(), 1.0);
            mapObjects = pokemonGo.getMap().getMapObjects();
        } catch (RemoteServerException | LoginFailedException e) {
            throw e;
        }

        // Add all pokemons
        mapObjects.getCatchablePokemons().stream()
                .map(MapPokemonMapper::toPokemon)
                .forEach(pokemon -> allData.getPokemons().add(pokemon));

        return allData;
    }

    public boolean isConnected() {
        try {
            return pokemonGo.getAuthInfo().isInitialized();
        } catch (LoginFailedException | RemoteServerException e) {
            return false;
        }
    }
}
