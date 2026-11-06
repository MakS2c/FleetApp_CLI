package fleet.model;

public final class Motorcycle extends Vehicle implements Insurable {
    private int engineVolume;
    private boolean hasSidecar;

    public Motorcycle(String id, String brand, String model, int year, String color,
                      int price, int engineVolume, boolean hasSidecar) {
        super(id, brand, model, year, color, price);
        setEngineVolume(engineVolume);
        this.hasSidecar = hasSidecar;
    }

    public int getEngineVolume() {
        return engineVolume;
    }

    public void setEngineVolume(int engineVolume) {
        if (engineVolume < 50) {
            throw new IllegalArgumentException("Объём двигателя должен быть не меньше 50 см3");
        }
        this.engineVolume = engineVolume;
    }

    public boolean hasSidecar() {
        return hasSidecar;
    }

    public void setHasSidecar(boolean hasSidecar) {
        this.hasSidecar = hasSidecar;
    }

    public boolean isSportBike() {
        return engineVolume >= 600 && !hasSidecar;
    }

    public double calculateTax() {
        return engineVolume * 0.05;
    }

    public void rev() {
        System.out.println(getBrand() + " " + getModel() + ": врррум!");
    }

    @Override
    public double calculateRentalCost(int days) {
        checkDays(days);
        return days * 30.0;
    }

    @Override
    public String getVehicleType() {
        return "Motorcycle";
    }

    @Override
    public double calculateInsurance() {
        return getPrice() * 0.08;
    }

    @Override
    public String toString() {
        return super.toString() + " | объём: " + engineVolume
                + " см3 | коляска: " + (hasSidecar ? "да" : "нет");
    }
}
