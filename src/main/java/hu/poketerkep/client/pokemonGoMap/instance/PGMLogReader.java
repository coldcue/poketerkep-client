package hu.poketerkep.client.pokemonGoMap.instance;

import hu.poketerkep.client.model.UserConfig;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PGMLogReader extends Thread {

    private final PGMInstance instance;
    private final Process process;
    private BufferedReader bufferedReader;

    PGMLogReader(PGMInstance instance, Process process) {
        this.instance = instance;
        this.process = process;
    }

    @Override
    public void run() {
        try {
            PrintWriter log = new PrintWriter(new FileWriter(instance.getLogFile(), true), true);
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

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

            if ("Could not retrieve token: Account is not yet active, please redirect.".equals(message)) {
                instance.getMapManager().onUserBanned(userConfig);
                instance.getHealthAnalyzer().onUserBanned();
            }
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

    public void terminate() {
        this.interrupt();
    }
}
