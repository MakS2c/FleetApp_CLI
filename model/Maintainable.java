package fleet.model;

public interface Maintainable {
    void performMaintenance();

    int getServiceIntervalKm();
}
