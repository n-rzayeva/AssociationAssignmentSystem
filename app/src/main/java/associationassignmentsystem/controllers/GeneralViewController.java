package associationassignmentsystem.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class GeneralViewController {
    @FXML
    private VBox serviceListVBox;

    @FXML
    public void initialize() {
        fetchGeneralAssignments();
    }

    private void fetchGeneralAssignments() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:12345/assignments"))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(this::renderAssignments)
                    .exceptionally(e -> {
                        e.printStackTrace();
                        return null;
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderAssignments(String responseBody) {
        JSONArray assignments = new JSONArray(responseBody);

        javafx.application.Platform.runLater(() -> {
            serviceListVBox.getChildren().clear();
            for (int i = 0; i < assignments.length(); i++) {
                JSONObject service = assignments.getJSONObject(i);
                String serviceName = service.getString("serviceName");
                int filled = service.getInt("filled");
                int capacity = service.getInt("capacity");

                Label label = new Label(serviceName + ": " + filled + "/" + capacity + " assigned");
                label.setStyle("-fx-font-size: 14px; -fx-padding: 4px;");
                serviceListVBox.getChildren().add(label);
            }
        });
    }
}
