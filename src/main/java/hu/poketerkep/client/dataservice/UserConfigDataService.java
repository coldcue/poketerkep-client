package hu.poketerkep.client.dataservice;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import hu.poketerkep.client.config.Constants;
import hu.poketerkep.client.mapper.UserConfigMapper;
import hu.poketerkep.client.model.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UserConfigDataService {
    private static final String USER_CONFIG_TABLE = "UserConfig";
    private static final String USER_CONFIG_KEY = "userName";

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final AmazonDynamoDBAsync dynamoDBAsync;

    @Autowired
    public UserConfigDataService(AmazonDynamoDBAsync dynamoDBAsync) {
        this.dynamoDBAsync = dynamoDBAsync;
    }

    public List<UserConfig> getUnusedUsers(int num) {
        // Now -900 sec
        long time = Instant.now().minusSeconds(Constants.UNUSED_USER_TIME_SECONDS).toEpochMilli();

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":time", new AttributeValue().withN(Long.toString(time)));
        expressionAttributeValues.put(":banned", new AttributeValue().withBOOL(false));

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(USER_CONFIG_TABLE)
                .withFilterExpression("lastUsed < :time and banned = :banned")
                .withExpressionAttributeValues(expressionAttributeValues);

        ScanResult result = dynamoDBAsync.scan(scanRequest);

        // If there are not enough results
        if (result.getCount() < num) {
            return Collections.emptyList();
        }

        return result.getItems().stream()
                .limit(num)
                .map(UserConfigMapper::mapFromDynamoDB)
                .collect(Collectors.toList());
    }

    public void updateLastUsed(UserConfig userConfig) {
        long time = Instant.now().toEpochMilli();

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":time", new AttributeValue().withN(Long.toString(time)));

        Map<String, AttributeValue> key = new HashMap<>();
        key.put(USER_CONFIG_KEY, new AttributeValue().withS(userConfig.getUserName()));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(USER_CONFIG_TABLE)
                .withKey(key)
                .withUpdateExpression("set lastUsed = :time")
                .withExpressionAttributeValues(expressionAttributeValues);

        logger.finer("Updating last use value for user " + userConfig.getUserName() + " to " + time);
        userConfig.setLastUsed(time);
        dynamoDBAsync.updateItemAsync(updateItemRequest);
    }

    public void setBanned(UserConfig userConfig) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":banned", new AttributeValue().withBOOL(true));


        Map<String, AttributeValue> key = new HashMap<>();
        key.put(USER_CONFIG_KEY, new AttributeValue().withS(userConfig.getUserName()));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(USER_CONFIG_TABLE)
                .withKey(key)
                .withUpdateExpression("set banned = :banned")
                .withExpressionAttributeValues(expressionAttributeValues);


        logger.finer("Banning user " + userConfig.getUserName());
        userConfig.setBanned(true);
        dynamoDBAsync.updateItem(updateItemRequest);
    }

    public void releaseUser(String userName) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":zero", new AttributeValue().withN(String.valueOf(0)));

        Map<String, AttributeValue> key = new HashMap<>();
        key.put(USER_CONFIG_KEY, new AttributeValue().withS(userName));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(USER_CONFIG_TABLE)
                .withKey(key)
                .withUpdateExpression("set lastUsed = :zero")
                .withExpressionAttributeValues(expressionAttributeValues);


        logger.finer("Releasing user " + userName);

        dynamoDBAsync.updateItem(updateItemRequest);
    }
}
