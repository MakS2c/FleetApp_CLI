package fleet.model;

public final class Truck extends Vehicle implements Maintainable, Insurable {
    private double loadCapacity;
    private int axles;

    public Truck(String id, String brand, String model, int year, String color,
                 int price, double loadCapacity, int axles) {
        super(id, brand, model, year, color, price);
        setLoadCapacity(loadCapacity);
        setAxles(axles);
    }

    public double getLoadCapacity() {
        return loadCapacity;
    }

    public void setLoadCapacity(double loadCapacity) {
        if (loadCapacity <= 0) {
            throw new IllegalArgumentException("Грузоподъёмность должна быть больше нуля");
        }
        this.loadCapacity = loadCapacity;
    }

    public int getAxles() {
        return axles;
    }

    public void setAxles(int axles) {
        if (axles < 2) {
            throw new IllegalArgumentException("У грузовика должно быть минимум 2 оси");
        }
        this.axles = axles;
    }

    public boolean canCarry(double weightTons) {
        if (weightTons < 0) {
            throw new IllegalArgumentException("Вес груза не может быть отрицательным");
        }
        return weightTons <= loadCapacity;
    }

    public void loadCargo(double weightTons) {
        if (!canCarry(weightTons)) {
            throw new IllegalArgumentException("Перегруз. Максимум: " + loadCapacity + " т");
        }
        System.out.println("Загружено " + weightTons + " т в " + getBrand()
                + " " + getModel());
    }

    public double calculateFreightCost(double km, double ratePerTonKm) {
        if (km < 0 || ratePerTonKm < 0) {
            throw new IllegalArgumentException("Расстояние и ставка не могут быть отрицательными");
        }
        return km * loadCapacity * ratePerTonKm;
    }

    @Override
    public double calculateRentalCost(int days) {
        checkDays(days);
        return days * 100.0 + loadCapacity * 10;
    }

    @Override
    public String getVehicleType() {
        return "Truck";
    }

    @Override
    public void performMaintenance() {
        System.out.println(getBrand() + " " + getModel()
                + ": проверка двигателя, тормозов и осей");
    }

    @Override
    public int getServiceIntervalKm() {
        return 20000;
    }

    @Override
    public double calculateInsurance() {
        return getPrice() * 0.03 + loadCapacity * 100;
    }

    @Override
    public String toString() {
        return super.toString() + " | груз: " + loadCapacity
                + " т | оси: " + axles;
    }
}
