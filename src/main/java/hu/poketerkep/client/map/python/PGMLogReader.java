package hu.poketerkep.client.map.python;

import hu.poketerkep.shared.model.UserConfig;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PGMLogReader extends Thread {

    private final PGMInstance instance;
    private final Process process;

    PGMLogReader(PGMInstance instance, Process process) {
        this.instance = instance;
        this.process = process;
    }

    @Override
    public void run() {
        try {
            PrintWriter log = new PrintWriter(new FileWriter(instance.getLogFile(), true), true);
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

            if ("Could not retrieve token: Account is not yet active, please redirect.".equals(message)) {
                instance.getMapManager().onUserBanned(userConfig);
                instance.getHealthAnalyzer().onUserBanned();
            } else if (message.contains("map parsing failed")) {
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
        return instance.getConfiguration().getUsers().get(id);
    }

    public void terminate() {
        this.interrupt();
    }
}
