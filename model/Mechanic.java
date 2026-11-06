package fleet.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mechanic extends User {
    private final String specialization;
    private final List<Vehicle> servicedVehicles = new ArrayList<>();

    public Mechanic(String id, String name, String specialization) {
        super(id, name);
        if (specialization == null || specialization.isBlank()) {
            throw new IllegalArgumentException("Специализация не может быть пустой");
        }
        this.specialization = specialization.trim();
    }

    public String getSpecialization() {
        return specialization;
    }

    public void service(Maintainable maintainable) {
        if (maintainable == null) {
            throw new IllegalArgumentException("Машина не может быть пустой");
        }
        System.out.println("Механик " + getName() + " (" + specialization + ") обслуживает:");
        maintainable.performMaintenance();
        System.out.println("Следующее обслуживание через "
                + maintainable.getServiceIntervalKm() + " км");

        if (maintainable instanceof Vehicle) {
            Vehicle vehicle = (Vehicle) maintainable;
            servicedVehicles.add(vehicle);
        }
    }

    public List<Vehicle> getServicedVehicles() {
        return Collections.unmodifiableList(new ArrayList<>(servicedVehicles));
    }

    @Override
    public String getRole() {
        return "Mechanic";
    }
}
