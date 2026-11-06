package fleet.model;

import java.util.Objects;

public class Engine {
    private final int horsePower;
    private final String type;

    public Engine(int horsePower, String type) {
        if (horsePower <= 0) {
            throw new IllegalArgumentException("Мощность двигателя должна быть больше нуля");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Тип двигателя не может быть пустым");
        }
        this.horsePower = horsePower;
        this.type = type.trim();
    }

    public int getHorsePower() {
        return horsePower;
    }

    public String getType() {
        return type;
    }

    public void start() {
        System.out.println("Двигатель " + type + " (" + horsePower + " л.с.) запущен");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Engine engine = (Engine) other;
        return horsePower == engine.horsePower && type.equals(engine.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(horsePower, type);
    }

    @Override
    public String toString() {
        return type + " " + horsePower + " л.с.";
    }
}
