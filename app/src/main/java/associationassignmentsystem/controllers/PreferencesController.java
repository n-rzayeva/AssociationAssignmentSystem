package associationassignmentsystem.controllers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;


public class PreferencesController {
    @FXML private ComboBox<String> pref1Combo;
    @FXML private ComboBox<String> pref2Combo;
    @FXML private ComboBox<String> pref3Combo;
    @FXML private Label statusLabel;

    // Assume service names are known or fetched earlier
    private List<String> serviceNames = Arrays.asList(
            "Food Service", "Reception", "Logistics", "Security", "Cleaning",
            "Accounting", "IT Support", "Procurement", "Marketing and Communications", "Human Resources"
    );

    @FXML
    private void submitPreferences() {
        try {
            int volunteerId = 1; // Replace with actual logic if needed

            String pref1 = pref1Combo.getValue();
            String pref2 = pref2Combo.getValue();
            String pref3 = pref3Combo.getValue();

            List<String> selected = Arrays.asList(pref1, pref2, pref3);

            if (selected.contains(null) || hasDuplicates(selected)) {
                statusLabel.setText("Please select 3 different services.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            List<Integer> preferenceIds = selected.stream()
                    .map(serviceNames::indexOf)
                    .map(i -> i + 1)
                    .toList();

            String jsonPayload = "[" + String.join(",", preferenceIds.stream().map(String::valueOf).toList()) + "]";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:12345/volunteers/" + volunteerId + "/preferences"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                statusLabel.setText("Preferences submitted successfully!");
                statusLabel.setStyle("-fx-text-fill: green;");
            } else {
                statusLabel.setText("Failed: " + response.statusCode());
                statusLabel.setStyle("-fx-text-fill: red;");
            }

        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }


    private boolean hasDuplicates(List<String> list) {
        return list.stream().distinct().count() != list.size();
    }
}
