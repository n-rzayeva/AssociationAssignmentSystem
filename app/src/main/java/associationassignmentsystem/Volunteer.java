package associationassignmentsystem;

import java.util.ArrayList;
import java.util.List;

public class Volunteer {

    private String _volunteerNumber; // represents the id like #Vo03
    private String _name;
    private static int _numberOfVolunteers = 0;
    private List<Preference> _preferences;

    public Volunteer(String name) {
        _numberOfVolunteers ++;
        this._name = name;
        this._volunteerNumber = "#Vo" + String.format("%02d", _numberOfVolunteers);
        this._preferences = new ArrayList<>();
    }

    // Getters
    public String getVolunteerNumber() {
        return _volunteerNumber;
    }

    public String getName() {
        return _name;
    }

    public List<Preference> getPreferedServices() {
        return _preferences;
    }

    // Method to add a preference to the volunteer's list
    public void addPreference(Service service, int rank) {
        if (this._preferences.size() < 5) { 
            Preference newPreference = new Preference(service, rank);
            this._preferences.add(newPreference);
        } else {
            System.out.println("Volunteer " + this._name + " already has 5 preferences. Cannot add more.");
            // Or handle this case as an error/exception
        }
    }

    // You might also need a method to find a preference by service
    public Preference getPreferenceForService(Service service) {
        for (Preference preference : this._preferences) {
            Service preferredService = preference.getService();

            if (preferredService != null && preferredService.equals(service)) {
                return preference;
            }
        }
        return null;
    }

    // Potentially more methods

    // Consider overriding equals() and hashCode() in Volunteer if needed
    // public boolean equals(Object o) { ... }
    // public int hashCode() { ... }
}
