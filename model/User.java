package fleet.model;

public abstract class User {
    private final String id;
    private final String name;

    protected User(String id, String name) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id пользователя не может быть пустым");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        this.id = id.trim();
        this.name = name.trim();
    }

    public final String getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public abstract String getRole();

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        User user = (User) other;
        return id.equals(user.id);
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return getRole() + " | " + id + " | " + name;
    }
}
