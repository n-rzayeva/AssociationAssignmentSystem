package associationassignmentsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
// import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;


public class GuiMain extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(GuiMain.class.getResource("/fxml/MainLayout.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Volunteer Client GUI");
        // stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png"))); // Optional icon
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
