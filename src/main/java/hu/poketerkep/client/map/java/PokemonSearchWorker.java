package hu.poketerkep.client.map.java;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.MapObjects;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import hu.poketerkep.shared.geo.Coordinate;
import hu.poketerkep.client.map.java.helper.MapPokemonMapper;
import hu.poketerkep.client.model.AllData;
import hu.poketerkep.shared.model.UserConfig;
import hu.poketerkep.client.support.UserConfigHelper;
import okhttp3.OkHttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * Pokemon Search Thread
 */
class PokemonSearchWorker {
    private final UserConfig userConfig;
    private OkHttpClient okHttpClient;
    private PokemonGo pokemonGo;
    private Proxy proxy;
    private boolean connected;

    private PokemonSearchWorker(UserConfig userConfig) {
        this.userConfig = userConfig;
        connected = false;
        proxy = Proxy.NO_PROXY;
    }

    static PokemonSearchWorker withUserConfig(UserConfig userConfig) {
        return new PokemonSearchWorker(userConfig);
    }

    PokemonSearchWorker withProxy(int proxyPort) {
        proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", proxyPort));
        return this;
    }

    void connect() throws LoginFailedException, RemoteServerException {

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .proxy(proxy)
                .build();

        PtcCredentialProvider ptcCredentialProvider = new PtcCredentialProvider(okHttpClient, userConfig.getUserName(), UserConfigHelper.getPassword(userConfig));
        pokemonGo = new PokemonGo(ptcCredentialProvider, okHttpClient);

        connected = true;

        //TODO Enable profile automatically
        //PlayerProfile playerProfile = pokemonGo.getPlayerProfile();
        //playerProfile.enableAccount();
        //playerProfile.updateProfile();
    }

    public AllData search(Coordinate location) throws LoginFailedException, RemoteServerException {
        AllData result = new AllData();

        MapObjects mapObjects;
        try {
            // Get all map objects
            pokemonGo.setLocation(location.getLatitude(), location.getLongitude(), 1.0);
            mapObjects = pokemonGo.getMap().getMapObjects();
        } catch (RemoteServerException | LoginFailedException e) {
            connected = false;
            throw e;
        }

        connected = true;

        // Add all pokemons
        mapObjects.getCatchablePokemons().stream()
                .map(MapPokemonMapper::toPokemon)
                .forEach(result.getPokemons()::add);

        return result;
    }

    public boolean isConnected() {
        return connected;
    }
}
