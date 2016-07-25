package hu.poketerkep.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import hu.poketerkep.model.Pokemon;
import hu.poketerkep.mapper.PokemonMapper;
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
        pokemons.parallelStream().map(PokemonMapper::mapToDynamoDb).forEach(valueMap -> dynamoDBAsync.putItemAsync("pokemons", valueMap));
    }

}
