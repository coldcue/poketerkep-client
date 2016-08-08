package hu.poketerkep.client.pokemonGoMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PokemonGoMapInstanceLogReader extends Thread {

    private final PokemonGoMapInstance instance;
    private final InputStream inputStream;

    public PokemonGoMapInstanceLogReader(PokemonGoMapInstance instance, InputStream inputStream) {
        this.instance = instance;
        this.inputStream = inputStream;
    }

    @Override
    public void run() {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null && !isInterrupted()) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
