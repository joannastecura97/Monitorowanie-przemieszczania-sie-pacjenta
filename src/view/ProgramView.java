package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import sample.Main;

import java.io.IOException;

public class ProgramView {

        public ProgramView(){
        }


        public Scene getScene() throws IOException {
            Parent root = FXMLLoader.load(getClass().getResource("/Data.fxml"));
            Scene scene = new Scene(root, Main.Bounds.WIDTH, Main.Bounds.HEIGHT);
            scene.getStylesheets().add(getClass().getResource("/stylesheet.css").toExternalForm());
            return scene;
        }
    }

