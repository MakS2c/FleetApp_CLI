package fleet.model;

public enum RequestPriority {
    HIGH(1),
    NORMAL(2),
    LOW(3);

    private final int weight;

    RequestPriority(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
