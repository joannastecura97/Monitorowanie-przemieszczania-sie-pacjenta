package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Patient;
import query.PatientQueries;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import RFID.*;


/**
 * <p>Graficzny interfejs użytkownika obsługujący zdarzenia </p>
 */
@SuppressWarnings("Duplicates")
public class PatientController implements Observer {


    @FXML
    private ListView<Patient> listView;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField peselTextField;

    @FXML
    private TextField hospitalWardTextField;

    @FXML
    private TextField armBandIDTextField;

    @FXML
    private TextField findByArmBardIDTextField;

    @FXML
    private TextField lastSeenTextField;

    @FXML
    private Button addPatientButton;

    @FXML
    private Button deletePatientButton;

    @FXML
    private Button saveChangesButton;

    @FXML
    private Label lastSeenLabel;

    @FXML
    private Label armbandIDLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Button addArmbandIDButton;

    @FXML
    private Button findButton;

    @FXML
    private Button showAllButton;


    private static PatientQueries patientQueries;

    private final ObservableList<Patient> patientsList = FXCollections.observableArrayList();

    public static PatientQueries getPatientQueries() {
        return patientQueries;
    }


    public void initialize() {
        try {
            patientQueries = new PatientQueries();

        } catch (FileNotFoundException e) {
//            e.printStackTrace();
        }


        listView.setItems(patientsList);
        getAllPatients();
        setInformation(listView.getSelectionModel().getSelectedItem());


        //gdy zmienimy wybraną osobę to zmeni wyświetlany wynik
        listView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
                    setInformation(newValue);
                }
        );

        RFIDReader.getRfidReader().addObserver(this);
            try {
                for (String IP : RFIDReader.getRfidReader().identifyNetwork()
                ) {
                    RFIDReader.getRfidReader().read(IP);
                }


        } catch (UnknownHostException e) {
        }

    }



    private void getAllPatients() {
        patientsList.setAll(patientQueries.getPatients());
        selectFirst();
    }

    private void selectFirst() {
        listView.getSelectionModel().selectFirst();
    }


    /**
     * <p>Metoda służy do wypełnienia pól tekstowych danymi o użytkowniku</p>
     *   @param patient pacjent którego dane chcemy wyświetlić
     *  @return type void
     */
    private void setInformation(Patient patient) {
        if (patient != null) {
            firstNameTextField.setText(patient.getFirstName());
            lastNameTextField.setText(patient.getLastName());
            peselTextField.setText(String.valueOf(patient.getPesel()));
            armBandIDTextField.setText(String.valueOf(patient.getArmbandID()));
            hospitalWardTextField.setText(patient.getHospitalWard());
            if (patient.getLocalizations() != null) {
                lastSeenTextField.setText(patient.getLocalizations().toString());


            }
        } else {
            clearTextFields();
        }
    }

    //szukamy po konkretnej opasce

    /**
     * <p>Metoda wyświetla pacjenta o podanym adresie opaski  </p>
     * @return type void
     */
    @FXML
    void findButtonPressed(ActionEvent event) {
        if (findButton.getText().equals("Szukaj")) {
            Patient patient = patientQueries.getPatientByArmbandID(findByArmBardIDTextField.getText());

            patientsList.setAll(patient);
            selectFirst();
        } else {

            findButton.setText("Szukaj");
            addPatientButton.setOpacity(1);
            deletePatientButton.setOpacity(1);
            saveChangesButton.setOpacity(1);
            showAllButton.setOpacity(1);
            addArmbandIDButton.setText("Dodaj opaskę");
        }
    }


    @FXML
    void showAllButtonPressed(ActionEvent event) {
        getAllPatients();

    }


    /**
     * <p>Metoda służy do dodawania pacjentów do bazy danych</p>
     * @return type void
     */
    @FXML
    void addPatientPressed(ActionEvent event) {

        System.out.println(addPatientButton.getText());

        if (addPatientButton.getText().equals("Dodaj pacjenta")) {
            clearTextFields();

            firstNameTextField.setEditable(true);
            lastNameTextField.setEditable(true);
            peselTextField.setEditable(true);
            hospitalWardTextField.setEditable(true);
            String style = " -fx-background-color: #CCFFFF; -fx-border-color: #006699;-fx-border-radius: 25px;-fx-background-radius: 25px";
            firstNameTextField.setStyle(style);
            lastNameTextField.setStyle(style);
            peselTextField.setStyle(style);
            hospitalWardTextField.setStyle(style);
            lastSeenLabel.setOpacity(0);
            armbandIDLabel.setOpacity(0);
            saveChangesButton.setOpacity(0);
            deletePatientButton.setOpacity(0);
            cancelButton.setOpacity(1);
            cancelButton.setText("Anuluj dodawanie pacjenta");
            addPatientButton.setText("Zapisz zmiany");


        } else {


            try {
                patientQueries.addPatient(firstNameTextField.getText(), lastNameTextField.getText(), hospitalWardTextField.getText(), (Long.parseLong(peselTextField.getText())));

                alert("Dodano nowego pacjenta",
                        "Próba dodania nowego pacjenta zakończona powodzeniem");
            } catch (InterruptedException e) {
                alert("Nie dodano nowego pacjenta",
                        "Próba dodania nowego pacjenta zakończona niepowodzeniem");
            }


            firstNameTextField.setEditable(false);
            lastNameTextField.setEditable(false);
            peselTextField.setEditable(false);
            hospitalWardTextField.setEditable(false);
            firstNameTextField.setStyle("-fx-background-color: transparent");
            lastNameTextField.setStyle("-fx-background-color: transparent");
            peselTextField.setStyle("-fx-background-color: transparent");
            hospitalWardTextField.setStyle("-fx-background-color: transparent");
            addPatientButton.setText("Dodaj pacjenta");
            lastSeenLabel.setOpacity(1);
            armbandIDLabel.setOpacity(1);
            saveChangesButton.setOpacity(1);
            deletePatientButton.setOpacity(1);
            cancelButton.setOpacity(0);

            getAllPatients();
        }


    }

    /**
     * <p>Metoda służąca do usuwania pacjentów z bazy danych</p>
     * @return type void
     */
    @FXML
    void deletePatientButtonPressed(ActionEvent event) {

        try {
            patientQueries.deletePatient(Long.parseLong(peselTextField.getText()));
            alert("Usunięto pacjenta",
                    "Pomyślnie zakończona próba usunięcia pacjenta");
        } catch (ExecutionException | InterruptedException e) {
            alert("Nie usunięto pacjenta",
                    "Próba usunięcia pacjenta zakończona niepowodzeniem");
        }
    }


    /**
     * <p>Metoda służaca do przypisywania ID opaski do pacjenta </p>
     * @return type void
     */
    @FXML
    void addArmbandIDButtonPressed(ActionEvent event) {

        if (addArmbandIDButton.getText().equals("Dodaj opaskę")) {
            findButton.setText("Anuluj");
            addPatientButton.setOpacity(0);
            deletePatientButton.setOpacity(0);
            saveChangesButton.setOpacity(0);
            showAllButton.setOpacity(0);
            addArmbandIDButton.setText("Zapisz");
        } else {

            try {
                patientQueries.addArmbandForPatient(findByArmBardIDTextField.getText(), Long.parseLong(peselTextField.getText()));
                alert("Dodano opaskę pacjentowi",
                        "Pomyślnie zakończono dodawanie opaski dla pacjenta");
            } catch (ExecutionException | InterruptedException e) {
                alert("Nie dodano opaski pacjentowi",
                        "Dodawanie opaski dla pacjenta zakończone niepowodzeniem");
            }
            getAllPatients();
            findButton.setText("Szukaj");
            addPatientButton.setOpacity(1);
            deletePatientButton.setOpacity(1);
            saveChangesButton.setOpacity(1);
            showAllButton.setOpacity(1);
            addArmbandIDButton.setText("Dodaj opaskę");

            findByArmBardIDTextField.setText("");
        }


    }

    /**
     * <p>Metoda służy do zapisania wprowadzonych zmian</p>
     * @return type void
     */
    @FXML
    void saveChangesButtonPressed(ActionEvent event) {


        if (saveChangesButton.getText().equals("Edytuj dane pacjenta")) {

            firstNameTextField.setEditable(true);
            lastNameTextField.setEditable(true);
            peselTextField.setEditable(true);
            hospitalWardTextField.setEditable(true);
            String style = " -fx-background-color: #CCFFFF; -fx-border-color: #006699;-fx-border-radius: 25px;-fx-background-radius: 25px";
            firstNameTextField.setStyle(style);
            lastNameTextField.setStyle(style);
            peselTextField.setStyle(style);
            hospitalWardTextField.setStyle(style);
            addPatientButton.setOpacity(0);
            deletePatientButton.setOpacity(0);
            cancelButton.setOpacity(1);
            cancelButton.setText("Anuluj edycję");
            saveChangesButton.setText("Zapisz zmiany");


        } else {


            try {
                patientQueries.deletePatient(Long.parseLong(peselTextField.getText()));
                patientQueries.addPatient(firstNameTextField.getText(), lastNameTextField.getText(), hospitalWardTextField.getText(), Long.parseLong(peselTextField.getText()));
                patientQueries.addArmbandForPatient(armBandIDTextField.getText(), Long.parseLong(peselTextField.getText()));
                alert("Zmodyfikowano dane pacjenta",
                        "Pomyślnie zakończona modyfikacja danych pacjenta");
            } catch (ExecutionException | InterruptedException e) {
                alert("Nie zmodyfikowano danych pacjenta",
                        "Próba modyfikacji danych pacjenta zakończona niepowodzeniem");
            }

            firstNameTextField.setEditable(false);
            lastNameTextField.setEditable(false);
            peselTextField.setEditable(false);
            hospitalWardTextField.setEditable(false);
            firstNameTextField.setStyle("-fx-background-color: transparent");
            lastNameTextField.setStyle("-fx-background-color: transparent");
            peselTextField.setStyle("-fx-background-color: transparent");
            hospitalWardTextField.setStyle("-fx-background-color: transparent");
            saveChangesButton.setText("Edytuj dane pacjenta");
            addPatientButton.setOpacity(1);
            deletePatientButton.setOpacity(1);
            cancelButton.setOpacity(0);

            getAllPatients();
        }

    }

    @FXML
    void cancelButtonPressed(ActionEvent event) {

        if (cancelButton.getText().equals("Anuluj dodawanie pacjenta")) {

            firstNameTextField.setEditable(false);
            lastNameTextField.setEditable(false);
            peselTextField.setEditable(false);
            hospitalWardTextField.setEditable(false);
            firstNameTextField.setStyle("-fx-background-color: transparent");
            lastNameTextField.setStyle("-fx-background-color: transparent");
            peselTextField.setStyle("-fx-background-color: transparent");
            hospitalWardTextField.setStyle("-fx-background-color: transparent");
            addPatientButton.setText("Dodaj pacjenta");
            lastSeenLabel.setOpacity(1);
            armbandIDLabel.setOpacity(1);
            saveChangesButton.setOpacity(1);
            deletePatientButton.setOpacity(1);
            cancelButton.setOpacity(0);

            getAllPatients();
        } else {

            firstNameTextField.setEditable(false);
            lastNameTextField.setEditable(false);
            peselTextField.setEditable(false);
            hospitalWardTextField.setEditable(false);
            firstNameTextField.setStyle("-fx-background-color: transparent");
            lastNameTextField.setStyle("-fx-background-color: transparent");
            peselTextField.setStyle("-fx-background-color: transparent");
            hospitalWardTextField.setStyle("-fx-background-color: transparent");
            saveChangesButton.setText("Edytuj dane pacjenta");
            lastSeenLabel.setOpacity(1);
            armbandIDLabel.setOpacity(1);
            addPatientButton.setOpacity(1);
            deletePatientButton.setOpacity(1);
            cancelButton.setOpacity(0);

            getAllPatients();

        }

    }

    private void alert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearTextFields() {

        firstNameTextField.clear();
        lastNameTextField.clear();
        peselTextField.clear();
        armBandIDTextField.clear();
        hospitalWardTextField.clear();
        lastSeenTextField.clear();

    }


    @Override
    public void update(String MAC, String bandID) {
        if (addArmbandIDButton.getText().equals("Zapisz"))
            findByArmBardIDTextField.setText(bandID);
        else {
            System.out.println(MAC + "   " + bandID);
            Patient patient = patientQueries.getPatientByArmbandID(bandID);
            try {
                String RoomNumber = patientQueries.getRoomNumberByMAC(MAC);
                System.out.println(RoomNumber);
                patientQueries.addRoomForPtient(RoomNumber, patient.getPesel(), patient.getArmbandID());
            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
            }

        }
    }
}

