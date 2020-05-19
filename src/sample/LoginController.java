package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import query.PatientQueries;


/**
 * <p>Klasa służy do uwierzytelnienia użytkownika i zweryfikowania jego danych z bazą </p>

 */
public class LoginController {

        @FXML
        private BorderPane background;

        @FXML
        private TextField loginTextField;

        @FXML
        private TextField passwordTextField;

        @FXML
        private Button loginButton;

        private PatientQueries patientQueries;



        @FXML
        void loginButtonPressed(ActionEvent event) {

                patientQueries=PatientController.getPatientQueries();
                String password =patientQueries.getUserPassword(loginTextField.getText());
                try {
                        if (password.equals(passwordTextField.getText())) {

                                Main.getStage().setScene(Main.getSceneMap().get(ViewName.PROGRAM));
                        } else {
                                loginTextField.setText("");
                                passwordTextField.setText("");
                                alert(
                                );
                        }
                }
                catch (NullPointerException e){
                        alert(
                        );
                }
        }

        private void alert(){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Logowanie zakończone niepowodzeniem");
                alert.setContentText("Błędny login lub hasło");
                alert.showAndWait();
        }


}

