package hu.poketerkep.client.support;

import hu.poketerkep.shared.model.UserConfig;
import org.apache.commons.codec.digest.DigestUtils;


public class UserConfigHelper {
    public static String getPassword(UserConfig user) {
        return DigestUtils.md5Hex(user.getUserName()).substring(0, 8);
    }
}
