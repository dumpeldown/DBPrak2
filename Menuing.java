import java.util.Scanner;

public class Menuing{

    /**
     * Menuführung, mit Aufruf der Klassen-Funktionen der anderen Module.
     */
    public static void menuing(){
        Scanner sc = new Scanner(System.in);
        printMenu();
        int auswahl;
        while((auswahl = Integer.parseInt(sc.nextLine())) >= 0){
            switch(auswahl){
                case 1:{
                    SQLManager.writeKunden(CSVManager.parseAllLines());
                    break;
                }
                case 2:{
                    SQLManager.showAllArtikel();
                    break;
                }
                case 3:{
                    SQLManager.showAllLager();
                    break;
                }
                case 4:{
                    SQLManager.showAllKunden();
                    break;
                }
                case 5:{
                    SQLManager.showLagerbestandForArtnr();
                    break;
                }
                case 6:{
                    SQLManager.updateWert();
                    break;
                }
                case 7:{
                    SQLManager.showArtikelAndBestand();
                    break;
                }
                case 8:{
                    Bestellsystem.addNewBestellung();
                    break;
                }
                case 0:{
                    System.out.println("Programm wird beendet.");
                    ConnectionManager.closeConnection();
                    System.exit(0);
                }
            }
            printMenu();
        }
    }

    private static void printMenu(){
        System.out.println("\nBitte eine Auswahl machen!\n" +
                "1) Eintraege aus KUNDE.CSV in die Datenbank schreiben.\n" +
                "2) SELECT: Anzeige aller Artikel\n" +
                "3) SELECT: Anzeige aller Lager\n" +
                "4) SELECT: Anzeige aller Kunden\n" +
                "5) SELECT: Anzeige aller Lagerbestaende fuer eine Lagernummer\n" +
                "6) UPDATE: Fuer eine ArtNr. den Wert (STUECKE*PREIS) eintragen.\n" +
                "7) Fuer eine ArtNr. den Artikel anzeigen und seine Lagerbestaende.\n" +
                "8) Eine neue Bestellung ins System aufnehmen.\n" +
                "0) Verbindung trennen und Program schliessen.");
    }
}
