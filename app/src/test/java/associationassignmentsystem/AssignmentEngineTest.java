package associationassignmentsystem;


import associationassignmentsystem.AssignmentEngine;
import associationassignmentsystem.Service;
import associationassignmentsystem.Volunteer;
import associationassignmentsystem.Preference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections; // For shuffling
import java.util.Map; // For accessing the serviceMap (if you add a getter)

public class AssignmentEngineTest {
    public static void main(String[] args) {

        System.out.println("--- Setting up Test Data ---");

        // --- 1. Set up Test Data ---

        // Create a list of Services (e.g., 10 services)
        List<Service> services = new ArrayList<>();
        services.add(new Service("Service A")); // ID will be 1
        services.add(new Service("Service B")); // ID will be 2
        services.add(new Service("Service C")); // ID will be 3
        services.add(new Service("Service D")); // ID will be 4
        services.add(new Service("Service E")); // ID will be 5
        services.add(new Service("Service F")); // ID will be 6
        services.add(new Service("Service G")); // ID will be 7
        services.add(new Service("Service H")); // ID will be 8
        services.add(new Service("Service I")); // ID will be 9
        services.add(new Service("Service J")); // ID will be 10

        // Optional: You might want to manually set capacities for some services for specific test cases
        // Example: services.get(0).assignCapacity(5); // Assuming you add a public assignCapacity(int capacity) method to Service


        // Create a list of Volunteers (e.g., 30 volunteers)
        List<Volunteer> volunteers = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= 30; i++) {
            Volunteer vol = new Volunteer("Volunteer " + i);

            // Add random preferences (up to 5)
            int numPreferences = random.nextInt(6); // Random number of preferences from 0 to 5
            List<Service> shuffledServices = new ArrayList<>(services);
            Collections.shuffle(shuffledServices, random); // Shuffle services to pick random preferred ones

            for (int j = 0; j < numPreferences; j++) {
                // Add the j-th shuffled service as the (j+1)-th preference
                vol.addPreference(shuffledServices.get(j), j + 1);
            }
            volunteers.add(vol);
        }

        System.out.println("Created " + volunteers.size() + " volunteers and " + services.size() + " services.");

        // --- 2. Create AssignmentEngine Instance ---
        // Ensure your AssignmentEngine constructor takes List<Volunteer> and List<Service>
        AssignmentEngine engine = new AssignmentEngine(volunteers, services);

        System.out.println("--- Running Genetic Algorithm ---");

        // --- 3. Run the Algorithm ---
        List<Integer> bestAssignmentChromosome = engine.runGeneticAlgorithm();

        System.out.println("--- Genetic Algorithm Finished ---");

        // --- 4. Analyze the Output ---

        System.out.println("\nBest Assignment Chromosome (Service IDs): " + bestAssignmentChromosome);

        // To get the cost of the best assignment, you can call calculateFitness on the result
        // You might need to make calculateFitness public in AssignmentEngine for testing purposes,
        // or add a public getter method that returns the fitness of a given chromosome.
        // Example (assuming calculateFitness is made public for testing):
        double bestCost = engine.calculateFitness(bestAssignmentChromosome);
        System.out.println("Cost of Best Assignment: " + bestCost);


        // Interpret the best assignment (map Service IDs back to Service names and Volunteers)
        System.out.println("\nBest Assignment Details:");
        if (bestAssignmentChromosome != null && bestAssignmentChromosome.size() == volunteers.size()) {
            // To get Service names efficiently, it's best to use the serviceMap within AssignmentEngine.
            // You might need to add a public getter for serviceMap in AssignmentEngine for this.
            // Example (assuming getServiceMap() is added to AssignmentEngine):
                Map<Integer, Service> serviceMap = engine.getServiceMap();

            for (int i = 0; i < volunteers.size(); i++) {
                Volunteer volunteer = volunteers.get(i);
                int assignedServiceId = bestAssignmentChromosome.get(i);
                Service assignedService = serviceMap.get(assignedServiceId);

                String serviceName = (assignedService != null) ? assignedService.getName() : "Unknown Service";
                System.out.println(volunteer.getName() + " (" + volunteer.getVolunteerNumber() + ") assigned to " + serviceName + " (ID: " + assignedServiceId + ")");
            }
        } else {
            System.out.println("Could not retrieve best assignment details.");
        }

        // --- 5. Optional: Observe Evolution ---
        // To see how the fitness improves over generations, add print statements inside the loop
        // in your runGeneticAlgorithm method in AssignmentEngine.
        // Example:
        // In AssignmentEngine.runGeneticAlgorithm():
        // for (int generation = 0; generation < numberOfGenerations; generation++) {
        //     // ... GA steps ...
        //     if (generation % 100 == 0 || generation == numberOfGenerations - 1) { // Print every 100 gens and last gen
        //         List<Integer> currentBest = getBestChromosome(); // You might need to make this public or add a getter
        //         System.out.println("Gen " + generation + ", Best Cost: " + calculateFitness(currentBest)); // calculateFitness also needs to be accessible
        //     }
        // }
    }

}
