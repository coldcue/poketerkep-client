package hu.poketerkep.client.map.scanner;


import hu.poketerkep.client.support.Rivers;

class MapScannerHealth {
    private final MapScannerInstance mapScannerInstance;
    private Rivers errorRivers = new Rivers(1, 5, 10);

    MapScannerHealth(MapScannerInstance mapScannerInstance) {
        this.mapScannerInstance = mapScannerInstance;
    }

    void onError() {
        boolean limitReached = errorRivers.increase();

        if (limitReached) {
            mapScannerInstance.setNextUserNeeded(true);
        }
    }

    void onSuccess() {
        errorRivers.decrease();
    }
}
