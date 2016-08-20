package hu.poketerkep.client.map;

import hu.poketerkep.client.model.AllData;

import java.io.IOException;

public interface MapInstance {
    void check();

    boolean isShouldBeStopped();

    MapConfiguration getConfiguration();

    String getInstanceName();

    int getInstanceId();

    void start() throws IOException;

    void stop();

    AllData getNewAllData();
}
