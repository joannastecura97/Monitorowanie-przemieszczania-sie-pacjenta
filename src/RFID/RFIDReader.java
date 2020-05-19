package RFID;

import org.jsoup.Jsoup;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


/**
 * <p>Klasa do obsługi łączności z urządzeniem
 * <a href="https://inveo.com.pl/czytniki-rfid/rfid-nano/">Link do urządzenia</a>
 * </p>
 */

public class RFIDReader implements Subject {


    /**
     * @param observers Lista klas - obserwatorów
     */



    private List<Observer> observers = new LinkedList<>();

    private static RFIDReader rfidReader = new RFIDReader();

    public static RFIDReader getRfidReader() {
        return rfidReader;
    }

    public void read(String ip) {
        new Thread(() -> startRead(ip)).start();
    }


    /**
     * <p>Metoda łączy się z cegłą używająć standardowego hasła i zczytuje z niej dane </p>
     * @param ip to numer ip cegły
     * @return type void
     */
    private void startRead(String ip) {

        try {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("admin", "admin00".toCharArray());
                }
            });



            URL url = new URL("http://" + ip + "/status.xml?releaseId=0");


            StringBuilder result = new StringBuilder();
            StringBuilder previous = new StringBuilder();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            BufferedReader rd = new BufferedReader(inputStreamReader);

            String line, rfidMAC, bandID;


            while (true) {

                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                Thread.sleep(100);

                conn = (HttpURLConnection) url.openConnection();

                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                if (!previous.toString().equals(result.toString()) && !previous.toString().equals("")) {
                    rfidMAC = Jsoup.parse(result.toString()).body().child(0).child(1).text();

                    bandID = Jsoup.parse(result.toString()).body().child(0).child(2).text();

                    notifyObservers(rfidMAC, bandID);

                    previous.setLength(0);

                    previous.append(result);

                }

                previous.setLength(0);
                previous.append(result);
                result.setLength(0);

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception during reading RFID");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            startRead(ip);    // czas po jakim resetuje sie połączenie po błędzie
        }
    }


    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void notifyObservers(String MAC, String BandID) {
        for (Observer o : observers
        ) {
            o.update(MAC, BandID);
        }
    }


    /**
     * <p>Metoda służy do identyfikacji dostępnych urządzeń w sieci  </p>
     * @return Vector <String> zawierający adresy IP dostępnych urządzeń
     */

    public Vector<String> identifyNetwork() throws UnknownHostException {


        Vector<String> Available_Devices = new Vector<>();
        String myip = InetAddress.getLocalHost().getHostAddress(); // nasze ip
        if (myip.equals("127.0.0.1")) {
            System.out.println("This PC is not connected to any network!");
        } else {
            String mynetworkips = "";


            for (int i = myip.length() - 1; i >= 0; --i) {
                if (myip.charAt(i) == '.') {
                    mynetworkips = myip.substring(0, i + 1);
                    break;
                }
            }

            System.out.println("My Device IP: " + myip + "\n");


            for (int i = 10; i <= 20; ++i) {
                try {
                    InetAddress addr = InetAddress.getByName(mynetworkips + i);

                    if (addr.isReachable(1000) && !addr.getHostAddress().equals(myip)) { // timeout 1sekunda
                    System.out.println("Available: " + addr.getHostAddress() ); // pokazuje czy jest dostepne
                        Available_Devices.add(addr.getHostAddress());
                    }else  System.out.println( "Not avaible: " +  addr.getHostAddress());


                } catch (IOException ignored) {
                }
            }


        }
        return Available_Devices;
    }
}
