package hu.poketerkep.client.dataservice;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import hu.poketerkep.client.mapper.LocationConfigMapper;
import hu.poketerkep.client.model.LocationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class LocationConfigDataService {
    private static final String LOCATION_CONFIG_TABLE = "LocationConfig";
    private static final String LOCATION_CONFIG_KEY = "locationId";

    private final AmazonDynamoDBAsync dynamoDBAsync;

    @Autowired
    public LocationConfigDataService(AmazonDynamoDBAsync dynamoDBAsync) {
        this.dynamoDBAsync = dynamoDBAsync;
    }

    public LocationConfig getUnusedLocation() {
        // Now +90 sec
        long time = Instant.now().minusSeconds(90).toEpochMilli();

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":time", new AttributeValue().withN(Long.toString(time)));

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(LOCATION_CONFIG_TABLE)
                .withFilterExpression("lastUsed < :time")
                .withExpressionAttributeValues(expressionAttributeValues);

        ScanResult result = dynamoDBAsync.scan(scanRequest);

        // If there are no results
        if (result.getCount() == 0) {
            return null;
        }

        // Get the first
        Map<String, AttributeValue> valueMap = result.getItems().get(0);
        return LocationConfigMapper.mapFromDynamoDB(valueMap);
    }

    public void updateLocationLastUsed(LocationConfig locationConfig) {
        long now = Instant.now().toEpochMilli();

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":now", new AttributeValue().withN(Long.toString(now)));

        Map<String, AttributeValue> key = new HashMap<>();
        key.put(LOCATION_CONFIG_KEY, new AttributeValue().withS(locationConfig.getLocationId()));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(LOCATION_CONFIG_TABLE)
                .withKey(key)
                .withUpdateExpression("set lastUsed = :now")
                .withExpressionAttributeValues(expressionAttributeValues);

        dynamoDBAsync.updateItem(updateItemRequest);
    }

    public void releaseLocation(LocationConfig locationConfig) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":zero", new AttributeValue().withN(Long.toString(0)));

        Map<String, AttributeValue> key = new HashMap<>();
        key.put(LOCATION_CONFIG_KEY, new AttributeValue().withS(locationConfig.getLocationId()));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(LOCATION_CONFIG_TABLE)
                .withKey(key)
                .withUpdateExpression("set lastUsed = :zero")
                .withExpressionAttributeValues(expressionAttributeValues);

        dynamoDBAsync.updateItem(updateItemRequest);
    }
}
