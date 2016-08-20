package hu.poketerkep.client.map.python;

import hu.poketerkep.client.json.RawDataJsonDto;
import hu.poketerkep.client.map.MapConfiguration;
import hu.poketerkep.client.map.MapInstance;
import hu.poketerkep.client.map.MapManager;
import hu.poketerkep.client.mapper.RawDataToAllDataMapper;
import hu.poketerkep.client.model.AllData;
import hu.poketerkep.client.model.UserConfig;
import hu.poketerkep.client.model.helpers.AllDataUtils;
import hu.poketerkep.client.support.UserConfigHelper;
import org.apache.commons.io.FileUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * This is a PokemonGoMap instance
 */
public class PGMInstance implements MapInstance {
    public static final File DIR = new File("PokemonGo-Map");
    private static final String RUNSERVER_PY = "runserver.py";
    private static final String PYTHON = "python";
    private final File logFile;
    private final Logger logger;
    private final MapManager mapManager;
    private final MapConfiguration conf;
    private final int instanceId;
    private final File workingDir;
    private final PGMInstanceHealthAnalyzer healthAnalyzer;
    private final String instanceName;
    private Process process;
    private AllData oldAllData;
    private PGMLogReader pgmLogReader;

    public PGMInstance(MapManager mapManager, MapConfiguration conf, int instanceId) {
        this.mapManager = mapManager;
        this.conf = conf;
        this.instanceId = instanceId;

        instanceName = "PGM-Instance-" + conf.getLocation().getLocationId();
        logger = Logger.getLogger(instanceName);
        logFile = new File("instances/" + instanceName + ".log");
        workingDir = new File("instances/" + instanceName);
        healthAnalyzer = new PGMInstanceHealthAnalyzer(this);
    }

    @Override
    public void check() {
        healthAnalyzer.checkAge();
    }

    @Override
    public boolean isShouldBeStopped() {
        return healthAnalyzer.isShouldBeStopped();
    }

    @Override
    public MapConfiguration getConfiguration() {
        return conf;
    }

    PGMInstanceHealthAnalyzer getHealthAnalyzer() {
        return healthAnalyzer;
    }

    @Override
    public String getInstanceName() {
        return instanceName;
    }

    @Override
    public int getInstanceId() {
        return instanceId;
    }

    @Override
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

            pgmLogReader = new PGMLogReader(this, process);
            pgmLogReader.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void stop() {
        logger.info("Stopping instance...");
        if (process != null) {
            pgmLogReader.terminate();
            ProcessUtils.killProcess(process);
            process.destroy();
            try {
                Thread.sleep(500);
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
            rawData = restTemplate.getForObject("http://localhost:" + getPort() + "/raw_data?pokemon=true&pokestops=false&gyms=false&scanned=false", RawDataJsonDto.class);

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

    @Override
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


    File getLogFile() {
        return logFile;
    }

    public MapManager getMapManager() {
        return mapManager;
    }
}
