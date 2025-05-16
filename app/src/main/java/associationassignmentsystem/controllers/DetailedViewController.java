package associationassignmentsystem.controllers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.control.Separator;


public class DetailedViewController {
    @FXML
    private VBox detailedAssignmentsContainer;

    @FXML
    public void initialize() {
        loadDetailedAssignments();
    }

    private void loadDetailedAssignments() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:12345/assignments/detailed"))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray data = new JSONArray(response.body());

            for (int i = 0; i < data.length(); i++) {
                JSONObject service = data.getJSONObject(i);

                VBox card = new VBox(5);
                card.setStyle("-fx-padding: 10; -fx-background-color: #f3f3f3; -fx-border-color: #ccc; -fx-border-radius: 6; -fx-background-radius: 6;");

                Label serviceName = new Label("🧾 " + service.getString("serviceName") + " (Capacity: " + service.getInt("capacity") + ")");
                serviceName.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

                JSONArray assigned = service.getJSONArray("assignedVolunteers");

                if (assigned.length() == 0) {
                    Text noOne = new Text("No volunteers assigned.");
                    noOne.setFill(Color.GRAY);
                    card.getChildren().addAll(serviceName, noOne);
                } else {
                    VBox volunteerList = new VBox(3);
                    for (int j = 0; j < assigned.length(); j++) {
                        JSONObject volunteer = assigned.getJSONObject(j);
                        String vName = volunteer.getString("volunteerName");
                        int cost = volunteer.getInt("cost");

                        Label vLabel = new Label("• " + vName + " (Cost: " + cost + ")");
                        volunteerList.getChildren().add(vLabel);
                    }
                    card.getChildren().addAll(serviceName, volunteerList);
                }

                card.getChildren().add(new Separator());
                detailedAssignmentsContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("⚠️ Failed to load detailed assignments.");
            errorLabel.setTextFill(Color.RED);
            detailedAssignmentsContainer.getChildren().add(errorLabel);
        }
    }
}
