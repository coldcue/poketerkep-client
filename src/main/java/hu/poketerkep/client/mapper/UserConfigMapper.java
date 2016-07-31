package hu.poketerkep.client.mapper;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import hu.poketerkep.client.model.UserConfig;

import java.util.Map;

public class UserConfigMapper {

    public static UserConfig mapFromDynamoDB(Map<String, AttributeValue> valueMap) {
        UserConfig userConfig = new UserConfig();

        userConfig.setUserName(valueMap.get("userName").getS());
        userConfig.setLastUsed(Long.valueOf(valueMap.get("lastUsed").getN()));
        userConfig.setBanned(valueMap.get("banned").getBOOL());

        return userConfig;
    }
}
