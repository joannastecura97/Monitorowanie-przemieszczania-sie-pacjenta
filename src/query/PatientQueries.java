package query;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import model.Patient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * <p>Klasa służąca do obsługi komunikacji z bazą danych Firebase </p>
 *
 *
 */
public class PatientQueries {

    private Firestore db;


    /**
     * <p>Konstruktor w którym tworzymy połączenie z bazą firebase</p>
     *
     */
    public PatientQueries() throws FileNotFoundException {

        FileInputStream serviceAccount = new FileInputStream("ServiceAccountKey.json");
        FirebaseOptions firestoreOptions = null;
        try {
            firestoreOptions = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
        }

        FirebaseApp.initializeApp(firestoreOptions);
        db = FirestoreClient.getFirestore();
    }

    /**
     * <p>Metoda zwracająca pacjentów w dostępnych w bazie danych firebase w dokumencie "patients" </p>
     * @return List<Patient> lista pacjentów z bazy
     */
    public List<Patient> getPatients() {

        ApiFuture<QuerySnapshot> query = db.collection("patients").get();
        // ...
        // query.get() blocks on response

        QuerySnapshot querySnapshot = null;
        try {
            querySnapshot = query.get();

        } catch (Exception e) {
        e.printStackTrace();
    }
        List<QueryDocumentSnapshot> documents = null;
        if (querySnapshot != null) {
            documents = querySnapshot.getDocuments();
        }
        List<Patient> patients = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {

            Patient patient = new Patient(document.getString("first_name"),
                    document.getString("last_name"),
                    document.getString("hospitalWard"),
                    document.getLong("identifier"),
                    document.getString("armbandID"),
                    (ArrayList<String>) document.get("lastSeen"));
            if (document.getString("armbandID") == null) {
                patient.setArmbandID("");
            }
            patients.add(patient);

            System.out.println(patient.toString());


        }
        Collections.sort(patients, new SortByLastName());
        return patients;

    }

    /**
     * <p>Metoda służy do pobrania z bazy pacjentów do których przypisany jest unikatowy nr ID </p>
     * @param armbandID numer identyfikacyjny opaski
     * @return type Patient
     */
    public Patient getPatientByArmbandID(String armbandID) {

        CollectionReference cr = db.collection("patients");
        Query query = cr.whereEqualTo("armbandID", armbandID);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        Patient patient = null;
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                patient = new Patient(document.getString("first_name"),
                        document.getString("last_name"),
                        document.getString("hospitalWard"),
                        document.getLong("identifier"),
                        document.getString("armbandID"),
                        (ArrayList<String>) document.get("lastSeen"));
            }
            return patient;
        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
        }
        return null;
    }


    /**
     * <p>Metoda dodająca do bazy danych pacjenta o podanych przez użytkownika aplikacji danych </p>
     * @param firstName imie pacjenta
     *  @param lastName nazwisko pacjenta
     *   @param hospitalWard oddział szpitalny w którym akutalnie się znajduje
     *    @param pesel  nr pesel
     * @return type void
     */
    public void addPatient(String firstName, String lastName, String hospitalWard, long pesel) throws InterruptedException {

        DocumentReference docRef = db.collection("patients").document();
        ArrayList lastSeen = new ArrayList();
        Map<String, Object> data = new HashMap<>();
        data.put("first_name", firstName);
        data.put("last_name", lastName);
        data.put("hospitalWard", hospitalWard);
        data.put("identifier", pesel);
        data.put("armbandID", "");
        data.put("lastSeen", lastSeen);

        ApiFuture<WriteResult> result = docRef.set(data);
        try {

            System.out.println("Update time : " + result.get().getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
        }
    }
    /**
     * <p>Metoda przypisująca opaske pacjentowi </p>
     *   @param armbandID numer ID opaski
     *    @param pesel  nr pesel
     * @return type void
     */
    public void addArmbandForPatient(String armbandID, long pesel) throws ExecutionException, InterruptedException {

        String id = getPatientID(pesel);


        Map<String, Object> update = new HashMap<>();
        update.put("armbandID", armbandID);

        ApiFuture<WriteResult> writeResult =
                db
                        .collection("patients")
                        .document(id)
                        .set(update, SetOptions.merge());

        System.out.println("Update time : " + writeResult.get().getUpdateTime());
    }


    /**
     * <p>Metoda przypisuje aktualną sale dla pacjenta o podanym numerze ID opaski </p>
     *   @param lastSeen  ostatnio widziany oddział
     *    @param pesel  nr pesel
     * @param id1 id opaski
     * @return type void
     */
    public void addRoomForPtient(String lastSeen, long pesel, String id1) throws ExecutionException, InterruptedException {

        String id = getPatientID(pesel);


        Map<String, Object> update = new HashMap<>();
        ArrayList<String> polska = new ArrayList<>(getPatientByArmbandID(id1).getLocalizations());
        polska.add(lastSeen);
        ArrayList<String> polska2 = new ArrayList<>();

            for (int i = 0; i<3; i++) {
                if(polska.size()-i > 0) {
                    polska2.add(polska.get(polska.size() - 1 - i));
                }
            }

        update.put("lastSeen", polska2);

        ApiFuture<WriteResult> writeResult =
                db
                        .collection("patients")
                        .document(id)
                        .set(update, SetOptions.merge());

        System.out.println("Update time : " + writeResult.get().getUpdateTime());
    }

    /**
     * <p>Metoda zwraca aktualne miejsce pacjenta ( numer sali) </p>
     *   @param MAC numer mac urządzenia
     * @return type String ( numer sali)
     */
    public String getRoomNumberByMAC(String MAC) throws ExecutionException, InterruptedException {

        CollectionReference cr = db.collection("RFIDID");
        Query query = cr.whereEqualTo("MAC", MAC);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        String RoomNumber = null;
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                RoomNumber = document.getString("RoomNumber");
            }
            return RoomNumber;
        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>Metoda usuwa pacjenta z bazy danych </p>
     *    @param pesel  nr pesel
     * @return type void
     */
    public void deletePatient(long pesel) throws ExecutionException, InterruptedException {

        String id = getPatientID(pesel);
        ApiFuture<WriteResult> writeResult = db.collection("patients").document(id).delete();
        System.out.println("Update time : " + writeResult.get().getUpdateTime());


    }

    /**
     * <p>Metoda zwracająca numer ID opaski pacjenta</p>
     *    @param pesel  nr pesel
     * @return type String (id opaski pacjenta)
     */
    public String getPatientID(long pesel) throws ExecutionException, InterruptedException {

        CollectionReference cr = db.collection("patients");
        Query query = cr.whereEqualTo("identifier", pesel);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        String id = null;
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                id = document.getId();
            }
        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
        }
        return id;
    }

    /**
     * <p>Metoda zwracająca hasło użytkownika z bazy </p>
     *
     *    @param login  login użytkownika
     * @return type string ( zwraca hasło użytkownika)
     */
    public String getUserPassword(String login) {

        CollectionReference cr = db.collection("personel");
        Query query = cr.whereEqualTo("login", login);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        String password = null;
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                password = document.getString("password");
            }
            return password;
        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
        }
        return null;
    }


}
