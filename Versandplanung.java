import java.util.*;


public class Versandplanung{
    public static int[] freieBoxtype = new int[4];

    public static void plan(){
        LinkedList<Bpd> bpdispo = new LinkedList<>();
        ArrayList<Packelement> packliste = new ArrayList<>();
        ArrayList<Bpd> voll;
        ArrayList<FreieBox> lfBO;
        int choice;

        SQLManager.printAllBestellungen(); //alle bestellungen mit status 1 werden
        // angezeigt.
        do{
            choice = makeChoice();
            if(!SQLManager.isValid("lagerbestand", "bestnr", choice)){
                System.out.println("Die ausgewählte Bestellnummer ist in der Lagerbestand-Tabelle nicht eingetragen.");
            }
            if(choice == 0) return;
        }while(!SQLManager.isValid("lagerbestand", "bestnr", choice) || choice == -1);

        voll = initList(choice);

        for(Bpd o : voll){
            System.out.println("Objekt in LinkedList geschrieben.");
            bpdispo.add(o);
        }

        printList(bpdispo); //Linked List ausgeben, ein Knoten/Objekt pro Zeile.
        lfBO= SQLManager.getFreieBoxen();
        lfBO = sortAndPrintFreie(lfBO);

        ArrayList<Bpd> zuloeschen = new ArrayList<>();
        System.out.println("Anzahl der elemente in bpdispo: "+bpdispo.size());
        for(Bpd o: bpdispo){
            if((o.algrad-1) > 100){
                System.out.println("Position mit algrad > 100 wird verpackt.");
                verpacken(lfBO, o, packliste);
                zuloeschen.add(o);
            }else{
                System.out.println("position gefunden, wo algrad < 100, aber erstmal übersprungen.");
            }

        }


        for(Bpd o: zuloeschen){
            bpdispo.remove(o);
        }
        zuloeschen.clear();

        ArrayList<Bpd> kleineor = new ArrayList<>();
        ArrayList<Bpd> kleinefr = new ArrayList<>();
        ArrayList<Bpd> kleinewp = new ArrayList<>();
        ArrayList<Bpd> kleinefw = new ArrayList<>();


        System.out.println("Anzahl der positionen mit algrad < 100: "+bpdispo.size());

        for(Bpd o: bpdispo){
            System.out.println("position gefunden, mit algrad <= 100");
            o.toPrint();

            if(o.ttyp.equals("OR")){
                System.out.println("position gefunden mit ttyp OR");
                kleineor.add(o);
                zuloeschen.add(o);
            }
            if(o.ttyp.equals("FR")){
                System.out.println("position gefunden mit ttyp FR");
                kleinefr.add(o);
                zuloeschen.add(o);

            }
            if(o.ttyp.equals("WP")){
                System.out.println("position gefunden mit ttyp WP");
                kleinewp.add(o);
                zuloeschen.add(o);
            }
            if(o.ttyp.equals("FW")){
                System.out.println("position gefunden mit ttyp FW");
                kleinefw.add(o);
                zuloeschen.add(o);
            }
        }
        for(Bpd o: zuloeschen){
            bpdispo.remove(o);
        }

        System.out.println("Anzahl der Position mit tyyp OR: "+kleineor.size());
        System.out.println("Anzahl der Position mit tyyp FR: "+kleinefr.size());
        System.out.println("Anzahl der Position mit tyyp WP: "+kleinewp.size());
        System.out.println("Anzahl der Position mit tyyp FW: "+kleinefw.size());

        //TODO MUSS AUCH FÜR 2 BOXEN FUNKTIONIEREN; DIE BEIDE ALGRAD < 100 ABER NICHT ZSM IN EINE BOX.
        //eventuell gefixt, ausprobieren.


        sortAndPack(kleineor, lfBO, packliste);
        sortAndPack(kleinefr, lfBO, packliste);
        sortAndPack(kleinefw, lfBO, packliste);
        sortAndPack(kleinewp, lfBO, packliste);

        System.out.println("elemente in packliste: "+packliste.size());
        for(Packelement pe: packliste){
            pe.toPrint();
        }


        SQLManager.writePackliste(packliste);
        for(Packelement el : packliste){
            SQLManager.setBoxenUsed(el.lagerbestand_bstnr, el.box_vbnr);
        }

        SQLManager.updateStatusOnBestellung(choice);
        //Lieferschein.print(choice);

    }

    public static void sortAndPack(ArrayList<Bpd> zuVersenden, ArrayList<FreieBox> lfBO,
                                   ArrayList<Packelement> packliste){
        if(zuVersenden.size() == 0){
            return;
        }
        Collections.sort(zuVersenden);
        do{
            int summe = 0;
            int anzBox = 1;
            ArrayList<Bpd> zuverpacken = new ArrayList<>();
            for(Bpd obj_or : zuVersenden){
                System.out.println("Checken auf summe der einzelnen positionen: summe = "+summe+" und algrad " +
                        "von diesem Objekt = "+(obj_or.algrad-1));
                if(((summe += (obj_or.algrad - 1)) <= 100)){
                    System.out.println("Eine die selbe box kann gepackt werden:" +obj_or.artbez);
                    zuverpacken.add(obj_or);
                }
                else{
                    System.out.println("Das naechste obj kann nicht in die selbe box.");
                    anzBox++;
                    break;
                }
            }
            System.out.println("Anzahl der zu verpackenden position = "+zuverpacken.size());
            for(Bpd obj_or_rm : zuverpacken){
                zuVersenden.remove(obj_or_rm);
            }
            if(zuverpacken.size() > 0){
                verpackenEinfach(lfBO, zuverpacken, packliste);
            }else{
                System.out.println("HIER wurde versucht EINE LISTE MIT 0 el zu übergeben.");
            }

            zuverpacken.clear();
            System.out.println("Anzahl der noch zu verpackenden erlemente: "+zuVersenden.size());
            if(zuVersenden.size() == 0){
                return;
            }
        }while(!zuVersenden.isEmpty());
    }


    private static void verpacken(ArrayList<FreieBox> freie, Bpd zuVersenden,
                                  ArrayList<Packelement> packliste){
        if(zuVersenden == null) return;

        String ttyp = zuVersenden.ttyp;


        System.out.println("\nZu versenden ist ttyp: "+ttyp);
        System.out.println("Auslastungsgrad für den Artikel: "+(zuVersenden.algrad-1));

        int anzbox = (int) Math.ceil(zuVersenden.algrad/100.0);
        System.out.println("Anzahl der benötigten Boxen: "+anzbox);

        FreieBox[] notwendige = new FreieBox[anzbox];

        int i = 0;
        for(FreieBox fr : freie){
            if(ttyp.equals("OR")){
                if(freieBoxtype[0] < 1){
                    System.out.println("boxtypsuche von or nach fr geändert, da keine or boxen mehr borhanden sind.");
                    ttyp = "FR";
                }
            }if(ttyp.equals("FR")){
                if(freieBoxtype[1] < 1){
                    System.out.println("boxtypsuche von fr nach wp geändert, da keine fr boxen mehr vorhanden sind.");
                    ttyp = "WP";
                }
            }
            if(ttyp.equals("WP")){
                if(freieBoxtype[2] < 1){
                    System.out.println("boxtypsuche von wp nach fw geändert, da keine wp boxen mehr vorhanden sind.");
                    ttyp = "FW";
                }
            }
            if(ttyp.equals("FR")){
                if(freieBoxtype[3] < 1){
                    System.out.println("ÜBERHAUPTKEINE FREIEN BOXEN MEHR. xD");
                    return;
                }
            }

            if(i == anzbox) break;
            System.out.println("ttyp = "+ttyp+" und versandtyp der zu vergleichenden box = "+fr.versandtyp);

            if(ttyp.equals(fr.versandtyp)){
                System.out.println("Box mit richtigem Typ gefunden und in notwendige array hinzugefügt.");
                notwendige[i] = fr;
                i++;
            }else{
                System.out.println("FEHLER!!");
                return;
            }
        }


        i = 0;
        System.out.println("Anzahl der bereitgestellten Boxen: "+notwendige.length+"!");
        for(int j = 0; j < notwendige.length; j++){
            if(notwendige[j].equals("OR")) freieBoxtype[0]--;
            if(notwendige[j].equals("FR")) freieBoxtype[1]--;
            if(notwendige[j].equals("WP")) freieBoxtype[2]--;
            if(notwendige[j].equals("FW")) freieBoxtype[3]--;
        }

        //jetzt sachen in box verpacken:
        do{
            Packelement verpackt;

            if((zuVersenden.menge - zuVersenden.anzbo) > 0){
                verpackt = new Packelement(zuVersenden.bstnr, notwendige[i].bestandnummer,
                        zuVersenden.anzbo);
                zuVersenden.menge -= zuVersenden.anzbo;
            }else{
                verpackt = new Packelement(zuVersenden.bstnr, notwendige[i].bestandnummer,
                        zuVersenden.menge);
            }

            zuVersenden.algrad -= 100;

            freie.remove(notwendige[i]);
            packliste.add(verpackt);
            i++;
        }while(zuVersenden.algrad > 0);
    }

    private static void verpackenEinfach(ArrayList<FreieBox> freie, ArrayList<Bpd> zuVersenden,
                                         ArrayList<Packelement> packliste){

        FreieBox versandbox = null;
        String ttyp = zuVersenden.get(0).ttyp;
        for(FreieBox fr : freie){
            if(ttyp.equals(fr.versandtyp)){
                versandbox = fr;
                break;
            }else{
                //TODO Auf andere Versandbox typen ausweichen, die auch genommen weden können.
            }
        }

        System.out.println("Es können "+ zuVersenden.size()+" Positionen in eine Box gepackt werden.");

        for(Bpd o : zuVersenden){
            packliste.add(new Packelement(o.bstnr,versandbox.bestandnummer, o.menge));
        }
        freie.remove(versandbox);
    }


    private static ArrayList<FreieBox> sortAndPrintFreie(ArrayList<FreieBox> leere){
        System.out.println("Anzahl der freien Boxen: "+leere.size());
        ArrayList<FreieBox> fw = new ArrayList<>();
        ArrayList<FreieBox> wp = new ArrayList<>();
        ArrayList<FreieBox> fr = new ArrayList<>();
        ArrayList<FreieBox> or = new ArrayList<>();
        ArrayList<FreieBox> alle = new ArrayList<>();

        for(FreieBox o: leere){
            if((o.versandtyp).equals("FW")){ fw.add(o);}
            if(o.versandtyp.equals("WP")){ wp.add(o);}
            if(o.versandtyp.equals("FR")){ fr.add(o);}
            if(o.versandtyp.equals("OR")){ or.add(o);}
        }
        int anzFreieOR = or.size();
        int anzFreieFR = fr.size();
        int anzFreieWP = wp.size();
        int anzFreieFW = fw.size();

        System.out.println("Anzahl der freien Boxen vom Typ OR: "+anzFreieOR);
        System.out.println("Anzahl der freien Boxen vom Typ FR: "+anzFreieFR);
        System.out.println("Anzahl der freien Boxen vom Typ WP: "+anzFreieWP);
        System.out.println("Anzahl der freien Boxen vom Typ FW: "+anzFreieFW);
        freieBoxtype[0] = anzFreieOR;
        freieBoxtype[1] = anzFreieFR;
        freieBoxtype[2] = anzFreieWP;
        freieBoxtype[3] = anzFreieFW;

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

        for(FreieBox o: or){
            alle.add(o);
        }
        for(FreieBox o: fr){
            alle.add(o);
        }
        for(FreieBox o: wp){
            alle.add(o);
        }
        for(FreieBox o: fw){
            alle.add(o);
        }
        return alle;
    }


    public static int makeChoice(){
        Scanner sc = new Scanner(System.in);
        System.out.println("\nGib eine gültige Bestellnummer ein, für die du planen willst. (0 für Abbruch)");
        try{
            return sc.nextInt();
        }catch(InputMismatchException e){
            System.out.println("[!!] Fehler bei der Eingabe.");
            return -1;
        }
    }

    public static ArrayList<Bpd> initList(int bestnr){
        ArrayList<Bpd> alle = new ArrayList<>();
        ArrayList<int[]> bestellungen = SQLManager.getBestellung(bestnr);
        //System.out.println("Anzahl der gefundenen bestellungen: "+bestellungen.size());

        for(int[] ia: bestellungen){
            String ttyp = SQLManager.getVersandTyp(ia[1]);
            String artbez = SQLManager.getArtikelBez(ia[0]);
            // anzbo gibt an, wie viel Prozent Platz ein Artikel in einer Box
            // einnimmt.

            int anzbo = SQLManager.getArtikelAnzbox(ia[0]);
            alle.add(new Bpd(
                    artbez,
                    ia[0], // artnr
                    ia[1], //bstnr
                    ia[2], //stuecke
                    false,
                    ttyp,
                    anzbo
            ));
        }
        return alle;
    }

    public static void printList(LinkedList<Bpd> bpdispo){
        sortList(bpdispo);
        //System.out.println("\n Anzahl der Objecte in bpdispo: "+bpdispo.size());
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
