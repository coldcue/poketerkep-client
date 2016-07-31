package hu.poketerkep.client.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import hu.poketerkep.client.mapper.UserConfigMapper;
import hu.poketerkep.client.model.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserConfigDataService {
    private static final String USER_CONFIG_TABLE = "UserConfig";
    private static final String USER_CONFIG_KEY = "userName";

    private final AmazonDynamoDBAsync dynamoDBAsync;

    @Autowired
    public UserConfigDataService(AmazonDynamoDBAsync dynamoDBAsync) {
        this.dynamoDBAsync = dynamoDBAsync;
    }

    public UserConfig getUnusedUser() {
        // Now +90 sec
        long time = Instant.now().plusSeconds(90).toEpochMilli();

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":time", new AttributeValue().withN(Long.toString(time)));

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(USER_CONFIG_TABLE)
                .withFilterExpression("lastUsed < :time")
                .withExpressionAttributeValues(expressionAttributeValues);

        ScanResult result = dynamoDBAsync.scan(scanRequest);

        // If there are no results
        if (result.getCount() == 0) {
            return null;
        }

        // Get the first
        Map<String, AttributeValue> valueMap = result.getItems().get(0);


        return UserConfigMapper.mapFromDynamoDB(valueMap);
    }

    public void updateUserLastUsed(String userName) {
        long now = Instant.now().toEpochMilli();

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":now", new AttributeValue().withN(Long.toString(now)));

        Map<String, AttributeValue> key = new HashMap<>();
        key.put(USER_CONFIG_KEY, new AttributeValue().withS(userName));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(USER_CONFIG_TABLE)
                .withKey(key)
                .withUpdateExpression("set lastUsed = :now")
                .withExpressionAttributeValues(expressionAttributeValues);

        dynamoDBAsync.updateItemAsync(updateItemRequest);
    }
}