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
        ArrayList<Bpd> voll = initList(choice);
        System.out.println("Größe von Voll: " + voll.size() + "!");
        for(Bpd o : voll){
            System.out.println("Objekt in LinkedList geschrieben.");
            bpdispo.add(o);
        }
        printList(bpdispo); //Linked List ausgeben, ein Knoten/Objekt pro Zeile.
        ArrayList<FreieBox> lfBO = SQLManager.getFreieBoxen();
        printFreie(lfBO);

        //Maschinelle Disposition
        /*
        int R = 100;

        for(Bpd box : bpdispo){
            //TODO Zeilen in lpos2box einfügen und update auf box tabelle, status auf 2 setzen.
            if(box.algrad > R){
                int anzBox = (box.algrad/100)+1;
                //TODO verpacken nach Schema auf Aufgabenblatt.
            }
            if(box.algrad < R){
                box.verpackt = true;
                R = R - box.algrad;
                //putLagerbestandInBox(box);
            }
            /*TODO wann wird R wieder auf 100 gesetzt?!?!
                tripel (bstnr, vbnr, vmenge) in packliste speichern.

             */
    }
    //TODO 8c)

    private static void putLagerbestandInBox(){

        //SQLManager.addLagerbestandToBox();

    }

    private static void printFreie(ArrayList<FreieBox> leere){
        System.out.println("Anzahl der freien Boxen: "+leere.size());
        ArrayList<FreieBox> fw = new ArrayList<>();
        ArrayList<FreieBox> wp = new ArrayList<>();
        ArrayList<FreieBox> fr = new ArrayList<>();
        ArrayList<FreieBox> or = new ArrayList<>();

        for(FreieBox o: leere){
            //System.out.println("\nBestandnummer der leeren Box: "+o.bestandnummer);
            if((o.versandtyp).equals("FW")){ fw.add(o);}
            if(o.versandtyp.equals("WP")){ wp.add(o);}
            if(o.versandtyp.equals("FR")){ fr.add(o);}
            if(o.versandtyp.equals("OR")){ or.add(o);}
        }

        for(FreieBox o: fw){
            o.toPrint();
        }
        for(FreieBox o: wp){
            o.toPrint();
        }
        for(FreieBox o: fr){
            o.toPrint();
        }
        for(FreieBox o: or){
            o.toPrint();
        }
    }


    public static int makeChoice(){
        Scanner sc = new Scanner(System.in);
        System.out.println("\nGib die Bestellnummer ein, für die du planen willst. ");
        return sc.nextInt();
    }

    public static ArrayList<Bpd> initList(int bestnr){
        /* TODO: Hier noch an die tabelle box eine anfrage stellen mit "where vbstnr = bestnr" um an vbtyp zu kommen.
            algrad = 100, verpackt = false; anzbo = 0;
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
        System.out.println("Anzahl der gefundenen bestellungen: "+bestellungen.size());
        for(int[] ia: bestellungen){
            String ttyp = SQLManager.getVersandTyp(ia[1]);
            String artbez = SQLManager.getArtikel(ia[0]);
            alle.add(new Bpd(
                    artbez,
                    ia[0], // artnr
                    ia[1], //bstnr
                    ia[2], //stuecke
                    false,
                    ttyp,
                    ia[2] % 6//auslastungsgrad test
            ));
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

    public static void sortList(LinkedList<Bpd> bpdispo){
        ArrayList<Bpd> listOR = new ArrayList<>();
        ArrayList<Bpd> listFR = new ArrayList<>();
        ArrayList<Bpd> listWP = new ArrayList<>();
        ArrayList<Bpd> listFW = new ArrayList<>();
        for(Bpd o: bpdispo){
            //TODO ausprobieren von sortieren nach ttyp!

            System.out.println("\nein objekt wird ttyp-liste hinzugefügt.");
            //je nach ttyp der liste zufügen
            if((o.ttyp).equals("OR")){ listOR.add(o); System.out.println("zu OR geaddet");}
            if((o.ttyp).equals("FR")){ listFR.add(o); System.out.println("zu FR geaddet");}
            if((o.ttyp).equals("WP")){ listWP.add(o); System.out.println("zu WP geaddet");}
            if((o.ttyp).equals("FW")){ listFW.add(o); System.out.println("zu FW geaddet");}
        }

        //alle einzelnen Listen nach algrad sortieren FUNKTIONIERT
        Collections.sort(listOR.subList(0,listOR.size()));
        Collections.sort(listFR.subList(0,listFR.size()));
        Collections.sort(listWP.subList(0,listWP.size()));
        Collections.sort(listFW.subList(0,listFW.size()));
        System.out.println("nach algrad sortiert.");

        bpdispo.clear();

        //Listen wieder zusammenfügen
        for(Bpd o: listOR){
            System.out.println("\nvon OR in bpdispo geaddet");
            bpdispo.add(o);
        }
        for(Bpd o: listFR){
            System.out.println("\nvon FR in bpdispo geaddet");
            bpdispo.add(o);
        }
        for(Bpd o: listWP){
            System.out.println("\nvon WP in bpdispo geaddet");
            bpdispo.add(o);
        }
        for(Bpd o: listFW){
            System.out.println("\nvon FW in bpdispo geaddet");
            bpdispo.add(o);
        }
    }
}
