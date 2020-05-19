package RFID;

public interface Subject {
    void addObserver(Observer o);
    void notifyObservers(String MAC,String BandID);
}
