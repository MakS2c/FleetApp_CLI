package fleet.app;

import fleet.model.Car;
import fleet.model.Client;
import fleet.model.Engine;
import fleet.model.Mechanic;
import fleet.model.Motorcycle;
import fleet.model.RentalRequest;
import fleet.model.RequestPriority;
import fleet.model.Truck;
import fleet.model.Vehicle;
import fleet.service.FleetService;
import fleet.service.VehicleComparators;
import fleet.storage.FleetFileStorage;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final FleetFileStorage STORAGE = new FleetFileStorage("fleet-data.txt");
    private static final Mechanic MECHANIC =
            new Mechanic("ME-1", "Алексей", "Двигатель и тормоза");

    private static FleetService service = createDemoService();

    public static void main(String[] args) {
        System.out.println("Добро пожаловать в систему автопарка!");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Выберите пункт: ");

            try {
                switch (choice) {
                    case 1:
                        showAllVehicles();
                        break;
                    case 2:
                        showAvailableVehicles();
                        break;
                    case 3:
                        findVehicle();
                        break;
                    case 4:
                        addVehicle();
                        break;
                    case 5:
                        removeVehicle();
                        break;
                    case 6:
                        showVehiclesByType();
                        break;
                    case 7:
                        showVehiclesCheaperThan();
                        break;
                    case 8:
                        showSortedVehicles();
                        break;
                    case 9:
                        showClients();
                        break;
                    case 10:
                        addClient();
                        break;
                    case 11:
                        createRequest();
                        break;
                    case 12:
                        showRequestQueue();
                        break;
                    case 13:
                        processNextRequest();
                        break;
                    case 14:
                        processAllRequests();
                        break;
                    case 15:
                        cancelRequest();
                        break;
                    case 16:
                        returnVehicle();
                        break;
                    case 17:
                        requestMaintenance();
                        break;
                    case 18:
                        processMaintenance();
                        break;
                    case 19:
                        showReport();
                        break;
                    case 20:
                        saveToFile();
                        break;
                    case 21:
                        loadFromFile();
                        break;
                    case 22:
                        updateVehiclePrice();
                        break;
                    case 0:
                        running = false;
                        System.out.println("До свидания!");
                        break;
                    default:
                        System.out.println("Такого пункта нет.");
                }
            } catch (Exception exception) {
                System.out.println("Ошибка: " + exception.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== МЕНЮ =====");
        System.out.println("1. Показать все машины");
        System.out.println("2. Показать свободные машины");
        System.out.println("3. Найти машину по id");
        System.out.println("4. Добавить машину");
        System.out.println("5. Удалить машину");
        System.out.println("6. Показать машины по типу");
        System.out.println("7. Показать машины до цены");
        System.out.println("8. Показать отсортированные машины");
        System.out.println("9. Показать клиентов");
        System.out.println("10. Добавить клиента");
        System.out.println("11. Создать заявку на аренду");
        System.out.println("12. Показать очередь заявок");
        System.out.println("13. Обработать следующую заявку");
        System.out.println("14. Обработать все заявки");
        System.out.println("15. Отменить заявку");
        System.out.println("16. Вернуть машину");
        System.out.println("17. Отправить машину на обслуживание");
        System.out.println("18. Обработать обслуживание");
        System.out.println("19. Показать отчёт");
        System.out.println("20. Сохранить в файл");
        System.out.println("21. Загрузить из файла");
        System.out.println("22. Изменить цену машины");
        System.out.println("0. Выход");
    }

    private static void showAllVehicles() {
        printVehicles("Все машины", service.getAllVehicles());
    }

    private static void showAvailableVehicles() {
        printVehicles("Свободные машины", service.getAvailableVehicles());
    }

    private static void findVehicle() {
        String id = readText("Введите id машины: ");
        Vehicle vehicle = service.findVehicle(id);

        if (vehicle == null) {
            System.out.println("Машина не найдена.");
        } else {
            System.out.println(vehicle);
        }
    }

    private static void addVehicle() {
        System.out.println("1 - Car, 2 - Truck, 3 - Motorcycle");
        int type = readInt("Тип машины: ");

        String id = readText("Id машины: ");
        String brand = readText("Марка: ");
        String model = readText("Модель: ");
        int year = readInt("Год выпуска: ");
        String color = readText("Цвет: ");
        int price = readInt("Цена: ");

        if (type == 1) {
            int doors = readInt("Количество дверей: ");
            double fuel = readDouble("Расход топлива: ");
            int power = readInt("Мощность двигателя: ");
            String engineType = readText("Тип двигателя: ");
            Vehicle car = new Car(id, brand, model, year, color, price,
                    doors, fuel, new Engine(power, engineType));
            service.addVehicle(car);
        } else if (type == 2) {
            double capacity = readDouble("Грузоподъёмность: ");
            int axles = readInt("Количество осей: ");
            service.addVehicle(new Truck(id, brand, model, year, color,
                    price, capacity, axles));
        } else if (type == 3) {
            int volume = readInt("Объём двигателя: ");
            boolean sidecar = readBoolean("Есть коляска?");
            service.addVehicle(new Motorcycle(id, brand, model, year, color,
                    price, volume, sidecar));
        } else {
            System.out.println("Неизвестный тип машины.");
            return;
        }

        System.out.println("Машина добавлена.");
    }

    private static void removeVehicle() {
        String id = readText("Введите id машины: ");
        service.removeVehicle(id);
        System.out.println("Машина удалена.");
    }

    private static void showVehiclesByType() {
        System.out.println("1 - Car, 2 - Truck, 3 - Motorcycle");
        int type = readInt("Тип машины: ");
        String typeName = getTypeName(type);

        if (typeName == null) {
            System.out.println("Неизвестный тип машины.");
            return;
        }

        printVehicles("Машины типа " + typeName, service.getVehiclesByType(typeName));
    }

    private static void showVehiclesCheaperThan() {
        int maxPrice = readInt("Максимальная цена: ");
        printVehicles("Машины до " + maxPrice,
                service.getVehiclesCheaperThan(maxPrice));
    }

    private static void showSortedVehicles() {
        System.out.println("1 - по цене");
        System.out.println("2 - по году");
        System.out.println("3 - по марке");
        System.out.println("4 - по типу и цене");
        System.out.println("5 - по цене аренды");
        System.out.println("6 - по id");
        int choice = readInt("Сортировка: ");

        List<Vehicle> result;

        if (choice == 1) {
            result = service.getVehiclesSorted(VehicleComparators.BY_PRICE_DESC);
        } else if (choice == 2) {
            result = service.getVehiclesSorted(VehicleComparators.BY_YEAR_DESC);
        } else if (choice == 3) {
            result = service.getVehiclesSorted(VehicleComparators.BY_BRAND_MODEL);
        } else if (choice == 4) {
            result = service.getVehiclesSorted(VehicleComparators.BY_TYPE_THEN_PRICE);
        } else if (choice == 5) {
            result = service.getVehiclesSorted(VehicleComparators.BY_DAILY_RENTAL_COST);
        } else if (choice == 6) {
            result = service.getVehiclesInNaturalOrder();
        } else {
            System.out.println("Неизвестный способ сортировки.");
            return;
        }

        printVehicles("Отсортированные машины", result);
    }

    private static void showClients() {
        List<Client> clients = service.getAllClients();

        if (clients.isEmpty()) {
            System.out.println("Клиентов пока нет.");
            return;
        }

        for (Client client : clients) {
            System.out.println(client + " | заявок: " + client.getRequestCount());
        }
    }

    private static void addClient() {
        String id = readText("Id клиента: ");
        String name = readText("Имя клиента: ");
        service.addClient(new Client(id, name));
        System.out.println("Клиент добавлен.");
    }

    private static void createRequest() {
        if (service.getAllClients().isEmpty()) {
            System.out.println("Сначала добавьте клиента.");
            return;
        }
        if (service.getAvailableVehicles().isEmpty()) {
            System.out.println("Нет свободных машин.");
            return;
        }

        showClients();
        printVehicles("Свободные машины", service.getAvailableVehicles());

        String requestId = readText("Id заявки: ");
        String clientId = readText("Id клиента: ");
        String vehicleId = readText("Id машины: ");
        int days = readInt("Количество дней: ");
        RequestPriority priority = readPriority();

        RentalRequest request = service.submitRentalRequest(
                requestId, clientId, vehicleId, days, priority);

        System.out.println("Заявка создана: " + request);
    }

    private static void showRequestQueue() {
        printRequests("Очередь заявок", service.getPendingRequests());
    }

    private static void processNextRequest() {
        RentalRequest request = service.processNextRequest();

        if (request == null) {
            System.out.println("Очередь заявок пуста.");
        } else {
            System.out.println("Обработана: " + request);
        }
    }

    private static void processAllRequests() {
        List<RentalRequest> requests = service.processAllRequests();

        if (requests.isEmpty()) {
            System.out.println("Очередь заявок пуста.");
            return;
        }

        for (RentalRequest request : requests) {
            System.out.println(request);
        }

        System.out.println("Обработано заявок: " + requests.size());
    }

    private static void cancelRequest() {
        String id = readText("Id заявки: ");
        service.cancelRequest(id);
        System.out.println("Заявка отменена.");
    }

    private static void returnVehicle() {
        String id = readText("Id машины: ");
        RentalRequest request = service.returnVehicle(id);
        System.out.println("Машина возвращена. Заявка: " + request);
    }

    private static void requestMaintenance() {
        String id = readText("Id машины: ");
        service.requestMaintenance(id);
        System.out.println("Машина отправлена на обслуживание.");
    }

    private static void processMaintenance() {
        Vehicle vehicle = service.processNextMaintenance(MECHANIC);

        if (vehicle == null) {
            System.out.println("Очередь обслуживания пуста.");
        } else {
            System.out.println("Обслужена машина: " + vehicle.getId());
        }
    }

    private static void showReport() {
        System.out.println();
        System.out.println(service.buildReport());
    }

    private static void updateVehiclePrice() {
        String id = readText("Id машины: ");
        Vehicle vehicle = service.findVehicle(id);

        if (vehicle == null) {
            System.out.println("Машина не найдена.");
            return;
        }
        if (!vehicle.isAvailable()) {
            System.out.println("Можно изменить только доступную машину.");
            return;
        }

        int newPrice = readInt("Новая цена: ");
        vehicle.setPrice(newPrice);
        service.updateVehicle(vehicle);
        System.out.println("Цена изменена.");
    }

    private static void saveToFile() throws IOException {
        STORAGE.save(service);
        System.out.println("Сохранено в файл: " + STORAGE.getFile().toAbsolutePath());
    }

    private static void loadFromFile() throws IOException {
        if (!STORAGE.exists()) {
            System.out.println("Файл не найден: " + STORAGE.getFile().toAbsolutePath());
            return;
        }

        service = STORAGE.load();
        System.out.println("Загружено машин: " + service.getVehicleCount()
                + ", клиентов: " + service.getClientCount());
    }

    private static void printVehicles(String title, List<Vehicle> vehicles) {
        System.out.println();
        System.out.println(title + ":");

        if (vehicles.isEmpty()) {
            System.out.println("Список пуст.");
            return;
        }

        for (Vehicle vehicle : vehicles) {
            System.out.println(vehicle);
        }
    }

    private static void printRequests(String title, List<RentalRequest> requests) {
        System.out.println();
        System.out.println(title + ":");

        if (requests.isEmpty()) {
            System.out.println("Список пуст.");
            return;
        }

        for (RentalRequest request : requests) {
            System.out.println(request);
        }
    }

    private static String getTypeName(int type) {
        if (type == 1) {
            return "Car";
        }
        if (type == 2) {
            return "Truck";
        }
        if (type == 3) {
            return "Motorcycle";
        }
        return null;
    }

    private static RequestPriority readPriority() {
        while (true) {
            System.out.println("1 - HIGH, 2 - NORMAL, 3 - LOW");
            int choice = readInt("Приоритет: ");

            if (choice == 1) {
                return RequestPriority.HIGH;
            }
            if (choice == 2) {
                return RequestPriority.NORMAL;
            }
            if (choice == 3) {
                return RequestPriority.LOW;
            }

            System.out.println("Введите 1, 2 или 3.");
        }
    }

    private static String readText(String message) {
        while (true) {
            System.out.print(message);
            String value = SCANNER.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("Поле не может быть пустым.");
        }
    }

    private static int readInt(String message) {
        while (true) {
            String value = readText(message);

            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Введите целое число.");
            }
        }
    }

    private static double readDouble(String message) {
        while (true) {
            String value = readText(message);

            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException exception) {
                System.out.println("Введите число.");
            }
        }
    }

    private static boolean readBoolean(String message) {
        while (true) {
            String value = readText(message + " (да/нет): ");

            if (value.equalsIgnoreCase("да")) {
                return true;
            }
            if (value.equalsIgnoreCase("нет")) {
                return false;
            }

            System.out.println("Введите да или нет.");
        }
    }

    private static FleetService createDemoService() {
        FleetService demo = new FleetService();

        demo.addVehicle(new Car("CAR-001", "Honda", "Civic", 2022, "Красный",
                50000, 4, 7.5));
        demo.addVehicle(new Car("CAR-002", "BMW", "X5", 2023, "Синий",
                80000, 5, 10.2, new Engine(340, "Бензин")));
        demo.addVehicle(new Truck("TRUCK-001", "Volvo", "FH16", 2020, "Белый",
                120000, 25.0, 3));
        demo.addVehicle(new Truck("TRUCK-002", "MAN", "TGX", 2021, "Серый",
                95000, 18.5, 2));
        demo.addVehicle(new Motorcycle("MOTO-001", "Yamaha", "R6", 2021, "Чёрный",
                12000, 600, false));
        demo.addVehicle(new Motorcycle("MOTO-002", "Ural", "Gear Up", 2019, "Зелёный",
                9000, 750, true));

        demo.addClient(new Client("CL-1", "Иван Петров"));
        demo.addClient(new Client("CL-2", "Анна Смирнова"));

        return demo;
    }
}
