public class Bpd implements Comparable{
    int bstnr;
    String ttyp;
    int artnr;
    String artbez;
    int anzbo; //Attribut aus Artikel, gibt an, wie viel Platz in Prozent ein Artikel in einer Box einnimmt.
    int menge;
    int algrad;
    boolean verpackt;

    Bpd(String artbez, int artnr, int bstnr, int menge, boolean verpackt, String ttyp, int anzbo){
        this.artbez = artbez;
        this.artnr = artnr;
        this.bstnr = bstnr;
        this.menge = menge;
        this.verpackt = verpackt;
        this.ttyp = ttyp;
        this.anzbo = anzbo;
        this.algrad = ((menge*100)/this.anzbo)+1;
        System.out.println("menge: "+this.menge+", anzbo: "+this.anzbo+" ergibt algrad: "+this.algrad);
    }

    @Override
    public int compareTo(Object o){
        //Nach Algrad absteigend sortiert ausgeben.
        return algrad - ((Bpd)o).algrad;
    }

    public void toPrint(){
        System.out.println("\nBestandsnummer: "+this.bstnr+" Transporttyp: "+this.ttyp+" Artikelnummer: "+this.artnr+
                " Artikelbez: "+this.artbez+" Anzahl Boxen: "+this.anzbo+" Menge in Bestellung: "+this.menge+" " +
                "Auslastungsgrad: "+(this.algrad-1)+" Verpackt?: "+this.verpackt);
    }
}
