package fleet.storage;

import fleet.model.Car;
import fleet.model.Client;
import fleet.model.Engine;
import fleet.model.Motorcycle;
import fleet.model.Truck;
import fleet.model.Vehicle;
import fleet.service.FleetService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FleetFileStorage {
    private final Path file;

    public FleetFileStorage(String fileName) {
        this.file = Path.of(fileName);
    }

    public Path getFile() {
        return file;
    }

    public boolean exists() {
        return Files.exists(file);
    }

    public void save(FleetService service) throws IOException {
        List<Vehicle> vehicles = service.getAllVehicles();
        List<Client> clients = service.getAllClients();

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write("VEHICLES");
            writer.newLine();

            for (Vehicle vehicle : vehicles) {
                if (vehicle instanceof Car) {
                    Car car = (Car) vehicle;
                    writer.write("CAR|" + car.getId()
                            + "|" + car.getBrand()
                            + "|" + car.getModel()
                            + "|" + car.getYear()
                            + "|" + car.getColor()
                            + "|" + car.getPrice()
                            + "|" + car.getDoors()
                            + "|" + car.getFuelConsumption()
                            + "|" + car.getEngine().getHorsePower()
                            + "|" + car.getEngine().getType());
                    writer.newLine();
                } else if (vehicle instanceof Truck) {
                    Truck truck = (Truck) vehicle;
                    writer.write("TRUCK|" + truck.getId()
                            + "|" + truck.getBrand()
                            + "|" + truck.getModel()
                            + "|" + truck.getYear()
                            + "|" + truck.getColor()
                            + "|" + truck.getPrice()
                            + "|" + truck.getLoadCapacity()
                            + "|" + truck.getAxles());
                    writer.newLine();
                } else if (vehicle instanceof Motorcycle) {
                    Motorcycle motorcycle = (Motorcycle) vehicle;
                    writer.write("MOTO|" + motorcycle.getId()
                            + "|" + motorcycle.getBrand()
                            + "|" + motorcycle.getModel()
                            + "|" + motorcycle.getYear()
                            + "|" + motorcycle.getColor()
                            + "|" + motorcycle.getPrice()
                            + "|" + motorcycle.getEngineVolume()
                            + "|" + motorcycle.hasSidecar());
                    writer.newLine();
                }
            }

            writer.write("CLIENTS");
            writer.newLine();

            for (Client client : clients) {
                writer.write(client.getId() + "|" + client.getName());
                writer.newLine();
            }
        }
    }

    public FleetService load() throws IOException {
        FleetService service = new FleetService();

        if (!Files.exists(file)) {
            return service;
        }

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String section = "";
            String line = reader.readLine();

            while (line != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    line = reader.readLine();
                    continue;
                }

                if (line.equals("VEHICLES") || line.equals("CLIENTS")) {
                    section = line;
                    line = reader.readLine();
                    continue;
                }

                if (section.equals("VEHICLES")) {
                    service.addVehicle(readVehicle(line));
                } else if (section.equals("CLIENTS")) {
                    String[] parts = split(line, 2);
                    service.addClient(new Client(parts[0], parts[1]));
                }

                line = reader.readLine();
            }
        }

        return service;
    }

    private Vehicle readVehicle(String line) throws IOException {
        if (line.startsWith("CAR|")) {
            String[] parts = split(line, 11);
            return new Car(parts[1], parts[2], parts[3],
                    Integer.parseInt(parts[4]), parts[5],
                    Integer.parseInt(parts[6]), Integer.parseInt(parts[7]),
                    Double.parseDouble(parts[8]),
                    new Engine(Integer.parseInt(parts[9]), parts[10]));
        }

        if (line.startsWith("TRUCK|")) {
            String[] parts = split(line, 9);
            return new Truck(parts[1], parts[2], parts[3],
                    Integer.parseInt(parts[4]), parts[5],
                    Integer.parseInt(parts[6]), Double.parseDouble(parts[7]),
                    Integer.parseInt(parts[8]));
        }

        if (line.startsWith("MOTO|")) {
            String[] parts = split(line, 9);
            return new Motorcycle(parts[1], parts[2], parts[3],
                    Integer.parseInt(parts[4]), parts[5],
                    Integer.parseInt(parts[6]), Integer.parseInt(parts[7]),
                    Boolean.parseBoolean(parts[8]));
        }

        throw new IOException("Неизвестная строка машины: " + line);
    }

    private String[] split(String line, int expectedParts) throws IOException {
        String[] parts = line.split("\\|");

        if (parts.length != expectedParts) {
            throw new IOException("Ошибка в строке файла: " + line);
        }

        return parts;
    }
}
