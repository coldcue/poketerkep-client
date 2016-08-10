package hu.poketerkep.client.pokemonGoMap.instance;


import hu.poketerkep.client.json.RawDataJsonDto;
import hu.poketerkep.client.model.AllData;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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
                && isRunningFor(2, ChronoUnit.MINUTES)) {

            log.warning("There were no data from this instance for 2 minutes");

            // Stop the instance
            shouldBeStopped = true;
        }
    }

    void analyzeNewData(AllData allData) {
        // If there's no new data after 5 minutes, increase the no data rivers
        if (allData.getPokemons().size() == 0 && isRunningFor(5, ChronoUnit.MINUTES)) {
            noDataRivers++;
        }
        // If there were pokemons, decrease the no data rivers if its not 0
        else if (noDataRivers > 0) {
            noDataRivers--;
        }

        if (noDataRivers >= 10) {
            log.warning("There were too few pokemons from this instance");

            shouldBeStopped = true;
        }
    }

    private boolean isRunningFor(long amount, TemporalUnit unit) {
        return Instant.now().minus(amount, unit).isAfter(startTime);
    }
}
