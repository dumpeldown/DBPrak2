
import java.util.Scanner;

public class Bestellsystem{
     private static int knr = 0;
     private static Datum dat;
     private static int bestnr;
     private static int artnr;
     private static int eStuecke;

    public static void addNewBestellung(){
        Scanner sc = new Scanner(System.in);
        boolean flag;
        System.out.print("Gib eine gültige Kundennummer ein: ");
        knr = Integer.parseInt(sc.nextLine());
        if(!SQLManager.isValid("kunde","knr", knr)){
            System.out.println("Die eingegebene Kundenummer ist nicht gültig. Bitte erneut versuchen.");
            return;
        }
        System.out.print("Gib ein gültiges Bestelldatum ein: ");
        dat = Datum.parseDate(sc.nextLine());

        SQLManager.writeBestellug(knr, dat);
        do{
            getInput();
            if(
                    !(SQLManager.isValid("artikel", "artnr", artnr)) ||
                    !SQLManager.isValid("bestellung", "bestnr", bestnr))
            {
                System.out.println("Die Artikelnummer oder die Bestellnummer ist nicht gültig, bitte erneut versuchen.");
                flag = false;
            }else{
                flag = true;
            }
        }while(!flag);

        int bstnr = SQLManager.checkAnzahlStuecke(artnr, eStuecke);

        if(bstnr == -1){
            System.out.println("Keinen Lagerbestand mit passender Anzahl von Artikeln gefunden.");
            return;
        }

        SQLManager.writeNewLagerbestand(bstnr, eStuecke);
        SQLManager.writeUpdateOnLagerbestand(bestnr, eStuecke, bstnr);

    }

    private static void getInput(){
        Scanner sc = new Scanner(System.in);
        System.out.print("\nGib eine gültige Bestellnummer ein:");
        bestnr = Integer.parseInt(sc.nextLine());
        System.out.print("\nGib eine gültige Artikelnummer ein:");
        artnr = Integer.parseInt(sc.nextLine());
        System.out.print("\nGib die Anzahl der Stuecke ein, die der Bestellung hinzugefügt werden sollen:");
        eStuecke = Integer.parseInt(sc.nextLine());
    }
}
