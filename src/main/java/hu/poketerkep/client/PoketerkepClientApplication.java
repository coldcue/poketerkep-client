package hu.poketerkep.client;

import hu.poketerkep.client.pokemonGoMap.instance.PGMInstance;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class PoketerkepClientApplication {

    public static void main(String[] args) throws Exception {
        check();
        ConfigurableApplicationContext ctx = SpringApplication.run(PoketerkepClientApplication.class, args);

        //Run forever
        new CountDownLatch(1).await();
    }

    //Check everything before running
    private static void check() throws Exception {
        if (!PGMInstance.DIR.isDirectory()) throw new Exception("No PokemonGo-Map present");
    }
}
