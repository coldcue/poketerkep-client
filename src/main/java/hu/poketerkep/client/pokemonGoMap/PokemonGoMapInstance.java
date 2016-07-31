package hu.poketerkep.client.pokemonGoMap;

import hu.poketerkep.client.json.RawDataJsonDto;
import hu.poketerkep.client.support.UserConfigHelper;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This is a PokemonGoMap instance
 */
public class PokemonGoMapInstance {
    public static final File DIR = new File("PokemonGo-Map");
    public static final String RUNSERVER_PY = "runserver.py";
    private static final String PYTHON = "python";
    private static final File LOG = new File("pokem.log");
    private final Logger logger;
    private final PokemonGoMapConfiguration conf;
    private final int instanceId;

    private Process process;

    public PokemonGoMapInstance(PokemonGoMapConfiguration conf, int instanceId) {
        this.conf = conf;
        this.instanceId = instanceId;
        logger = Logger.getLogger("PGM-Instance-"
                + conf.getUser().getUserName()
                + "-" + conf.getLocation().getLocationId());
    }

    public void start() {
        logger.info("Starting PokemonGo-Map...");

        //Check if directory and runnable is present
        ProcessBuilder processBuilder = new ProcessBuilder("python",
                "runserver.py",
                "-u", conf.getUser().getUserName(),
                "-p", UserConfigHelper.getPassword(conf.getUser()),
                "-st", Integer.toString(conf.getLocation().getSteps()),
                "-k", conf.getGoogleMapsKey(),
                "-l", conf.getLocation().getLatitude() + " " + conf.getLocation().getLongitude(),
                "-t", Integer.toString(3),
                "-P", Integer.toString(getPort()));

        processBuilder.directory(DIR);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(LOG);

        //TODO handle errors
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        logger.info("Stopping PokemonGo-Map...");
        if (process != null) {
            process.destroy();
            try {
                process.waitFor(10, TimeUnit.SECONDS);
                process = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public RawDataJsonDto getRawData() {
        logger.info("Getting data...");
        RestTemplate restTemplate = new RestTemplate();
        RawDataJsonDto rawData = restTemplate.getForObject("http://localhost:" + getPort() + "/raw_data?pokemon=true&pokestops=true&gyms=true&scanned=true", RawDataJsonDto.class);

        logger.info("Data arrived: [pokemons: " + rawData.getPokemons().size() +
                ", pokestops: " + rawData.getPokestops().size() +
                ", gyms: " + rawData.getGyms().size() + "]");

        return rawData;
    }

    private int getPort() {
        return 5000 + instanceId;
    }

    public boolean isRunning() {
        return process != null;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public PokemonGoMapConfiguration getConf() {
        return conf;
    }
}
