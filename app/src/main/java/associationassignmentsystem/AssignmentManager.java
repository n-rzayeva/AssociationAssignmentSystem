package associationassignmentsystem;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


public class AssignmentManager {

    private List<Integer> bestAssignmentResult;
    private List<Volunteer> volunteers;
    private List<Service> services;

    public AssignmentManager(List<Volunteer> volunteers, List<Service> services) {
        this.volunteers = volunteers;
        this.services = services;
    }

    // Synchronize this method
    public synchronized void setBestAssignmentResult(List<Integer> bestAssignmentResult) {
        this.bestAssignmentResult = bestAssignmentResult;
    }

    public synchronized List<JSONObject> getAssignments() {
        List<JSONObject> assignments = new ArrayList<>();
        for (Service service : services) {
            JSONObject serviceData = new JSONObject();
            serviceData.put("serviceName", service.getName());
            int filled = 0;

            if (bestAssignmentResult != null) {
                for (int i = 0; i < bestAssignmentResult.size(); i++) {
                    int assignedServiceIndex = bestAssignmentResult.get(i);
                    if (assignedServiceIndex == services.indexOf(service)) {
                        filled++;
                    }
                }
            }

            serviceData.put("filled", filled);
            serviceData.put("capacity", service.getCapacity());
            assignments.add(serviceData);
        }
        return assignments;
    }

    public synchronized List<JSONObject> getDetailedAssignments() {
        List<JSONObject> detailedAssignments = new ArrayList<>();
        for (Service service : services) {
            JSONObject serviceData = new JSONObject();
            serviceData.put("serviceName", service.getName());
            serviceData.put("capacity", service.getCapacity());

            JSONArray assignedVolunteers = new JSONArray();
            if (bestAssignmentResult != null) {
                for (int i = 0; i < bestAssignmentResult.size(); i++) {
                    int assignedServiceIndex = bestAssignmentResult.get(i);
                    if (assignedServiceIndex == services.indexOf(service)) {
                        Volunteer volunteer = volunteers.get(i);
                        Preference pref = volunteer.getPreferenceForService(service);
                        JSONObject volunteerData = new JSONObject();
                        volunteerData.put("volunteerName", volunteer.getName());
                        volunteerData.put("cost", (pref != null) ? pref.getRank() : -1);
                        assignedVolunteers.put(volunteerData);
                    }
                }
            }
            serviceData.put("assignedVolunteers", assignedVolunteers);
            detailedAssignments.add(serviceData);
        }
        return detailedAssignments;
    }

}
