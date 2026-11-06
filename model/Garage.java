package fleet.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Garage {
    private final String name;
    private final Set<Vehicle> vehicles = new LinkedHashSet<>();

    public Garage(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название гаража не может быть пустым");
        }
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }

    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Машина не может быть пустой");
        }
        return vehicles.add(vehicle);
    }

    public boolean removeVehicle(Vehicle vehicle) {
        return vehicles.remove(vehicle);
    }

    public boolean contains(Vehicle vehicle) {
        return vehicles.contains(vehicle);
    }

    public int size() {
        return vehicles.size();
    }

    public int totalValue() {
        int total = 0;
        for (Vehicle vehicle : vehicles) {
            total = total + vehicle.getPrice();
        }
        return total;
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(new ArrayList<>(vehicles));
    }

    public void showVehicles() {
        System.out.println("Гараж: " + name + " (" + vehicles.size() + " машин)");
        for (Vehicle vehicle : vehicles) {
            System.out.println("- " + vehicle);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Garage garage = (Garage) other;
        return name.equals(garage.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Garage | " + name + " | машин: " + vehicles.size();
    }
}
