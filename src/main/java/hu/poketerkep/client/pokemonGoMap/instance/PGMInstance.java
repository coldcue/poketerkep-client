package hu.poketerkep.client.pokemonGoMap.instance;

import hu.poketerkep.client.json.RawDataJsonDto;
import hu.poketerkep.client.mapper.RawDataToAllDataMapper;
import hu.poketerkep.client.model.AllData;
import hu.poketerkep.client.model.UserConfig;
import hu.poketerkep.client.model.helpers.AllDataUtils;
import hu.poketerkep.client.pokemonGoMap.MapManager;
import hu.poketerkep.client.support.UserConfigHelper;
import org.apache.commons.io.FileUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This is a PokemonGoMap instance
 */
public class PGMInstance {
    public static final File DIR = new File("PokemonGo-Map");
    private static final String RUNSERVER_PY = "runserver.py";
    private static final String PYTHON = "python";
    private final File logFile;
    private final Logger logger;
    private final MapManager mapManager;
    private final PGMConfiguration conf;
    private final int instanceId;
    private final File workingDir;
    private final PGMInstanceHealthAnalyzer healthAnalyzer;
    private final String instanceName;
    private Process process;
    private AllData oldAllData;

    public PGMInstance(MapManager mapManager, PGMConfiguration conf, int instanceId) {
        this.mapManager = mapManager;
        this.conf = conf;
        this.instanceId = instanceId;

        instanceName = "PGM-Instance-" + conf.getLocation().getLocationId();
        logger = Logger.getLogger(instanceName);
        logFile = new File("instances/" + instanceName + ".log");
        workingDir = new File("instances/" + instanceName);
        healthAnalyzer = new PGMInstanceHealthAnalyzer(this);
    }

    public PGMInstanceHealthAnalyzer getHealthAnalyzer() {
        return healthAnalyzer;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void start() throws IOException {
        String locationString = conf.getLocation().getLatitude() + " " + conf.getLocation().getLongitude();
        logger.info("Starting instance: [user:" + conf.getUsers() + ", loc:" + locationString + "]");

        logger.info("Creating working directory: " + workingDir);
        FileUtils.copyDirectory(DIR, workingDir);

        // Add command parameters
        List<String> command = new ArrayList<>();
        command.addAll(Arrays.asList(PYTHON,
                RUNSERVER_PY,
                "-st", Integer.toString(conf.getLocation().getSteps()),
                "-k", conf.getGoogleMapsKey(),
                "-l", locationString,
                "-P", Integer.toString(getPort())
        ));

        // Add users
        for (UserConfig userConfig : conf.getUsers()) {
            command.add("-u");
            command.add(userConfig.getUserName());
            command.add("-p");
            command.add(UserConfigHelper.getPassword(userConfig));
        }

        // Check if there's a proxy
        Optional<Integer> proxyPort = conf.getProxyPort();
        if (proxyPort.isPresent()) {
            String proxyAddress = "socks5://127.0.0.1:" + Integer.toString(proxyPort.get());
            command.addAll(Arrays.asList(
                    "--proxy", proxyAddress
            ));
        }

        logger.info("Command: " + String.join(" ", command));


        try {
            //Start the process
            process = new ProcessBuilder(command)
                    .directory(workingDir)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectErrorStream(true)
                    .start();

            PGMLogReader pgmLogReader = new PGMLogReader(this, process);
            pgmLogReader.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void stop() {
        logger.info("Stopping instance...");
        if (process != null) {
            process.destroy();
            try {
                process.waitFor(10, TimeUnit.SECONDS);
                process = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                logger.info("Deleting directory...");
                FileUtils.deleteDirectory(workingDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private RawDataJsonDto getRawData() {
        logger.fine("Getting data...");
        RestTemplate restTemplate = new RestTemplate();

        RawDataJsonDto rawData = new RawDataJsonDto();
        try {
            rawData = restTemplate.getForObject("http://localhost:" + getPort() + "/raw_data?pokemon=true&pokestops=true&gyms=true&scanned=true", RawDataJsonDto.class);

            logger.info("Data arrived: [pokemons: " + rawData.getPokemons().size() +
                    ", pokestops: " + rawData.getPokestops().size() +
                    ", gyms: " + rawData.getGyms().size() + "]");
        } catch (Exception e) {
            logger.severe("Map does not answer!");
        }

        // Send raw data to health analysis
        healthAnalyzer.analyzeRawData(rawData);

        return rawData;
    }


    private AllData getAllData() {
        RawDataJsonDto rawData = getRawData();
        return RawDataToAllDataMapper.fromRawData(rawData);
    }

    public AllData getNewAllData() {
        AllData allData = getAllData();
        AllData newAllData = AllDataUtils.getNew(oldAllData, allData);

        oldAllData = allData;

        // Send new data for health analysis
        healthAnalyzer.analyzeNewData(newAllData);

        return newAllData;
    }

    private int getPort() {
        return 6000 + instanceId;
    }

    public PGMConfiguration getConf() {
        return conf;
    }

    File getLogFile() {
        return logFile;
    }

    public MapManager getMapManager() {
        return mapManager;
    }
}
