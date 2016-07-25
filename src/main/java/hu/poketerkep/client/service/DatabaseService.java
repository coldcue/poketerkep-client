package hu.poketerkep.client.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import hu.poketerkep.client.mapper.PokemonMapper;
import hu.poketerkep.client.model.Pokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DatabaseService {

    private final AmazonDynamoDBAsync dynamoDBAsync;

    @Autowired
    public DatabaseService(AmazonDynamoDBAsync dynamoDBAsync) {
        this.dynamoDBAsync = dynamoDBAsync;
    }

    /**
     * Put pokemons into the database
     *
     * @param pokemons the list of pokemons
     */
    public void putPokemons(List<Pokemon> pokemons) {
        pokemons.parallelStream().map(PokemonMapper::mapToDynamoDb).forEach(valueMap -> dynamoDBAsync.putItem("pokemons", valueMap));
    }

}