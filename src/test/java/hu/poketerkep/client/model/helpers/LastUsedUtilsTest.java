package hu.poketerkep.client.model.helpers;

import hu.poketerkep.client.model.UserConfig;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LastUsedUtilsTest {

    @Test
    public void isAfter() throws Exception {
        // Create users that are implementing LastUsed

        UserConfig lu10 = new UserConfig("test1", (long) 10, false);
        UserConfig luNull = new UserConfig("test1", null, false);

        assertFalse(LastUsedUtils.isAfter(lu10, 15));
        assertTrue(LastUsedUtils.isAfter(lu10, 10));
        assertTrue(LastUsedUtils.isAfter(lu10, 5));

        assertFalse(LastUsedUtils.isAfter(luNull, 10));
    }

    @Test
    public void isBefore() throws Exception {
        // Create users that are implementing LastUsed

        UserConfig lu10 = new UserConfig("test1", (long) 10, false);
        UserConfig luNull = new UserConfig("test1", null, false);

        assertTrue(LastUsedUtils.isBefore(lu10, 15));
        assertFalse(LastUsedUtils.isBefore(lu10, 10));
        assertFalse(LastUsedUtils.isBefore(lu10, 5));

        assertFalse(LastUsedUtils.isBefore(luNull, 10));
    }
}