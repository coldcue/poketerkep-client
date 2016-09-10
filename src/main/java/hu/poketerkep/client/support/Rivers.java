package hu.poketerkep.client.support;


public class Rivers {
    private final int increaseStep;
    private final int decreaseStep;
    private final int limit;
    private int value = 0;

    public Rivers(int increaseStep, int decreaseStep, int limit) {
        this.increaseStep = increaseStep;
        this.decreaseStep = decreaseStep;
        this.limit = limit;
    }

    public void reset() {
        this.value = 0;
    }

    public boolean increase() {
        int newValue = value + increaseStep;

        if (newValue >= limit) {
            value = limit;
            return true;
        } else {
            value = newValue;
            return false;
        }
    }

    public void decrease() {
        int newValue = value - decreaseStep;

        if (newValue < 0) {
            value = 0;
        } else {
            value = newValue;
        }
    }
}
