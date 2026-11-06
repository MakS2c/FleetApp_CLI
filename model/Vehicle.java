package fleet.model;

public abstract class Vehicle implements Comparable<Vehicle> {
    private final String id;
    private String brand;
    private String model;
    private int year;
    private String color;
    private int price;
    private int speed;
    private VehicleStatus status;

    protected Vehicle(String id, String brand, String model, int year,
                      String color, int price) {
        this.id = checkText(id, "Id машины");
        this.status = VehicleStatus.AVAILABLE;
        setBrand(brand);
        setModel(model);
        setYear(year);
        setColor(color);
        setPrice(price);
    }

    public final String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public final void setBrand(String brand) {
        this.brand = checkText(brand, "Марка");
    }

    public String getModel() {
        return model;
    }

    public final void setModel(String model) {
        this.model = checkText(model, "Модель");
    }

    public int getYear() {
        return year;
    }

    public final void setYear(int year) {
        if (year < 1900) {
            throw new IllegalArgumentException("Год выпуска не может быть меньше 1900");
        }
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public final void setColor(String color) {
        this.color = checkText(color, "Цвет");
    }

    public int getPrice() {
        return price;
    }

    public final void setPrice(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }
        this.price = price;
    }

    public int getSpeed() {
        return speed;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public final void setStatus(VehicleStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Статус не может быть пустым");
        }
        this.status = status;
    }

    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    public void accelerate(int increment) {
        if (increment < 0) {
            throw new IllegalArgumentException("Ускорение не может быть отрицательным");
        }
        speed = speed + increment;
        System.out.println(brand + " " + model + ": скорость " + speed + " км/ч");
    }

    public void displayInfo() {
        System.out.println(this);
    }

    public abstract double calculateRentalCost(int days);

    public abstract String getVehicleType();

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vehicle)) {
            return false;
        }
        Vehicle vehicle = (Vehicle) other;
        return id.equals(vehicle.id);
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }

    @Override
    public final int compareTo(Vehicle other) {
        return id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return getVehicleType() + " | " + id + " | " + brand + " " + model
                + " | " + year + " | " + price + " | " + status;
    }

    protected static String checkText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " не может быть пустым");
        }
        return value.trim();
    }

    protected static void checkDays(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Количество дней должно быть больше нуля");
        }
    }
}
