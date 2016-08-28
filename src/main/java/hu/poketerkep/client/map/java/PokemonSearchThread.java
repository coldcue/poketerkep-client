package hu.poketerkep.client.map.java;

import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import hu.poketerkep.client.map.java.geo.Coordinate;
import hu.poketerkep.client.map.java.geo.LocationGenerator;
import hu.poketerkep.client.model.UserConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class PokemonSearchThread extends Thread {

    private final PokemonMapInstance mapInstance;
    private final HashSet<PokemonSearchWorker> workers;

    public PokemonSearchThread(PokemonMapInstance mapInstance) {
        this.mapInstance = mapInstance;
        workers = new HashSet<>();
    }

    @Override
    public void run() {
        List<UserConfig> users = mapInstance.getConfiguration().getUsers();

        for (UserConfig userConfig : users) {
            PokemonSearchWorker worker = PokemonSearchWorker.withUserConfig(userConfig);

            Optional<Integer> proxyPort = mapInstance.getConfiguration().getProxyPort();
            if (proxyPort.isPresent()) {
                worker.withProxy(proxyPort.get());
            }

            try {
                worker.connect();
            } catch (RemoteServerException | LoginFailedException e) {
                e.printStackTrace();
            }
        }

        while (true) {
            LocationGenerator locationGenerator = new LocationGenerator(mapInstance.getConfiguration().getLocation());
            Queue<Coordinate> coordinates = new LinkedBlockingQueue<>(locationGenerator.generateSteps());

            while (!coordinates.isEmpty()) {
                Runnable runnable = () -> {

                };
            }
        }
    }
}
