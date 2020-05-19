package model;

import java.util.ArrayList;

public class Patient {



    private String firstName;
    private String lastName;
    private String hospitalWard;
    private String armbandID;
    private long pesel;
    private ArrayList<String> localizations;



    public Patient(String firstName, String lastName, String hospitalWard, long pesel, String armbandID, ArrayList<String> localizations) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.hospitalWard = hospitalWard;
        this.pesel = pesel;
        this.armbandID = armbandID;
        this.localizations = localizations;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHospitalWard() {
        return hospitalWard;
    }

    public void setHospitalWard(String hospitalWard) {
        this.hospitalWard = hospitalWard;
    }

    public long getPesel() {
        return pesel;
    }

    public void setPesel(long pesel) {
        this.pesel = pesel;
    }

    public String getArmbandID() {
        return armbandID;
    }

    public void setArmbandID(String armbandID) {
        this.armbandID = armbandID;
    }

    public ArrayList<String> getLocalizations() {
        return localizations;
    }

    public void setLocalizations(ArrayList<String> localizations) {
        this.localizations = localizations;
    }

    @Override
    public String toString() {
        return lastName + " "+ firstName;
    }
}
