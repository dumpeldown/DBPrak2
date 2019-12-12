import java.util.*;


public class Versandplanung{
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
        printFreie(lfBO);

        for(Bpd o: bpdispo){
            if((o.algrad-1)> 100){
                System.out.println("IN VERPACKEN GESPRUNGEN, ALSO ALGRAD GROE?ER 100");
                verpacken(lfBO, o, packliste);
                bpdispo.remove(o);
            }
        }

        ArrayList<Bpd> kleineor = new ArrayList<>();
        ArrayList<Bpd> kleinefr = new ArrayList<>();
        ArrayList<Bpd> kleinewp = new ArrayList<>();
        ArrayList<Bpd> kleinefw = new ArrayList<>();
        //TODO MUSS AUCH FÜR 2 BOXEN FUNKTIONIEREN; DIE BEIDE ALGRAD < 100 ABER NICHT ZSM IN EINE BOX.
        for(Bpd o: bpdispo){
            if(o != null){
                if(o.ttyp.equals("OR")){
                    kleineor.add(o);
                    bpdispo.remove(o);
                }
                if(o.ttyp.equals("FR")){
                    kleinefr.add(o);
                    bpdispo.remove(o);
                }
                if(o.ttyp.equals("WP")){
                    kleinewp.add(o);
                    bpdispo.remove(o);
                }
                if(o.ttyp.equals("FW")){
                    kleinefw.add(o);
                    bpdispo.remove(o);
                }
            }

            if(!kleineor.isEmpty()){
                Collections.sort(kleineor);
                do{
                    int summe = 0;
                    ArrayList<Bpd> zuverpacken = new ArrayList<>();
                    for(Bpd obj_or : kleineor){
                        if(obj_or != null){
                            if(((summe += (obj_or.algrad - 1)) <= 100)){
                                zuverpacken.add(obj_or);
                            }
                            else{
                                break;
                            }
                        }
                    }
                    for(Bpd obj_or_rm : zuverpacken){
                        kleineor.remove(obj_or_rm);
                    }
                    verpackenEinfach(lfBO, zuverpacken, packliste);
                    zuverpacken.clear();
                }
                while(!kleineor.isEmpty());
            }

            if(!kleinefr.isEmpty()){
                System.out.println("element in kleinefr erkannt.");
                Collections.sort(kleinefr);
                do{
                    int summe = 0;
                    ArrayList<Bpd> zuverpacken = new ArrayList<>();
                    for(Bpd obj_fr : kleinefr){
                        if(obj_fr != null){
                            if(((summe += (obj_fr.algrad - 1)) <= 100)){
                                zuverpacken.add(obj_fr);
                                System.out.println("element zu 'zuverpacken' hinzugefügt.!!");
                            }
                            else{
                                break;
                            }
                        }
                    }
                    for(Bpd obj_fr_rm : zuverpacken){
                        kleinefr.remove(obj_fr_rm);
                    }
                    verpackenEinfach(lfBO, zuverpacken, packliste);
                    zuverpacken.clear();
                }
                while(!kleinefr.isEmpty());
            }

            if(!kleinefw.isEmpty()){
                Collections.sort(kleinewp);
                do{
                    int summe = 0;
                    ArrayList<Bpd> zuverpacken = new ArrayList<>();
                    for(Bpd obj_wp : kleinewp){
                        if(obj_wp != null){
                            if((summe += obj_wp.algrad) <= 100){
                                zuverpacken.add(obj_wp);
                            }
                            else{
                                break;
                            }
                        }
                    }
                    for(Bpd obj_wp_rm : zuverpacken){
                        kleinewp.remove(obj_wp_rm);
                    }
                    verpackenEinfach(lfBO, zuverpacken, packliste);
                    zuverpacken.clear();
                }
                while(!kleinewp.isEmpty());
            }

            if(!kleinefw.isEmpty()){
                Collections.sort(kleinefw);
                do{
                    int summe = 0;
                    ArrayList<Bpd> zuverpacken = new ArrayList<>();
                    for(Bpd obj_fw : kleinefw){
                        if(obj_fw != null){
                            if((summe += obj_fw.algrad) <= 100){
                                zuverpacken.add(obj_fw);
                            }
                            else{
                                break;
                            }
                        }
                    }
                    for(Bpd obj_fw_rm : zuverpacken){
                        kleinefw.remove(obj_fw_rm);
                    }
                    verpackenEinfach(lfBO, zuverpacken, packliste);
                    zuverpacken.clear();
                }
                while(!kleinefw.isEmpty());
            }
        }


        for(Packelement pe: packliste){
            pe.toPrint();
        }
        SQLManager.writePackliste(packliste);
        for(Packelement el : packliste){
            SQLManager.setBoxenUsed(el.lagerbestand_bstnr, el.box_vbnr);
        }

        SQLManager.updateStatusOnBestellung(choice);
    }

    /**
     * Gibt Versandbox zurück, wenn dort noch Platz ist, sonst NULL.
     */
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
                if(i == anzbox) break;
                System.out.println("ttyp = "+ttyp+" und versandtyp der zu vergleichenden box = "+fr.versandtyp);
                if(ttyp.equals(fr.versandtyp)){
                    System.out.println("Box mit richtigem Typ gefunden und in notwendige array hinzugefügt.");
                    notwendige[i] = fr;
                    i++;
                }else{
                    /*TODO Auf andere Versandbox typen ausweichen, die auch genommen weden können.
                        am besten einem typ von box eine wertigkeit (int) zuweisen.

                     */
                }
            }


            i = 0;
            System.out.println("Anzahl der bereitgestellten Boxen: "+notwendige.length+"!");

            //jetzt sachen in box verpacken:
            do{
                Packelement verpackt;

                    verpackt = new Packelement(zuVersenden.bstnr, notwendige[i].bestandnummer,
                                zuVersenden.menge/anzbox);
                    //TODO welche Rechung ist richtig??
                    //verpackt = new Packelement(zuVersenden.get(0).bstnr, notwendige[i].bestandnummer,
                            //zuVersenden.get(0).menge-(zuVersenden.get(0).anzbo*(notwendige.length-1)));

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


    private static void printFreie(ArrayList<FreieBox> leere){
        System.out.println("Anzahl der freien Boxen: "+leere.size());
        ArrayList<FreieBox> fw = new ArrayList<>();
        ArrayList<FreieBox> wp = new ArrayList<>();
        ArrayList<FreieBox> fr = new ArrayList<>();
        ArrayList<FreieBox> or = new ArrayList<>();

        for(FreieBox o: leere){
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
