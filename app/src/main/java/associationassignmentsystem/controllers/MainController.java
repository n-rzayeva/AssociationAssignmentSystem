package associationassignmentsystem.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class MainController {
    @FXML
    private Label volunteerLabel;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label assignedServiceLabel;

    @FXML
    private VBox sidebar;

    @FXML
    private Button preferencesButton;

    @FXML
    private Button generalButton;

    @FXML
    private Button detailedButton;

    @FXML
    private Button refreshButton;

    private String volunteerId = "V05";
    private String assignedService = "Logistics";

    @FXML
    public void initialize() {
        volunteerLabel.setText("Volunteer: #" + volunteerId);
        assignedServiceLabel.setText(assignedService);
        loadView("GeneralView.fxml");
    }

    @FXML
    private void handlePreferencesView() {
        loadView("PreferencesView.fxml");
    }

    @FXML
    private void handleGeneralView() {
        loadView("GeneralView.fxml");
    }

    @FXML
    private void handleDetailedView() {
        loadView("DetailedView.fxml");
    }

    @FXML
    private void handleRefresh() {
        System.out.println("Refreshing assigned service...");
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            AnchorPane view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
