package hu.poketerkep.client.map.scanner;

import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import hu.poketerkep.client.service.MapCacheService;
import hu.poketerkep.shared.geo.Coordinate;
import hu.poketerkep.shared.model.Pokemon;
import hu.poketerkep.shared.model.UserConfig;

import java.math.BigInteger;
import java.net.Proxy;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Logger;

class MockMapScannerWorker extends MapScannerWorker {
    private final MapCacheService mapCacheService;
    private final Logger log;
    private Random random = new Random();

    MockMapScannerWorker(UserConfig userConfig, MapCacheService mapCacheService, Logger log, Proxy proxy) {
        super(userConfig, mapCacheService, log, proxy);
        this.log = log;
        this.mapCacheService = mapCacheService;
    }

    @Override
    void connect() throws LoginFailedException, RemoteServerException {
        //Do nothing
    }

    @Override
    void scan(Coordinate location) throws LoginFailedException, RemoteServerException {
        HashSet<Pokemon> pokemonsToAdd = new HashSet<>();

        int limit = random.nextInt(2);
        for (int i = 0; i < limit; i++) {
            Pokemon pokemon = new Pokemon();
            pokemon.setEncounterId(new BigInteger(120, random).toString(32));
            pokemon.setCoordinate(location.getNew(((double) random.nextInt(90)) / 1000.0, random.nextInt(360)));
            pokemon.setPokemonId(random.nextInt(100) + 1);
            pokemon.setDisappearTime(Instant.now().plus(Duration.ofMinutes(random.nextInt(13) + 1)).toEpochMilli());
            pokemonsToAdd.add(pokemon);
        }

//        Pokemon pokemon = new Pokemon();
//        pokemon.setEncounterId(new BigInteger(120, random).toString(32));
//        pokemon.setCoordinate(location);
//        pokemon.setPokemonId(25);
//        pokemon.setDisappearTime(Instant.now().plus(Duration.ofMinutes(14)).toEpochMilli());
//        pokemonsToAdd.add(pokemon);

        if (pokemonsToAdd.size() != 0) {
            mapCacheService.addPokemons(pokemonsToAdd);

            log.info("Adding " + pokemonsToAdd.size() + " pokemons");
        }

    }

    @Override
    public boolean isConnected() {
        return true;
    }
}
