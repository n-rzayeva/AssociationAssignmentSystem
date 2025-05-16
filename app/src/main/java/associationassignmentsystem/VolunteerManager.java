package associationassignmentsystem;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class VolunteerManager {
    private List<Volunteer> volunteers;

    public VolunteerManager() {
        this.volunteers = new CopyOnWriteArrayList<>();
    }

    public int createVolunteer() {
        Volunteer volunteer = new Volunteer();
        volunteers.add(volunteer);
        System.out.println("Created volunteer with ID: " + volunteer.getId());
        return volunteer.getId();
    }

    public void setVolunteerPreferences(int volunteerId, List<Integer> serviceIds, List<Service> services) {
        Volunteer volunteer = getVolunteerById(volunteerId);
        if (volunteer != null) {
            volunteer.setPreferencesFromIds(serviceIds, services);
            System.out.println("Preferences set for Volunteer #" + volunteerId);
        } else {
            System.err.println("Volunteer not found: " + volunteerId);
        }
    }

    public void updateVolunteerPreferences(int volunteerId, List<Integer> serviceIds, List<Service> services) {
        Volunteer volunteer = getVolunteerById(volunteerId);
        if (volunteer != null) {
            volunteer.setPreferencesFromIds(serviceIds, services);
            System.out.println("Preferences updated for Volunteer #" + volunteerId);
        } else {
            System.err.println("Volunteer not found: " + volunteerId);
        }
    }

    private Volunteer getVolunteerById(int id) {
        for (Volunteer volunteer : volunteers) {
            if (volunteer.getId() == id) {
                return volunteer;
            }
        }
        return null;
    }

    public List<Volunteer> getVolunteers() {
        return volunteers;
    }
}
