package hu.poketerkep.client.map.scanner;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import hu.poketerkep.client.service.ClientService;
import hu.poketerkep.client.support.UserConfigHelper;
import hu.poketerkep.shared.geo.Coordinate;
import hu.poketerkep.shared.model.Pokemon;
import hu.poketerkep.shared.model.UserConfig;
import okhttp3.OkHttpClient;

import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Pokemon Search Thread
 */
class MapScannerWorker {
    private final Logger log;
    private final UserConfig userConfig;
    private final OkHttpClient okHttpClient;
    private final ClientService clientService;
    private PokemonGo pokemonGo;

    MapScannerWorker(UserConfig userConfig, Proxy proxy, ClientService clientService, Logger log) {
        this.userConfig = userConfig;

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .proxy(proxy)
                .build();
        this.clientService = clientService;
        this.log = log;
    }

    void connect() throws LoginFailedException, RemoteServerException {
        pokemonGo = new PokemonGo(okHttpClient);

        String userName = userConfig.getUserName();
        String password = UserConfigHelper.getPassword(userConfig);

        pokemonGo.login(new PtcCredentialProvider(okHttpClient, userName, password));

        //TODO Enable profile automatically
        PlayerProfile playerProfile = pokemonGo.getPlayerProfile();
        playerProfile.activateAccount();
        playerProfile.updateProfile();
    }

    void scan(Coordinate location) throws LoginFailedException, RemoteServerException {
        try {
            // Get pokemons
            pokemonGo.setLocation(location.getLatitude(), location.getLongitude(), 1.0);

            List<Pokemon> pokemons = pokemonGo.getMap().getCatchablePokemon().stream()
                    .map(MapPokemonMapper::toPokemon)
                    .collect(Collectors.toList());

            if (pokemons.size() != 0) {
                log.info("Adding " + pokemons.size() + " pokemons");
                clientService.addPokemons(pokemons);
            }


        } catch (RemoteServerException | LoginFailedException e) {
            throw e;
        }
    }

    public boolean isConnected() {
        try {
            return pokemonGo.getAuthInfo().isInitialized();
        } catch (LoginFailedException | RemoteServerException e) {
            return false;
        }
    }
}
