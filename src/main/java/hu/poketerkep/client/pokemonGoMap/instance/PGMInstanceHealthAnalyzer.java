package hu.poketerkep.client.pokemonGoMap.instance;


import hu.poketerkep.client.config.Constants;
import hu.poketerkep.client.json.PokemonJsonDto;
import hu.poketerkep.client.json.RawDataJsonDto;
import hu.poketerkep.client.model.AllData;
import hu.poketerkep.client.model.UserConfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashSet;
import java.util.logging.Logger;

public class PGMInstanceHealthAnalyzer {

    private final PGMInstance pgmInstance;
    private final Instant startTime;
    private final Logger log;
    private int noDataRivers; //Between 0 and 10
    private boolean shouldBeStopped = false;

    PGMInstanceHealthAnalyzer(PGMInstance pgmInstance) {
        this.pgmInstance = pgmInstance;
        this.startTime = Instant.now();
        log = Logger.getLogger(pgmInstance.getInstanceName() + "-Health");
    }

    public boolean isShouldBeStopped() {
        return shouldBeStopped;
    }

    void analyzeRawData(RawDataJsonDto rawDataJsonDto) {
        int pokemonCount = rawDataJsonDto.getPokemons().size();
        int pokestopCount = rawDataJsonDto.getPokestops().size();
        int gymCount = rawDataJsonDto.getGyms().size();

        // If there's no data at all after 3 minutes, stop the instance
        if (pokemonCount == 0
                && pokestopCount == 0
                && gymCount == 0
                && isRunningFor(Constants.NO_DATA_GRACE_PERIOD, ChronoUnit.MINUTES)) {

            log.warning("There were no data from this instance for " + Constants.NO_DATA_GRACE_PERIOD + " minutes");

            // Stop the instance
            stopInstance();
        }

        // Check max 15 min
        long nowPlus15Min = Instant.now().plus(15, ChronoUnit.MINUTES).toEpochMilli();

        HashSet<PokemonJsonDto> wrongDatePokemons = new HashSet<>();
        rawDataJsonDto.getPokemons().stream()
                .filter(pokemonJsonDto -> pokemonJsonDto.getDisappear_time() > nowPlus15Min)
                .forEach(wrongDatePokemons::add);

        if (wrongDatePokemons.size() > 0) {
            log.warning("There were " + wrongDatePokemons.size() + " pokemons with wrong disappear time!");
            rawDataJsonDto.getPokemons().removeAll(wrongDatePokemons);
        }
    }

    void analyzeNewData(AllData allData) {
        // If there's no new data after 5 minutes, increase the no data rivers
        if (allData.getPokemons().size() == 0 && isRunningFor(5, ChronoUnit.MINUTES)) {
            noDataRivers++;
        }
        // If there were pokemons, decrease the no data rivers if its not 0
        else if (noDataRivers > 0) {
            noDataRivers =
                    (noDataRivers - Constants.NEW_DATA_RIVERS_MINUS <= 0)
                            ? 0
                            : noDataRivers - Constants.NEW_DATA_RIVERS_MINUS;
        }

        if (noDataRivers >= Constants.NEW_DATA_RIVERS_MAX) {
            log.warning("There were too few pokemons from this instance");

            stopInstance();
        }
    }

    private boolean isRunningFor(long amount, TemporalUnit unit) {
        return Instant.now().minus(amount, unit).isAfter(startTime);
    }

    void onUserBanned() {
        int bannedCount = (int) pgmInstance.getConf().getUsers().stream()
                .filter(UserConfig::getBanned)
                .count();
        int userCount = pgmInstance.getConf().getUsers().size();
        int remainingUsers = userCount - bannedCount;

        if (remainingUsers < 10) {
            log.warning("Too much banned users in the instance");
            stopInstance();
        }
    }

    private void stopInstance() {
        shouldBeStopped = true;
    }
}
