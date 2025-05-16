package associationassignmentsystem;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;


public class AssignmentServer {
    private static final int PORT = 12345;
    private AssignmentEngine assignmentEngine;
    private VolunteerManager volunteerManager;
    private AssignmentManager assignmentManager;
    private List<Service> services;
    private final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

    public AssignmentServer() {
        services = Arrays.asList(
                new Service("Food Service"),
                new Service("Reception"),
                new Service("Logistics"),
                new Service("Security"),
                new Service("Cleaning"),
                new Service("Accounting"),
                new Service("IT Support"),
                new Service("Procurement"),
                new Service("Marketing and Communications"),
                new Service("Human Resources")
        );
        volunteerManager = new VolunteerManager();
        assignmentEngine = new AssignmentEngine(volunteerManager.getVolunteers(), services);
        assignmentManager = new AssignmentManager(volunteerManager.getVolunteers(), services);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Assignment Server started on port " + PORT);

            while (true) {
                System.out.println("Waiting for a client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from " + clientSocket.getInetAddress().getHostAddress());

                ServerClientHandler handler = new ServerClientHandler(clientSocket, this);
                clientProcessingPool.submit(handler);
            }

        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
            e.printStackTrace();
        } finally {
            clientProcessingPool.shutdown();
            try {
                if (!clientProcessingPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Client processing pool did not terminate gracefully.");
                    clientProcessingPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                clientProcessingPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // --- Server Methods for ClientHandler ---
    // 1. Create Volunteer
    public int createVolunteer() {
        return volunteerManager.createVolunteer();
    }

    // 2. Set Volunteer Preferences (POST)
    public void setVolunteerPreferences(int volunteerId, List<Integer> serviceIds) {
        volunteerManager.setVolunteerPreferences(volunteerId, serviceIds, this.services);
    }

    // 3. Update Volunteer Preferences (PUT)
    public void updateVolunteerPreferences(int volunteerId, List<Integer> serviceIds) {
        volunteerManager.updateVolunteerPreferences(volunteerId, serviceIds, this.services);
    }

    // 4. Trigger Optimization
    public void triggerOptimization() {
        System.out.println("Optimization triggered.");
        this.assignmentEngine.setVolunteers(volunteerManager.getVolunteers()); // Update the volunteers in the engine.
        List<Integer> results = this.assignmentEngine.runGeneticAlgorithm();
        assignmentManager.setBestAssignmentResult(results);

        for (int i = 0; i < results.size(); i++) {
            Service assignedService = services.get(results.get(i));
            volunteerManager.getVolunteers().get(i).setAssignedService(assignedService);
        }

        System.out.println("Optimization finished.");
    }

    // 5. Get Assignments (General View)
    public List<JSONObject> getAssignments() {
        return assignmentManager.getAssignments();
    }

    // 6. Get Detailed Assignments View
    public List<JSONObject> getDetailedAssignments() {
        return assignmentManager.getDetailedAssignments();
    }

    public static void main(String[] args) {
        AssignmentServer server = new AssignmentServer();
        server.start();
    }
}
