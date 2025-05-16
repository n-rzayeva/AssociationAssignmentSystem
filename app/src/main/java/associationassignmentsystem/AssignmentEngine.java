package associationassignmentsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class AssignmentEngine {

    private List<Volunteer> volunteers;
    private List<Service> services;
    private Random random = new Random();
    private int populationSize = 100;
    private double mutationRate = 0.01;
    private double crossoverRate = 0.9;
    private int tournamentSize = 5;

    public AssignmentEngine(List<Volunteer> volunteers, List<Service> services) {
        this.volunteers = volunteers;
        this.services = services;
    }

    public void setVolunteers(List<Volunteer> volunteers) {
        this.volunteers = volunteers;
    }

    public List<Integer> runGeneticAlgorithm() {
        List<Chromosome> population = initializePopulation();
        for (int generation = 0; generation < 100; generation++) {
            List<Integer> fitnesses = calculateFitnesses(population);
            List<Chromosome> newPopulation = new ArrayList<>();

            for (int i = 0; i < populationSize; i++) {
                Chromosome parent1 = selectParent(population, fitnesses);
                Chromosome parent2 = selectParent(population, fitnesses);
                Chromosome child = crossover(parent1, parent2);
                mutate(child);
                newPopulation.add(child);
            }

            population = newPopulation;
            int bestFitness = Collections.min(fitnesses);
            System.out.println("Generation " + generation + " Best Fitness: " + bestFitness);
            if (bestFitness == 0) {
                System.out.println("Found optimal solution at generation " + generation);
                break;
            }
        }

        List<Integer> finalFitnesses = calculateFitnesses(population);
        int bestIndex = finalFitnesses.indexOf(Collections.min(finalFitnesses));
        return population.get(bestIndex).getServiceAssignments(); // Return the service assignments from the best chromosome
    }

    private List<Chromosome> initializePopulation() {
        List<Chromosome> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            Chromosome chromosome = new Chromosome(volunteers.size()); // Create a new chromosome
            for (int j = 0; j < volunteers.size(); j++) {
                Volunteer volunteer = volunteers.get(j);
                if (volunteer.getPreferedServices().isEmpty()) {
                    int randomServiceIndex = random.nextInt(services.size());
                    chromosome.setServiceAssignment(j, randomServiceIndex);
                } else {
                    int randomPreferenceIndex = random.nextInt(volunteer.getPreferedServices().size());
                    Service preferredService = volunteer.getPreferedServices().get(randomPreferenceIndex).getService();
                    chromosome.setServiceAssignment(j, services.indexOf(preferredService));
                }
            }
            population.add(chromosome);
        }
        return population;
    }

    private List<Integer> calculateFitnesses(List<Chromosome> population) {
        List<Integer> fitnesses = new ArrayList<>();

        for (Chromosome chromosome : population) {
            int fitness = 0;

            int[] serviceCounts = new int[services.size()];

            for (int i = 0; i < volunteers.size(); i++) {
                Volunteer volunteer = volunteers.get(i);
                int serviceIndex = chromosome.getServiceAssignment(i);
                serviceCounts[serviceIndex]++;

                Service assignedService = services.get(serviceIndex);
                Preference pref = volunteer.getPreferenceForService(assignedService);

                if (pref != null) {
                    int rank = pref.getRank(); // rank = 1 for first choice
                    fitness += (rank - 1) * (rank - 1);  // (i - 1)^2
                } else {
                    int nd = volunteer.getPreferedServices().size();
                    fitness += 10 * nd * nd;  // heavy penalty
                }
            }

            // Penalize over-capacity
            for (int j = 0; j < services.size(); j++) {
                int overflow = serviceCounts[j] - services.get(j).getCapacity();
                if (overflow > 0) {
                    fitness += overflow * 100;  // adjustable penalty
                }
            }

            fitnesses.add(fitness);
        }

        return fitnesses;
    }

    private Chromosome selectParent(List<Chromosome> population, List<Integer> fitnesses) {
        List<Integer> candidates = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            int randomIndex = random.nextInt(populationSize);
            candidates.add(randomIndex);
        }
        int bestCandidate = candidates.get(0);
        for (int i = 1; i < tournamentSize; i++) {
            if (fitnesses.get(candidates.get(i)) < fitnesses.get(bestCandidate)) {
                bestCandidate = candidates.get(i);
            }
        }
        return population.get(bestCandidate);
    }

    private Chromosome crossover(Chromosome parent1, Chromosome parent2) {
        Chromosome child = new Chromosome(parent1.size());
        if (random.nextDouble() < crossoverRate) {
            int crossoverPoint = random.nextInt(parent1.size());
            for (int i = 0; i < parent1.size(); i++) {
                if (i < crossoverPoint) {
                    child.setServiceAssignment(i, parent1.getServiceAssignment(i));
                } else {
                    child.setServiceAssignment(i, parent2.getServiceAssignment(i));
                }
            }
        } else {
            for(int i=0; i< parent1.size(); i++){
                child.setServiceAssignment(i, parent1.getServiceAssignment(i));
            }
        }
        return child;
    }

    private void mutate(Chromosome individual) {
        for (int i = 0; i < individual.size(); i++) {
            if (random.nextDouble() < mutationRate) {
                int randomServiceIndex = random.nextInt(services.size());
                individual.setServiceAssignment(i, randomServiceIndex);
            }
        }
    }
}
