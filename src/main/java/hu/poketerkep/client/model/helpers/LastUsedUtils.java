package hu.poketerkep.client.model.helpers;

/**
 * Utils class for last used interfac
 */
public class LastUsedUtils {
    public static boolean isAfter(LastUsed object, long time) {
        return object.getLastUsed() != null && object.getLastUsed() > time;
    }
}
