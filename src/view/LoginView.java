package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import sample.Main;
import sample.ViewName;

import java.io.IOException;

public class LoginView{


    public Scene getScene() throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        Scene scene = new Scene(root, Main.Bounds.WIDTH, Main.Bounds.HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/stylesheet.css").toExternalForm());
        return scene;
    }
}
