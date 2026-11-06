package fleet.service;

import fleet.model.Client;
import fleet.model.Garage;
import fleet.model.Maintainable;
import fleet.model.Mechanic;
import fleet.model.RentalRequest;
import fleet.model.RequestPriority;
import fleet.model.RequestStatus;
import fleet.model.Vehicle;
import fleet.model.VehicleStatus;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

public class FleetService {
    private final Garage garage = new Garage("Центральный гараж");

    private final List<Vehicle> vehicles = new ArrayList<>();
    private final Map<String, Vehicle> vehicleById = new HashMap<>();
    private final Map<String, List<Vehicle>> vehiclesByType = new HashMap<>();
    private final Map<VehicleStatus, List<Vehicle>> vehiclesByStatus =
            new EnumMap<>(VehicleStatus.class);

    private final Map<String, Client> clientsById = new LinkedHashMap<>();
    private final List<RentalRequest> requests = new ArrayList<>();
    private final Map<String, RentalRequest> requestById = new HashMap<>();
    private final Map<String, List<RentalRequest>> requestsByClient = new HashMap<>();
    private final Map<String, List<RentalRequest>> requestsByVehicle = new HashMap<>();
    private final Map<String, RentalRequest> activeRentals = new HashMap<>();
    private final Queue<RentalRequest> requestQueue = new PriorityQueue<>();

    private final Queue<Vehicle> maintenanceQueue = new ArrayDeque<>();
    private final Set<String> scheduledMaintenance = new HashSet<>();

    private long requestSequence;

    public FleetService() {
        vehiclesByStatus.put(VehicleStatus.AVAILABLE, new ArrayList<>());
        vehiclesByStatus.put(VehicleStatus.RENTED, new ArrayList<>());
        vehiclesByStatus.put(VehicleStatus.IN_SERVICE, new ArrayList<>());
    }

    public synchronized void addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Машина не может быть пустой");
        }
        if (vehicleById.containsKey(vehicle.getId())) {
            throw new IllegalArgumentException("Машина с id " + vehicle.getId() + " уже есть");
        }
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new IllegalArgumentException("Новая машина должна быть доступна");
        }

        vehicles.add(vehicle);
        vehicleById.put(vehicle.getId(), vehicle);
        addToTypeIndex(vehicle);
        addToStatusIndex(vehicle);
        garage.addVehicle(vehicle);
    }

    public synchronized void updateVehicle(Vehicle updated) {
        if (updated == null) {
            throw new IllegalArgumentException("Машина не может быть пустой");
        }

        Vehicle old = findVehicleOrThrow(updated.getId());
        if (old.getStatus() != VehicleStatus.AVAILABLE) {
            throw new IllegalStateException("Нельзя изменить арендованную или обслуживаемую машину");
        }
        if (updated.getStatus() != VehicleStatus.AVAILABLE) {
            throw new IllegalStateException("Статусом машины управляет сервис");
        }

        int index = vehicles.indexOf(old);
        vehicles.set(index, updated);
        vehicleById.put(updated.getId(), updated);
        garage.removeVehicle(old);
        garage.addVehicle(updated);
        rebuildVehicleIndexes();
    }

    public synchronized void removeVehicle(String vehicleId) {
        Vehicle vehicle = findVehicleOrThrow(vehicleId);

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new IllegalStateException("Нельзя удалить арендованную или обслуживаемую машину");
        }
        if (hasOpenRequests(vehicleId)) {
            throw new IllegalStateException("У машины есть открытые заявки");
        }

        maintenanceQueue.remove(vehicle);
        scheduledMaintenance.remove(vehicleId);
        vehicles.remove(vehicle);
        vehicleById.remove(vehicleId);
        garage.removeVehicle(vehicle);
        rebuildVehicleIndexes();
    }

    public synchronized Vehicle findVehicle(String vehicleId) {
        return vehicleById.get(vehicleId);
    }

    public synchronized List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles);
    }

    public synchronized List<Vehicle> getVehiclesByType(String type) {
        List<Vehicle> result = vehiclesByType.get(type);
        if (result == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(result);
    }

    public synchronized List<Vehicle> getVehiclesByStatus(VehicleStatus status) {
        List<Vehicle> result = vehiclesByStatus.get(status);
        if (result == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(result);
    }

    public synchronized List<Vehicle> getAvailableVehicles() {
        return getVehiclesByStatus(VehicleStatus.AVAILABLE);
    }

    public synchronized List<Vehicle> getVehiclesByBrand(String brand) {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getBrand().equalsIgnoreCase(brand)) {
                result.add(vehicle);
            }
        }
        return result;
    }

    public synchronized List<Vehicle> getVehiclesCheaperThan(int maxPrice) {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getPrice() <= maxPrice) {
                result.add(vehicle);
            }
        }
        return result;
    }

    public synchronized List<Vehicle> getVehiclesInNaturalOrder() {
        List<Vehicle> result = new ArrayList<>(vehicles);
        Collections.sort(result);
        return result;
    }

    public synchronized List<Vehicle> getVehiclesSorted(Comparator<Vehicle> comparator) {
        List<Vehicle> result = new ArrayList<>(vehicles);
        Collections.sort(result, comparator);
        return result;
    }

    public synchronized void addClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Клиент не может быть пустым");
        }
        if (clientsById.containsKey(client.getId())) {
            throw new IllegalArgumentException("Клиент с id " + client.getId() + " уже есть");
        }
        clientsById.put(client.getId(), client);
    }

    public synchronized Client findClient(String clientId) {
        return clientsById.get(clientId);
    }

    public synchronized List<Client> getAllClients() {
        return new ArrayList<>(clientsById.values());
    }

    public synchronized RentalRequest submitRentalRequest(String requestId,
                                                          String clientId,
                                                          String vehicleId,
                                                          int days,
                                                          RequestPriority priority) {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("Id заявки не может быть пустым");
        }
        if (requestById.containsKey(requestId)) {
            throw new IllegalArgumentException("Заявка с id " + requestId + " уже есть");
        }

        Client client = clientsById.get(clientId);
        if (client == null) {
            throw new IllegalArgumentException("Клиент с id " + clientId + " не найден");
        }

        Vehicle vehicle = findVehicleOrThrow(vehicleId);
        if (!vehicle.isAvailable()) {
            throw new IllegalStateException("Машина " + vehicleId + " сейчас недоступна");
        }

        requestSequence = requestSequence + 1;
        RentalRequest request = new RentalRequest(
                requestId, client, vehicle, days, priority, requestSequence);

        requests.add(request);
        requestById.put(request.getId(), request);
        addToClientIndex(request);
        addToVehicleRequestIndex(request);
        requestQueue.add(request);
        client.addRequest(request);

        return request;
    }

    public synchronized RentalRequest processNextRequest() {
        while (!requestQueue.isEmpty()) {
            RentalRequest request = requestQueue.poll();

            if (request.getStatus() != RequestStatus.NEW) {
                continue;
            }

            Vehicle vehicle = vehicleById.get(request.getVehicle().getId());
            if (vehicle == null || !vehicle.isAvailable()) {
                request.reject();
                return request;
            }

            double cost = vehicle.calculateRentalCost(request.getDays());
            changeStatus(vehicle, VehicleStatus.RENTED);
            request.approve(cost);
            activeRentals.put(vehicle.getId(), request);
            return request;
        }
        return null;
    }

    public synchronized List<RentalRequest> processAllRequests() {
        List<RentalRequest> result = new ArrayList<>();
        RentalRequest request = processNextRequest();
        while (request != null) {
            result.add(request);
            request = processNextRequest();
        }
        return result;
    }

    public synchronized void cancelRequest(String requestId) {
        RentalRequest request = findRequestOrThrow(requestId);
        request.cancel();
        requestQueue.remove(request);
    }

    public synchronized RentalRequest returnVehicle(String vehicleId) {
        Vehicle vehicle = findVehicleOrThrow(vehicleId);
        RentalRequest request = activeRentals.remove(vehicleId);

        if (request == null) {
            throw new IllegalStateException("Машина " + vehicleId + " не арендована");
        }

        request.complete();
        changeStatus(vehicle, VehicleStatus.AVAILABLE);
        return request;
    }

    public synchronized List<RentalRequest> getPendingRequests() {
        List<RentalRequest> result = new ArrayList<>();
        for (RentalRequest request : requestQueue) {
            result.add(request);
        }
        Collections.sort(result);
        return result;
    }

    public synchronized int getPendingRequestCount() {
        return requestQueue.size();
    }

    public synchronized List<RentalRequest> getAllRequests() {
        return new ArrayList<>(requests);
    }

    public synchronized List<RentalRequest> getRequestsByClient(String clientId) {
        List<RentalRequest> result = requestsByClient.get(clientId);
        if (result == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(result);
    }

    public synchronized List<RentalRequest> getRequestsByVehicle(String vehicleId) {
        List<RentalRequest> result = requestsByVehicle.get(vehicleId);
        if (result == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(result);
    }

    public synchronized List<RentalRequest> getRequestsByStatus(RequestStatus status) {
        List<RentalRequest> result = new ArrayList<>();
        for (RentalRequest request : requests) {
            if (request.getStatus() == status) {
                result.add(request);
            }
        }
        return result;
    }

    public synchronized Map<String, RentalRequest> getActiveRentals() {
        return new HashMap<>(activeRentals);
    }

    public synchronized void requestMaintenance(String vehicleId) {
        Vehicle vehicle = findVehicleOrThrow(vehicleId);

        if (!(vehicle instanceof Maintainable)) {
            throw new IllegalArgumentException("Машина " + vehicleId + " не обслуживается");
        }
        if (!vehicle.isAvailable()) {
            throw new IllegalStateException("Обслуживать можно только доступную машину");
        }
        if (scheduledMaintenance.contains(vehicleId)) {
            throw new IllegalStateException("Машина уже стоит в очереди на обслуживание");
        }

        scheduledMaintenance.add(vehicleId);
        changeStatus(vehicle, VehicleStatus.IN_SERVICE);
        maintenanceQueue.add(vehicle);
    }

    public synchronized Vehicle processNextMaintenance(Mechanic mechanic) {
        if (mechanic == null) {
            throw new IllegalArgumentException("Механик не может быть пустым");
        }

        while (!maintenanceQueue.isEmpty()) {
            Vehicle vehicle = maintenanceQueue.poll();
            scheduledMaintenance.remove(vehicle.getId());

            if (vehicleById.get(vehicle.getId()) == null) {
                continue;
            }

            mechanic.service((Maintainable) vehicle);
            changeStatus(vehicle, VehicleStatus.AVAILABLE);
            return vehicle;
        }
        return null;
    }

    public synchronized List<Vehicle> getMaintenanceQueue() {
        return new ArrayList<>(maintenanceQueue);
    }

    public synchronized int getVehicleCount() {
        return vehicles.size();
    }

    public synchronized int getClientCount() {
        return clientsById.size();
    }

    public synchronized int getAvailableCount() {
        return vehiclesByStatus.get(VehicleStatus.AVAILABLE).size();
    }

    public synchronized int getRentedCount() {
        return vehiclesByStatus.get(VehicleStatus.RENTED).size();
    }

    public synchronized int getInServiceCount() {
        return vehiclesByStatus.get(VehicleStatus.IN_SERVICE).size();
    }

    public synchronized int getTotalFleetValue() {
        int total = 0;
        for (Vehicle vehicle : vehicles) {
            total = total + vehicle.getPrice();
        }
        return total;
    }

    public synchronized double getAverageVehiclePrice() {
        if (vehicles.isEmpty()) {
            return 0;
        }
        return (double) getTotalFleetValue() / vehicles.size();
    }

    public synchronized double getTotalRentalCost() {
        double total = 0;
        for (RentalRequest request : requests) {
            if (request.getStatus() == RequestStatus.APPROVED
                    || request.getStatus() == RequestStatus.COMPLETED) {
                total = total + request.getTotalCost();
            }
        }
        return total;
    }

    public synchronized Map<String, Integer> countVehiclesByType() {
        Map<String, Integer> result = new TreeMap<>();
        for (Vehicle vehicle : vehicles) {
            String type = vehicle.getVehicleType();
            Integer count = result.get(type);
            if (count == null) {
                result.put(type, 1);
            } else {
                result.put(type, count + 1);
            }
        }
        return result;
    }

    public synchronized Map<VehicleStatus, Integer> countVehiclesByStatus() {
        Map<VehicleStatus, Integer> result = new EnumMap<>(VehicleStatus.class);
        result.put(VehicleStatus.AVAILABLE, getAvailableCount());
        result.put(VehicleStatus.RENTED, getRentedCount());
        result.put(VehicleStatus.IN_SERVICE, getInServiceCount());
        return result;
    }

    public synchronized Map<String, Integer> countRequestsByClient() {
        Map<String, Integer> result = new TreeMap<>();
        for (Client client : clientsById.values()) {
            result.put(client.getName(), client.getRequestCount());
        }
        return result;
    }

    public synchronized List<Vehicle> getTopExpensiveVehicles(int limit) {
        List<Vehicle> sorted = getVehiclesSorted(VehicleComparators.BY_PRICE_DESC);
        List<Vehicle> result = new ArrayList<>();
        int size = Math.min(limit, sorted.size());
        for (int i = 0; i < size; i++) {
            result.add(sorted.get(i));
        }
        return result;
    }

    public synchronized String buildReport() {
        StringBuilder report = new StringBuilder();
        report.append("ОТЧЁТ ПО АВТОПАРКУ\n");
        report.append("Гараж: ").append(garage.getName()).append("\n");
        report.append("Машин: ").append(getVehicleCount())
                .append(", клиентов: ").append(getClientCount())
                .append(", заявок: ").append(requests.size()).append("\n");
        report.append("Доступно: ").append(getAvailableCount())
                .append(", арендовано: ").append(getRentedCount())
                .append(", на обслуживании: ").append(getInServiceCount()).append("\n");
        report.append(String.format(Locale.US,
                "Стоимость автопарка: %.2f, средняя цена: %.2f, сумма аренды: %.2f%n",
                (double) getTotalFleetValue(),
                getAverageVehiclePrice(),
                getTotalRentalCost()));
        report.append("Машины по типам: ").append(countVehiclesByType()).append("\n");
        report.append("Заявки по клиентам: ").append(countRequestsByClient()).append("\n");
        report.append("В очереди: ").append(getPendingRequestCount())
                .append(", активных аренд: ").append(activeRentals.size()).append("\n");
        report.append("Самые дорогие машины:\n");

        for (Vehicle vehicle : getTopExpensiveVehicles(3)) {
            report.append("- ").append(vehicle.getId())
                    .append(" ").append(vehicle.getBrand())
                    .append(" ").append(vehicle.getModel())
                    .append(": ").append(vehicle.getPrice()).append("\n");
        }

        return report.toString();
    }

    public synchronized Garage getGarage() {
        return garage;
    }

    private Vehicle findVehicleOrThrow(String vehicleId) {
        Vehicle vehicle = vehicleById.get(vehicleId);
        if (vehicle == null) {
            throw new IllegalArgumentException("Машина с id " + vehicleId + " не найдена");
        }
        return vehicle;
    }

    private RentalRequest findRequestOrThrow(String requestId) {
        RentalRequest request = requestById.get(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Заявка с id " + requestId + " не найдена");
        }
        return request;
    }

    private boolean hasOpenRequests(String vehicleId) {
        List<RentalRequest> list = requestsByVehicle.get(vehicleId);
        if (list == null) {
            return false;
        }

        for (RentalRequest request : list) {
            if (request.getStatus() == RequestStatus.NEW
                    || request.getStatus() == RequestStatus.APPROVED) {
                return true;
            }
        }
        return false;
    }

    private void changeStatus(Vehicle vehicle, VehicleStatus newStatus) {
        VehicleStatus oldStatus = vehicle.getStatus();
        if (oldStatus == newStatus) {
            return;
        }

        vehiclesByStatus.get(oldStatus).remove(vehicle);
        vehicle.setStatus(newStatus);
        vehiclesByStatus.get(newStatus).add(vehicle);
    }

    private void rebuildVehicleIndexes() {
        vehiclesByType.clear();
        for (List<Vehicle> list : vehiclesByStatus.values()) {
            list.clear();
        }

        for (Vehicle vehicle : vehicles) {
            addToTypeIndex(vehicle);
            addToStatusIndex(vehicle);
        }
    }

    private void addToTypeIndex(Vehicle vehicle) {
        String type = vehicle.getVehicleType();
        List<Vehicle> list = vehiclesByType.get(type);
        if (list == null) {
            list = new ArrayList<>();
            vehiclesByType.put(type, list);
        }
        list.add(vehicle);
    }

    private void addToStatusIndex(Vehicle vehicle) {
        List<Vehicle> list = vehiclesByStatus.get(vehicle.getStatus());
        list.add(vehicle);
    }

    private void addToClientIndex(RentalRequest request) {
        String clientId = request.getClient().getId();
        List<RentalRequest> list = requestsByClient.get(clientId);
        if (list == null) {
            list = new ArrayList<>();
            requestsByClient.put(clientId, list);
        }
        list.add(request);
    }

    private void addToVehicleRequestIndex(RentalRequest request) {
        String vehicleId = request.getVehicle().getId();
        List<RentalRequest> list = requestsByVehicle.get(vehicleId);
        if (list == null) {
            list = new ArrayList<>();
            requestsByVehicle.put(vehicleId, list);
        }
        list.add(request);
    }
}
