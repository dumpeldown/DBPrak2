import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;


public class Versandplanung{

    public static void plan(){
        LinkedList<Bpd> bpdispo = new LinkedList<>();
        int choice = 0;
        SQLManager.printAllBestellungen(); //alle bestellungen mit status 1 werden
        // angezeigt.
        choice = makeChoice();
        for(Bpd o: fillList(choice)){
            bpdispo.add(o);
            //Linked List mit Objeckten und Attibuten füllen.
        }
        printList(bpdispo); //Linked List ausgeben, ein Knoten/Objekt pro Zeile.
        //ArrayList<Bpd> freieBoxen = SQLManager.getFreieBoxen();
        //printFreie(); //TODO sortieren nach ttyp reihenfolge beachten
        //TODO 8c)

    }


    public static int makeChoice(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Gib die Bestellnummer ein, für die du planen willst. ");
        return sc.nextInt();
    }

    public static ArrayList<Bpd> fillList(int bestnr){
        // TODO: Hier noch an die tabelle box eine anfrage stellen mit "where vbstnr = bestnr" um an vbtyp zu kommen.
        //TODO: algrad = 100, verpackt = false; anzbo = 0;
        /*
        int bstnr;
        String ttyp;
        int artnr;
        String artbez;
        int anzbo;
        int menge;
        int algrad;
        boolean verpackt;
        */

        ArrayList<Bpd> alle = new ArrayList<>();
        ArrayList<int[]> bestellungen = SQLManager.getBestellung(bestnr);
        for(int[] ia: bestellungen){
            alle.add(new Bpd(
                    ia[0],
                    ia[1],
                    ia[2]
            ));
        }
        //Alle zu einer bestellung zugehörige Artikeldaten holen.
        int i = 0;
        for(Bpd o: alle){
            String artbez = SQLManager.getArtikel((alle.get(i)).artnr);
            o.artbez = artbez;
            i++;
        }
        return alle;
    }

    public static void printList(LinkedList<Bpd> bpdispo){
        sortList(bpdispo);
        System.out.println("\n Anzahl der Objecte in bpdispo: "+bpdispo.size());
        for(Bpd o: bpdispo){
            o.toPrint();
        }
    }

    public static void sortList(LinkedList<Bpd> lbest2bstnr){
        ArrayList<Bpd> listOR = new ArrayList<>();
        ArrayList<Bpd> listFR = new ArrayList<>();
        ArrayList<Bpd> listWP = new ArrayList<>();
        ArrayList<Bpd> listFW = new ArrayList<>();
        for(Bpd o: lbest2bstnr){
            //TODO ausprobieren mit werten.
            if(o.ttyp == null){
                System.out.println("Kann noch nicht nach ttyp sortieren.");
                continue;
            }
            if((o.ttyp).equals("OR")) listOR.add(o);
            if((o.ttyp).equals("FR")) listFR.add(o);
            if((o.ttyp).equals("WP")) listWP.add(o);
            if((o.ttyp).equals("FW")) listFW.add(o);
        }
        Collections.sort(listOR.subList(0,listOR.size()));
        Collections.sort(listFR.subList(0,listFR.size()));
        Collections.sort(listWP.subList(0,listWP.size()));
        Collections.sort(listFW.subList(0,listFW.size()));
        lbest2bstnr.clear();
        for(Bpd o: listOR){
            lbest2bstnr.add(o);
        }
        for(Bpd o: listFR){
            lbest2bstnr.add(o);
        }
        for(Bpd o: listWP){
            lbest2bstnr.add(o);
        }
        for(Bpd o: listFW){
            lbest2bstnr.add(o);
        }
    }
}
