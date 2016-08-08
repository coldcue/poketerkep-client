package hu.poketerkep.client.tor;


import com.googlecode.torcontrol.TorControl;
import com.googlecode.torcontrol.command.TorAuthenticate;
import com.googlecode.torcontrol.command.TorSignal;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TorInstance extends Thread {

    private final Logger logger;
    private final int torId;
    private final int proxyPort;
    private final int controlPort;
    private final TorControl torControl;

    private Process torProcess;
    private boolean stop = false;
    private int useCount = 0;
    private long rateLimitDelay = 0;
    private boolean started = false;


    public TorInstance(int torId) {
        this.torId = torId;
        logger = Logger.getLogger("Tor-Instance-" + torId);

        proxyPort = 7060 + torId * 2;
        controlPort = proxyPort + 1;

        torControl = new TorControl("127.0.0.1", controlPort, null);
    }

    @Override
    public void run() {
        logger.info("Starting tor on port " + proxyPort + "...");
        try {

            File dataDirectory = new File("tor-" + torId + "/");
            //Create directory if not existing
            if (!dataDirectory.exists())
                //noinspection ResultOfMethodCallIgnored
                dataDirectory.mkdir();

            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("SOCKSListenAddress", "127.0.0.1:" + proxyPort));
            params.add(new BasicNameValuePair("ControlPort", Integer.toString(controlPort)));
            params.add(new BasicNameValuePair("DataDirectory", dataDirectory.getCanonicalPath()));

            File torc = new File("tor-" + torId + "/torc");
            if (!torc.exists()) //noinspection ResultOfMethodCallIgnored
                torc.createNewFile();
            PrintWriter printWriter = new PrintWriter(new FileWriter(torc, false));
            for (NameValuePair param : params) {
                printWriter.println(param.getName() + " " + param.getValue());
            }
            printWriter.flush();
            printWriter.close();

            String command = "tor -f " + torc.getCanonicalPath();
            logger.info("Command: " + command);

            torProcess = Runtime.getRuntime().exec(command);

            Scanner scanner = new Scanner(torProcess.getInputStream());
            while (!stop) {
                try {
                    String msg = scanner.nextLine();
                    if (msg.contains("100%")) {
                        started = true;
                        try {
                            torControl.connect();
                            logger.fine(torControl.executeCommand(new TorAuthenticate()));
                        } catch (Exception ignored) {

                        }
                    } else if (msg.contains("Socks4A")) {
                        //Don't write out socks5 warnings
                        continue;
                    }

                    //Rate limit
                    Matcher matcher = Pattern.compile("^.*Rate limiting NEWNYM request: delaying by (\\d+) second\\(s\\).*").matcher(msg);
                    if (matcher.find()) {
                        rateLimitDelay = System.currentTimeMillis() + Long.parseLong(matcher.group(1)) * 1000;
                    }

                    logger.info(msg);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }

            scanner.close();
            torProcess.destroy();
        } catch (Exception e) {
            e.printStackTrace();
            assert torProcess != null;
            torProcess.destroyForcibly();
        }
    }

    public void newCicuit() {
        try {
            logger.info("Switching to a new circuit...");
            torControl.connect();
            logger.fine(torControl.executeCommand(new TorAuthenticate()));
            logger.fine(torControl.executeCommand(TorSignal.newClearDNSCache()));
            logger.fine(torControl.executeCommand(TorSignal.newNewNym()));
            useCount = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRateLimited() {
        return System.currentTimeMillis() < rateLimitDelay;
    }

    @Override
    public void interrupt() {
        if (torProcess != null) {
            torProcess.destroyForcibly();
        }
    }

    public void incrementUseCount() {
        if (++useCount >= 2) {
            newCicuit();
        }
    }

    public void setStop(boolean stop) {
        this.stop = stop;
        torProcess.destroy();
    }

    public int getTorId() {
        return torId;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public int getControlPort() {
        return controlPort;
    }

    public boolean isStarted() {
        return started;
    }
}