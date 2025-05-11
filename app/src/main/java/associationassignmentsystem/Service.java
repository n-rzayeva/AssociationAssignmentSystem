package associationassignmentsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Service {

    private int _id;
    private String _name;
    private int _capacity;
    private List<Volunteer> _assignedVolunteers;
    private static int _numberofServices = 0;

    public Service (String name) {
        _numberofServices++;

        this._id = _numberofServices;
        this._name = name;

        Random random = new Random();
        this._capacity = random.nextInt(6) + 1;

        this._assignedVolunteers = new ArrayList<>();
    }

    // Getters
    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public int getCapacity () {
        return _capacity;
    }

    public List<Volunteer> getAssignedVolunteers() {
        return _assignedVolunteers;
    }

    public boolean assignVolunteer(Volunteer volunteer) {
        if (this._assignedVolunteers.size() < this._capacity) {
            this._assignedVolunteers.add(volunteer);
            return true;
        }
        return false;
    }

    public boolean removeVolunteer(Volunteer volunteer) {
        return this._assignedVolunteers.remove(volunteer);
    }

    public int getNumberOfAssignedVolunteers() {
        return this._assignedVolunteers.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return _id == service._id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }


    // potentially many other methods
}
