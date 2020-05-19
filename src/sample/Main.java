package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.LoginView;
import view.ProgramView;


import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class Main extends Application {


    public static class Bounds{
        public static final int WIDTH = 550;
        public static final int HEIGHT = 480;
    }

    //statyczne, by dostawac dostep z kontrolerów widoków
    private static Map<ViewName, Scene> sceneMap = new TreeMap<>();
    //tak samo stage
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Main.primaryStage = primaryStage;

        //poszczególne odsłony (sceny) są w mapie pod kluczami z typu wyliczeniowego ViewName
        primaryStage.setTitle("niebieskaOpaska 2.0");

        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        initSceneMap();
        Scene scene = sceneMap.get(ViewName.LOGIN);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/stylesheet.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static Map<ViewName, Scene> getSceneMap() {
        return sceneMap;
    }

    public static Stage getStage(){
        return primaryStage;
    }

    public static void initSceneMap() throws IOException {
        sceneMap.put(ViewName.LOGIN, new LoginView().getScene());
        sceneMap.put(ViewName.PROGRAM, new ProgramView().getScene());

    }

    public static void main(String[] args) {
        launch(args);

    }
}

