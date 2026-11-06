package fleet.model;

import java.time.LocalDateTime;

public class RentalRequest implements Comparable<RentalRequest> {
    private final String id;
    private final Client client;
    private final Vehicle vehicle;
    private final int days;
    private final RequestPriority priority;
    private final long sequence;
    private final LocalDateTime createdAt;

    private RequestStatus status;
    private double totalCost;

    public RentalRequest(String id, Client client, Vehicle vehicle, int days,
                         RequestPriority priority, long sequence) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id заявки не может быть пустым");
        }
        if (client == null) {
            throw new IllegalArgumentException("Клиент не может быть пустым");
        }
        if (vehicle == null) {
            throw new IllegalArgumentException("Машина не может быть пустой");
        }
        if (days <= 0) {
            throw new IllegalArgumentException("Количество дней должно быть больше нуля");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Приоритет не может быть пустым");
        }
        this.id = id.trim();
        this.client = client;
        this.vehicle = vehicle;
        this.days = days;
        this.priority = priority;
        this.sequence = sequence;
        this.createdAt = LocalDateTime.now();
        this.status = RequestStatus.NEW;
    }

    public String getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public int getDays() {
        return days;
    }

    public RequestPriority getPriority() {
        return priority;
    }

    public long getSequence() {
        return sequence;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void approve(double totalCost) {
        checkStatus(RequestStatus.NEW);
        if (totalCost < 0) {
            throw new IllegalArgumentException("Стоимость не может быть отрицательной");
        }
        this.totalCost = totalCost;
        this.status = RequestStatus.APPROVED;
    }

    public void reject() {
        checkStatus(RequestStatus.NEW);
        this.status = RequestStatus.REJECTED;
    }

    public void complete() {
        checkStatus(RequestStatus.APPROVED);
        this.status = RequestStatus.COMPLETED;
    }

    public void cancel() {
        checkStatus(RequestStatus.NEW);
        this.status = RequestStatus.CANCELLED;
    }

    private void checkStatus(RequestStatus expected) {
        if (status != expected) {
            throw new IllegalStateException(
                    "Заявка " + id + " должна быть в статусе " + expected
                            + ", а сейчас " + status);
        }
    }

    @Override
    public int compareTo(RentalRequest other) {
        int byPriority = Integer.compare(priority.getWeight(), other.priority.getWeight());
        if (byPriority != 0) {
            return byPriority;
        }
        int bySequence = Long.compare(sequence, other.sequence);
        if (bySequence != 0) {
            return bySequence;
        }
        return id.compareTo(other.id);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        RentalRequest request = (RentalRequest) other;
        return id.equals(request.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Заявка " + id + " | клиент: " + client.getName()
                + " | машина: " + vehicle.getId()
                + " | дней: " + days
                + " | приоритет: " + priority
                + " | статус: " + status
                + " | стоимость: " + totalCost;
    }
}
