package hu.poketerkep.client.pokemonGoMap;

import hu.poketerkep.client.model.UserConfig;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokemonGoMapInstanceLogReader extends Thread {

    private final PokemonGoMapInstance instance;
    private final Process process;

    public PokemonGoMapInstanceLogReader(PokemonGoMapInstance instance, Process process) {
        this.instance = instance;
        this.process = process;
    }

    @Override
    public void run() {
        try {
            PrintWriter log = new PrintWriter(new FileWriter(instance.getLogFile()));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null && !isInterrupted()) {
                log.println(line);
                analyzeLog(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Analyze a line of log
     *
     * @param line str
     */
    private void analyzeLog(String line) {
        Matcher matcher = Pattern.compile("^.*search_worker_(?<id>\\d+).*]\\s+(?<message>.*)$").matcher(line);

        if (matcher.find()) {
            int id = Integer.parseInt(matcher.group("id"));
            String message = matcher.group("message");

            UserConfig userConfig = getUserConfigFromId(id);
            System.out.println(userConfig.getUserName() + ": " + message);
        }
    }

    /**
     * Get the UserConfiguration from the search id
     *
     * @param id the id from the log
     * @return the UserConfig
     */
    private UserConfig getUserConfigFromId(int id) {
        return instance.getConf().getUsers().get(id);
    }
}
