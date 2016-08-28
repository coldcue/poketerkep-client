package hu.poketerkep.client.dataservice;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import hu.poketerkep.client.mapper.PokemonMapper;
import hu.poketerkep.shared.model.Pokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class PokemonDataService {

    private static final String POKEMONS_TABLE = "pokemons";
    private static final String POKEMON_TABLE_KEY = "encounterId";
    private final AmazonDynamoDBAsync dynamoDBAsync;

    @Autowired
    public PokemonDataService(AmazonDynamoDBAsync dynamoDBAsync) {
        this.dynamoDBAsync = dynamoDBAsync;
    }

    /**
     * Put pokemons into the database
     *
     * @param pokemons the list of pokemons
     */
    public void putPokemons(List<Pokemon> pokemons) {
        pokemons.stream()
                .map(PokemonMapper::mapToDynamoDb)
                .forEach(valueMap -> dynamoDBAsync.putItemAsync(POKEMONS_TABLE, valueMap));
    }

    public List<Pokemon> getOldPokemons() {
        // http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/ScanJavaDocumentAPI.html
        long now = Instant.now().toEpochMilli();

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":now", new AttributeValue().withN(Long.toString(now)));

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(POKEMONS_TABLE)
                .withFilterExpression("disappearTime < :now")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withProjectionExpression("encounterId, disappearTime");

        ScanResult result = dynamoDBAsync.scan(scanRequest);

        return result.getItems().parallelStream()
                .map(valueMap -> {
                    Pokemon pokemon = new Pokemon();
                    pokemon.setEncounterId(valueMap.get("encounterId").getS());
                    pokemon.setDisappearTime(Long.parseLong(valueMap.get("disappearTime").getN()));
                    return pokemon;
                })
                .collect(Collectors.toList());
    }

    public void deletePokemon(Pokemon pokemon) {
        Map<String, AttributeValue> hashKey = new HashMap<>();
        hashKey.put(POKEMON_TABLE_KEY, new AttributeValue().withS(pokemon.getEncounterId()));

        DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
                .withTableName(POKEMONS_TABLE)
                .withKey(hashKey);

        dynamoDBAsync.deleteItemAsync(deleteItemRequest);
    }

}
