package associationassignmentsystem;

import java.util.ArrayList;
import java.util.List;

public class Volunteer {

    private int _id;
    private String _name;
    private List<Preference> _preferences;
    private static int _numberOfVolunteers = 0;

    public Volunteer() {
        _numberOfVolunteers ++;
        this._id = _numberOfVolunteers;
        this._name = "#V" + String.format("%02d", this._id);
        this._preferences = new ArrayList<>();
    }

    // Getters
    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public List<Preference> getPreferedServices() {
        return _preferences;
    }

    public void addPreference(Service service, int rank) {
        if (this._preferences.size() < 5) { 
            Preference newPreference = new Preference(service, rank);
            this._preferences.add(newPreference);
        } else {
            System.out.println("Volunteer " + this._name + " already has 5 preferences. Cannot add more.");
            // Or handle this case as an error/exception
        }
    }

    public Preference getPreferenceForService(Service service) {
        for (Preference preference : this._preferences) {
            Service preferredService = preference.getService();

            if (preferredService != null && preferredService.equals(service)) {
                return preference;
            }
        }
        return null;
    }

    public void setPreferencesFromIds(List<Integer> preferredServiceIds, List<Service> allServices) {
        this._preferences.clear(); // Clear any existing preferences
        if (preferredServiceIds != null) {
            for (int rank = 0; rank < Math.min(preferredServiceIds.size(), 5); rank++) {
                int serviceId = preferredServiceIds.get(rank);
                // Find the Service object by its ID
                Service service = allServices.stream()
                        .filter(s -> s.getId() == serviceId)
                        .findFirst()
                        .orElse(null);
                if (service != null) {
                    this._preferences.add(new Preference(service, rank + 1)); // Rank is 1-based
                } else {
                    System.out.println("Warning: Service ID " + serviceId + " not found for volunteer " + this._name);
                }
            }
        }
    }
}
