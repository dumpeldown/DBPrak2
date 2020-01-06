import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Scanner;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

public class Lieferschein{
    int knr, kplz;
    String kname, kort, kstrasse;

    int bestellnr;
    Date bestelldat;

    ArrayList<int[]> lagerbest = new ArrayList<>();
    ArrayList<String> artbez = new ArrayList<>();

    int verwOR = 0, verwFR = 0, verwWP = 0, verwFW = 0;

    Lieferschein(int bestnr){

        ResultSet rs = SQLManager.getKundeFromBestnr(bestnr);
        this.bestellnr = bestnr;
        try{
            while(rs.next()){
                this.knr = rs.getInt("knr");
                this.kplz = rs.getInt("plz");
                this.kname = rs.getString("kname");
                this.kort = rs.getString("ort");
                this.kstrasse = rs.getString("strasse");
            }
            rs = SQLManager.getLagerbeste(bestnr);
            while(rs.next()){
                int artnr = rs.getInt("artnr");
                lagerbest.add(new int[]{artnr, rs.getInt("lnr"), rs.getInt("stuecke"), rs.getInt("wert"), rs.getInt(
                        "bstnr")});
                artbez.add(SQLManager.getArtikelBez(artnr));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        this.bestelldat = SQLManager.getBestellDatum(bestnr);

        ArrayList<String> versandtypen = SQLManager.getBoxenByBestellnr(bestnr);
        for(String s : versandtypen){
            if(s.equals("OR")){
                verwOR++;
            }
            if(s.equals("FR")){
                verwFR++;
            }
            if(s.equals("WP")){
                verwWP++;
            }
            if(s.equals("FW")){
                verwFW++;
            }
        }
    }

    public static void initLieferschein(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Fuer welche Bestellung willst du einen Lieferschein erstellen?");

        int bestnr = sc.nextInt();
        if(!SQLManager.isBestellungVerpackt(bestnr)){
            System.out.println("\nDie eingegebene Bestellnummer ist nicht gueltig oder wurde noch nicht verpackt.");
            return;
        }
        Lieferschein lf = new Lieferschein(bestnr);
        System.out.println("Wie willst du den Lieferschein erstellen?");
        System.out.println(
                "1) Auf der Konsole ausgeben.\n" +
                        "2) Als XML Datei.\n" +
                        "3) Als txt.\n" +
                        "4)Alle 3 Medien des LF's erstellen.\n");
        int ausw = sc.nextInt();
        switch(ausw){
            case 1:{
                lf.toCon();
                break;
            }
            case 2:{
                lf.toXML();
                break;
            }
            case 3:{
                lf.toTXT();
                break;
            }
            case 4:{
                lf.toCon();
                lf.toXML();
                lf.toTXT();
                break;
            }
        }
    }

    public void toCon(){
        int i = 0;
        //System.out.println("\n[!!!!]Anzahl der sonderfaelle: " + Versandplanung.andersVerpackt.size());
        //System.out.println("\n[!!!!]Anzahl der sonderfaelle: " + Versandplanung.andersVerpackt.size());
        System.out.println("#########LIEFERSCHEIN############\n");
        System.out.println("Kunde: " + this.kname + "  " + this.kstrasse + " " + this.kplz + "  " + this.kort + "\n");
        System.out.println("Bestellte Artikel: " + lagerbest.size() + "\n");
        System.out.println("Insgesamt verwendet wurden: \n" +
                "   " + verwOR + "x ORDINARY\n" +
                "   " + verwFR + "x FRAGILE\n" +
                "   " + verwWP + "x WATERPROOF\n" +
                "   " + verwFW + "x FRAGILE&WATERPROOF\n");
        for(int[] arr : lagerbest){
            System.out.println((i + 1) + "). TOTAL: " + arr[3] + "€\n    " + arr[2] + "x Artikel: " + this.artbez.get(i) + " " +
                    "(Artikelnr: " + arr[0] + "), versandt aus Lager " + arr[1] + " ");
            i++;
        }
        if(Versandplanung.andersVerpackt.size() != 0){
            i = 0;
            System.out.println("\nAnders verpackt wurde/n:");
            for(int[] arr : lagerbest){
                if(Versandplanung.andersVerpackt.containsKey(arr[4])){
                    String[] strarr = Versandplanung.andersVerpackt.get(arr[4]);
                    System.out.println("\n" + "   " + (i + 1) + "). " + strarr[0] + ", eigentlich " + strarr[1] + ", " +
                            "jetzt " + strarr[2]);
                }
            }
        }
        else if((verwFW + verwWP + verwFR + verwOR) == 0){
            System.out.println("\nEs konnte keine Bestellung verschickt werden, da keine passende/n Box/en gefunden " +
                    "wurde/n\n");
        }
        else{
            System.out.println("\nAlle Bestellungen wurden mit dem richtigen Versandtyp verpackt.");
        }
    }

    public void toXML(){
        try{

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("lieferung");
            Attr attr = document.createAttribute("bestnr");
            attr.setValue(String.valueOf(this.bestellnr));
            root.setAttributeNode(attr);
            document.appendChild(root);

            //kundendetails
            Element kundendetails = document.createElement("kundendetails");
            root.appendChild(kundendetails);

            Element kname = document.createElement("kname");
            kname.appendChild(document.createTextNode(this.kname));
            kundendetails.appendChild(kname);

            Element kstrasse = document.createElement("kstrasse");
            kstrasse.appendChild(document.createTextNode(this.kstrasse));
            kundendetails.appendChild(kstrasse);

            Element kplz = document.createElement("kplz");
            kplz.appendChild(document.createTextNode(String.valueOf(this.kplz)));
            kundendetails.appendChild(kplz);

            Element kort = document.createElement("kort");
            kort.appendChild(document.createTextNode(this.kort));
            kundendetails.appendChild(kort);

            Element positionen = document.createElement("positionen");
            root.appendChild(positionen);

            int i = 0;
            for(int[] arr : lagerbest){
                Element position = document.createElement("position");
                positionen.appendChild(position);

                Element artbez = document.createElement("artbez");
                artbez.appendChild(document.createTextNode(this.artbez.get(i))); // i einsetzen
                position.appendChild(artbez);

                Element artnr = document.createElement("artnr");
                artnr.appendChild(document.createTextNode(String.valueOf(arr[0]))); // i anstatt
                // erster 0
                position.appendChild(artnr);

                Element stuecke = document.createElement("stuecke");
                stuecke.appendChild(document.createTextNode(String.valueOf(arr[2]))); // i anstat 0
                position.appendChild(stuecke);

                Element total = document.createElement("total");
                total.appendChild(document.createTextNode(String.valueOf(arr[3])));
                position.appendChild(total);
                i++;
            }

            //Boxen element
            Element boxen = document.createElement("boxen");
            root.appendChild(boxen);

            Element ordinary = document.createElement("ordinary");
            ordinary.appendChild(document.createTextNode(String.valueOf(this.verwOR)));
            boxen.appendChild(ordinary);

            Element fragile = document.createElement("fragile");
            fragile.appendChild(document.createTextNode(String.valueOf(this.verwFR)));
            boxen.appendChild(fragile);

            Element waterproof = document.createElement("waterproof");
            waterproof.appendChild(document.createTextNode(String.valueOf(this.verwWP)));
            boxen.appendChild(waterproof);

            Element fr_and_wp = document.createElement("fr_and_wp");
            fr_and_wp.appendChild(document.createTextNode(String.valueOf(this.verwFW)));
            boxen.appendChild(fr_and_wp);

            Element sonderfaelle = document.createElement("sonderfaelle");
            root.appendChild(sonderfaelle);


            for(int[] arr : lagerbest){
                    if(Versandplanung.andersVerpackt.containsKey(arr[4])){
                        String[] strarr = Versandplanung.andersVerpackt.get(arr[4]);
                        Element fall = document.createElement("fall");
                        sonderfaelle.appendChild(fall);

                        Element artbez = document.createElement("artbez");
                        artbez.appendChild(document.createTextNode(String.valueOf(strarr[0])));
                        fall.appendChild(artbez);

                        Element eigtl = document.createElement("eigentlich");
                        eigtl.appendChild(document.createTextNode(String.valueOf(strarr[1])));
                        fall.appendChild(eigtl);

                        Element tats = document.createElement("tatsaechlich");
                        tats.appendChild(document.createTextNode(String.valueOf(strarr[2])));
                        fall.appendChild(tats);
                    }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(".\\LIEF" + this.bestellnr + ".XML"));
            //streamResult = new StreamResult(System.out);

            transformer.transform(domSource, streamResult);

            System.out.println("\n[!!!!]Anzahl der sonderfaelle: " + Versandplanung.andersVerpackt.size());
            System.out.println("XML Dokument erstellt.");

        }catch(ParserConfigurationException pce){
            pce.printStackTrace();
        }catch(TransformerException tfe){
            tfe.printStackTrace();
        }
    }

    public void toTXT(){
        BufferedWriter bf = null;
        try{
            bf = new BufferedWriter(new FileWriter(".\\LIEF"+this.bestellnr+".txt"));
        }catch(IOException e){
            e.printStackTrace();
        }

        int i = 0;
        try{
            bf.write("#########LIEFERSCHEIN############\n");
            bf.write("Kunde: " + this.kname + "  " + this.kstrasse + " " + this.kplz + "  " + this.kort + "\n");
            bf.write("Bestellte Artikel: " + lagerbest.size() + "\n");
            bf.write("Insgesamt verwendet wurden: \n" +
                    "   " + verwOR + "x ORDINARY\n" +
                    "   " + verwFR + "x FRAGILE\n" +
                    "   " + verwWP + "x WATERPROOF\n" +
                    "   " + verwFW + "x FRAGILE&WATERPROOF\n");
            for(int[] arr : lagerbest){
                bf.write("\n"+(i + 1) + "). TOTAL: " + arr[3] + "€\n    " + arr[2] + "x Artikel: " + this.artbez.get(i)+
                        "(Artikelnr: " + arr[0] + "), versandt aus Lager " + arr[1] + " ");
                i++;
            }
            if(Versandplanung.andersVerpackt.size() != 0){
                i = 0;
                bf.write("\n\nAnders verpackt wurde/n:");
                for(int[] arr : lagerbest){
                    if(Versandplanung.andersVerpackt.containsKey(arr[4])){
                        String[] strarr = Versandplanung.andersVerpackt.get(arr[4]);
                        bf.write("\n" + "   " + (i + 1) + "). " + strarr[0] + ", eigentlich " + strarr[1] + ", " +
                                "jetzt " + strarr[2]);
                    }
                }
            }
            else if((verwFW + verwWP + verwFR + verwOR) == 0){
                bf.write("\nEs konnte keine Bestellung verschickt werden, da keine passende/n Box/en gefunden " +
                        "wurde/n\n");
            }
            else{
                bf.write("\nAlle Bestellungen wurden mit dem richtigen Versandtyp verpackt.");
            }
            bf.flush();
            bf.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}