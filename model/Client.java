package fleet.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Client extends User {
    private final List<RentalRequest> requests = new ArrayList<>();

    public Client(String id, String name) {
        super(id, name);
    }

    public void addRequest(RentalRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Заявка не может быть пустой");
        }
        if (!requests.contains(request)) {
            requests.add(request);
        }
    }

    public List<RentalRequest> getRequests() {
        return Collections.unmodifiableList(new ArrayList<>(requests));
    }

    public int getRequestCount() {
        return requests.size();
    }

    @Override
    public String getRole() {
        return "Client";
    }
}
