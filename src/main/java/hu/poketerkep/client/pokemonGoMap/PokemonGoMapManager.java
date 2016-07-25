package hu.poketerkep.client.pokemonGoMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This service manages PokemonGoMap
 */
@Component
public class PokemonGoMapManager implements SmartLifecycle {
    public static final File DIR = new File("PokemonGo-Map");
    public static final String RUNSERVER_PY = "runserver.py";
    private static final String PYTHON = "python";
    private static final File LOG = new File("pokem.log");
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final PokemonGoMapConfiguration conf;

    private Process process;

    @Autowired
    public PokemonGoMapManager(PokemonGoMapConfiguration conf) {
        this.conf = conf;
    }

    @Override
    public void start() {
        logger.info("Starting PokemonGo-Map...");

        //Check if directory and runnable is present
        ProcessBuilder processBuilder = new ProcessBuilder("python",
                "runserver.py",
                "-u", conf.getUsername(),
                "-p", conf.getPassword(),
                "-st", Integer.toString(conf.getSteps()),
                "-k", conf.getGoogleMapsKey(),
                "-l", conf.getLocation());

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

    @Override
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

    @Override
    public boolean isRunning() {
        return process != null;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        this.stop();
        callback.run();
    }


    @Override
    public int getPhase() {
        return 0;
    }
}