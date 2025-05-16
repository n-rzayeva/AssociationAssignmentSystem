package associationassignmentsystem;

import java.util.ArrayList;
import java.util.List;

public class Chromosome {
    private List<Integer> serviceAssignments;

    public Chromosome(List<Integer> serviceAssignments) {
        this.serviceAssignments = serviceAssignments;
    }

    public Chromosome(int numVolunteers) {
        this.serviceAssignments = new ArrayList<>(numVolunteers);
        for (int i = 0; i < numVolunteers; i++) {
            this.serviceAssignments.add(-1); // Initialize with an invalid value
        }
    }

    public int getServiceAssignment(int volunteerIndex) {
        return serviceAssignments.get(volunteerIndex);
    }

    public void setServiceAssignment(int volunteerIndex, int serviceIndex) {
        serviceAssignments.set(volunteerIndex, serviceIndex);
    }

    public List<Integer> getServiceAssignments() {
        return serviceAssignments;
    }

    public int size() {
        return serviceAssignments.size();
    }

    //  Override toString() for easy printing of chromosomes
    @Override
    public String toString() {
        return "Chromosome{" +
                "serviceAssignments=" + serviceAssignments +
                "}";
    }
}
