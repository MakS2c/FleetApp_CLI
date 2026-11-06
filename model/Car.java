package fleet.model;

public final class Car extends Vehicle implements Maintainable, Insurable {
    private int doors;
    private double fuelConsumption;
    private Engine engine;

    public Car(String id, String brand, String model, int year, String color,
               int price, int doors, double fuelConsumption) {
        this(id, brand, model, year, color, price, doors, fuelConsumption,
                new Engine(150, "Бензин"));
    }

    public Car(String id, String brand, String model, int year, String color,
               int price, int doors, double fuelConsumption, Engine engine) {
        super(id, brand, model, year, color, price);
        setDoors(doors);
        setFuelConsumption(fuelConsumption);
        setEngine(engine);
    }

    public int getDoors() {
        return doors;
    }

    public void setDoors(int doors) {
        if (doors < 2 || doors > 5) {
            throw new IllegalArgumentException("У автомобиля должно быть от 2 до 5 дверей");
        }
        this.doors = doors;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(double fuelConsumption) {
        if (fuelConsumption <= 0) {
            throw new IllegalArgumentException("Расход топлива должен быть больше нуля");
        }
        this.fuelConsumption = fuelConsumption;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        if (engine == null) {
            throw new IllegalArgumentException("Двигатель не может быть пустым");
        }
        this.engine = engine;
    }

    public void startEngine() {
        engine.start();
    }

    public boolean isFamilyCar() {
        return doors >= 4;
    }

    public double calculateTripCost(double km, double fuelPricePerLiter) {
        if (km < 0 || fuelPricePerLiter < 0) {
            throw new IllegalArgumentException("Расстояние и цена топлива не могут быть отрицательными");
        }
        return km / 100.0 * fuelConsumption * fuelPricePerLiter;
    }

    public void honk() {
        System.out.println(getBrand() + " " + getModel() + ": бип-бип!");
    }

    @Override
    public double calculateRentalCost(int days) {
        checkDays(days);
        return days * 50.0;
    }

    @Override
    public String getVehicleType() {
        return "Car";
    }

    @Override
    public void performMaintenance() {
        System.out.println(getBrand() + " " + getModel()
                + ": замена масла, проверка тормозов и двигателя");
    }

    @Override
    public int getServiceIntervalKm() {
        return 10000;
    }

    @Override
    public double calculateInsurance() {
        return getPrice() * 0.05;
    }

    @Override
    public String toString() {
        return super.toString() + " | двери: " + doors
                + " | расход: " + fuelConsumption
                + " | двигатель: " + engine;
    }
}
