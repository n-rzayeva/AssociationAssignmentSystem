package associationassignmentsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AssignmentEngine {

    private List<Volunteer> allVolunteers;
    private List<Service> allServices;
    private Map<Integer, Service> serviceMap;

    private List<List<Integer>> population;

    private int populationSize = 300;
    private double mutationRate = 0.03; // 3%
    private int numberOfGenerations = 1000;
    private int tournamentSize = 5;

    private Random random = new Random();

    private static final double NON_PREFERRED_PENALTY = 250;
    private static final double CAPACITY_VIOLATION_PENALTY = 1250.0;


    public AssignmentEngine(List<Volunteer> volunteers, List<Service> services) {
        this.allVolunteers = volunteers;
        this.allServices = services;
        this.population = new ArrayList<>();
        this.serviceMap = createServiceMap(allServices);

        initializePopulation();
    }

    public Map<Integer, Service> getServiceMap() {
        return serviceMap;
    }

    private Map<Integer, Service> createServiceMap(List<Service> services) {
        Map<Integer, Service> map = new HashMap<>();
        for (Service service : services) {
            map.put(service.getId(), service);
        }
        return map;
    }

    private void initializePopulation() {
        int numberOfVolunteers = allVolunteers.size();
        int numberOfServices = allServices.size();

        for (int i = 0; i < populationSize; i++) {
            List<Integer> chromosome = new ArrayList<>(numberOfVolunteers);
            for (int j = 0; j < numberOfVolunteers; j++) {
                int randomServiceId = random.nextInt(numberOfServices) + 1;
                chromosome.add(randomServiceId);
            }
            this.population.add(chromosome);
        }
    }

    public List<Integer> runGeneticAlgorithm() {
        System.out.println("Starting Genetic Algorithm for " + numberOfGenerations + " generations with population size " + populationSize);

        List<Integer> overallBestChromosome = null;
        double overallMinCost = Double.MAX_VALUE;

        for (int generation = 0; generation < numberOfGenerations; generation++) {
            // 1. Evaluate Fitness (implicitly done during selection and getBestChromosome)

            // Get the best chromosome of the current generation before creating the next one
            List<Integer> currentGenerationBest = getBestChromosome();
            double currentBestCost = calculateFitness(currentGenerationBest);

            // Track overall best
            if (currentBestCost < overallMinCost) {
                overallMinCost = currentBestCost;
                overallBestChromosome = currentGenerationBest;
            }

            // 2. Select Parents
            List<List<Integer>> parents = selectParents(population);

            // 3. Create the next generation
            List<List<Integer>> nextGeneration = new ArrayList<>();

            // *** Implement Elitism: Add the best chromosome from the current generation to the next ***
            nextGeneration.add(currentGenerationBest);

            // 4. Perform Crossover and Mutation to fill the rest of the next generation
            // We need populationSize - 1 more chromosomes
            //int childrenToCreate = populationSize - 1; // One spot is taken by the elite chromosome
            // Ensure we process parents in pairs and create enough children
            // Adjust the loop to create childrenToCreate children instead of populationSize
            for (int i = 0; i < parents.size() && nextGeneration.size() < populationSize; i += 2) { // Ensure we don't exceed population size
                if (i + 1 < parents.size()) {
                    List<Integer> parent1 = parents.get(i);
                    List<Integer> parent2 = parents.get(i+1);

                    List<List<Integer>> children = crossover(parent1, parent2);

                    // Mutate the children
                    mutate(children.get(0));
                    if (children.size() > 1) {
                        mutate(children.get(1));
                    }

                    // Add children to the next generation, but don't exceed populationSize
                    if (nextGeneration.size() < populationSize) {
                        nextGeneration.add(children.get(0));
                    }
                    if (children.size() > 1 && nextGeneration.size() < populationSize) {
                        nextGeneration.add(children.get(1));
                    }
                }
            }


            // 5. Replace population
            this.population = nextGeneration; // This nextGeneration now includes the elite

            // Print status periodically
            if (generation % 100 == 0 || generation == numberOfGenerations - 1) {
                System.out.println("Generation " + generation + ", Best Cost (Current Gen): " + currentBestCost);
                System.out.println("Generation " + generation + ", Best Cost (Overall): " + overallMinCost); // Show overall best too
            }
        }

        System.out.println("Genetic Algorithm finished.");
        System.out.println("Overall best cost found: " + overallMinCost);

        return overallBestChromosome;
    }

    public double calculateFitness(List<Integer> chromosome) {
        double totalCost = 0;
        int numberOfVolunteers = allVolunteers.size();
        Map<Integer, Integer> serviceAssignmentCounts = new HashMap<>();

        for (int i = 0; i < numberOfVolunteers; i++) {
            int assignedServiceId = chromosome.get(i);
            Volunteer currentVolunteer = allVolunteers.get(i);
            Service assignedService = serviceMap.get(assignedServiceId);

            Preference matchingPreference = currentVolunteer.getPreferenceForService(assignedService);

            if (matchingPreference != null) {
                totalCost += matchingPreference.getCostFactor();
            } else {
                totalCost += NON_PREFERRED_PENALTY;
            }

            serviceAssignmentCounts.put(assignedServiceId, serviceAssignmentCounts.getOrDefault(assignedServiceId, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry : serviceAssignmentCounts.entrySet()) {
            int serviceId = entry.getKey();
            int assignedCount = entry.getValue();
            Service service = serviceMap.get(serviceId);

            if (service != null && assignedCount > service.getCapacity()) {
                totalCost += CAPACITY_VIOLATION_PENALTY;
            }
        }

        return totalCost;
    }

    private List<Integer> getBestChromosome() {
         List<Integer> bestChromosome = null;
        double minCost = Double.MAX_VALUE;

        for (List<Integer> chromosome : population) {
            double currentCost = calculateFitness(chromosome);
            if (currentCost < minCost) {
                minCost = currentCost;
                bestChromosome = chromosome;
            }
        }
        return bestChromosome;
    }

    private List<List<Integer>> selectParents(List<List<Integer>> currentPopulation) {
        List<List<Integer>> parents = new ArrayList<>();
        int populationSize = currentPopulation.size();

        for (int i = 0; i < populationSize; i++) {
            List<Integer> selectedParent = tournamentSelection(currentPopulation);
            parents.add(selectedParent);
        }

        return parents;
    }

    private List<Integer> tournamentSelection(List<List<Integer>> currentPopulation) {
        List<Integer> tournamentContestantsIndices = new ArrayList<>();
        int populationSize = currentPopulation.size();

        while (tournamentContestantsIndices.size() < tournamentSize) {
            int randomIndex = random.nextInt(populationSize);
            if (!tournamentContestantsIndices.contains(randomIndex)) {
                tournamentContestantsIndices.add(randomIndex);
            }
        }

        List<Integer> bestChromosome = null;
        double bestFitness = Double.MAX_VALUE;

        for (int index : tournamentContestantsIndices) {
            List<Integer> contestant = currentPopulation.get(index);
            double fitness = calculateFitness(contestant);

            if (fitness < bestFitness) {
                bestFitness = fitness;
                bestChromosome = contestant;
            }
        }

        return bestChromosome;
    }

    private List<List<Integer>> crossover(List<Integer> parent1, List<Integer> parent2) {
        List<List<Integer>> children = new ArrayList<>();
        if (parent1.size() != parent2.size()) {
            throw new IllegalArgumentException("Parent chromosomes must have the same length.");
        }

        int chromosomeLength = parent1.size();
        int crossoverPoint = random.nextInt(chromosomeLength - 1) + 1;

        List<Integer> child1 = new ArrayList<>(chromosomeLength);
        List<Integer> child2 = new ArrayList<>(chromosomeLength);

        for (int i = 0; i < chromosomeLength; i++) {
            if (i < crossoverPoint) {
                child1.add(parent1.get(i));
                child2.add(parent2.get(i));
            } else {
                child1.add(parent2.get(i));
                child2.add(parent1.get(i));
            }
        }

        children.add(child1);
        children.add(child2);

        return children;
    }

    private void mutate(List<Integer> chromosome) {
        int chromosomeLength = chromosome.size();
        int numberOfServices = allServices.size();

        for (int i = 0; i < chromosomeLength; i++) {
            if (random.nextDouble() < mutationRate) {
                int currentServiceId = chromosome.get(i);
                int newServiceId = currentServiceId;

                // Generate a new random service ID until it's different from the current one
                while (newServiceId == currentServiceId) {
                    newServiceId = random.nextInt(numberOfServices) + 1;
                }

                chromosome.set(i, newServiceId);
            }
        }
    }
}
