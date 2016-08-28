package hu.poketerkep.client.map.python;


import hu.poketerkep.client.config.Constants;
import hu.poketerkep.client.json.RawDataJsonDto;
import hu.poketerkep.client.model.AllData;
import hu.poketerkep.shared.model.UserConfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.logging.Logger;

class PGMInstanceHealthAnalyzer {

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

        long wrongDatePokemonsSize = rawDataJsonDto.getPokemons().stream()
                .filter(pokemonJsonDto -> pokemonJsonDto.getDisappear_time() > nowPlus15Min)
                .peek(pokemonJsonDto -> pokemonJsonDto.setDisappear_time(nowPlus15Min))
                .count();

        if (wrongDatePokemonsSize > 0) {
            log.warning("There were " + wrongDatePokemonsSize + " pokemons with wrong disappear time!");
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

    /**
     * Check the age of the instance. If it's older than 1hr, kill it
     */
    public void checkAge() {
        if (isRunningFor(Constants.INSTANCE_KILL_AGE, ChronoUnit.HOURS)) {
            log.info("Stopping instance, because it is too old.");
            stopInstance();
        }
    }

    private boolean isRunningFor(long amount, TemporalUnit unit) {
        return Instant.now().minus(amount, unit).isAfter(startTime);
    }

    void onUserBanned() {
        int bannedCount = (int) pgmInstance.getConfiguration().getUsers().stream()
                .filter(UserConfig::getBanned)
                .count();
        int userCount = pgmInstance.getConfiguration().getUsers().size();
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
