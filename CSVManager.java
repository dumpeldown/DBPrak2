import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVManager{
    /**
     * liest die CSV-Datei Kunde.csv aus und schreibt alle daten aus einer zeile in ein String-Array, welches dann in
     * eine Liste geschrieben wird.
     * @return Liste mit String-Arrays, welche alle Kundendaten aus der CSV enthält.
     */
    public static ArrayList<String[]> parseAllLines(){
        BufferedReader bf = null;
        try{
            bf = new BufferedReader(new FileReader("KUNDE.CSV"));
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        String zeile;
        ArrayList<String[]> kunden = new ArrayList<>();
        try{
            //Erste Zeile mit Headern wegwerfen.
            bf.readLine();
            while((zeile = bf.readLine()) != null){
                    kunden.add(zeile.split(";"));
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return kunden;
    }
}
