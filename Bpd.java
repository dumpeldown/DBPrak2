public class Bpd implements Comparable{
    int bstnr;
    String ttyp;
    int artnr;
    String artbez;
    int anzbo; //Anzahl der Boxen
    int menge;
    int algrad;
    boolean verpackt;

    /*Bpd(){
        this.algrad = (menge*100)/anzbo+1;
    }
     */

    Bpd(int artnr, int bstnr, int menge){
        this.artnr = artnr;
        this.bstnr = bstnr;
        this.menge = menge;
    }

    @Override
    public int compareTo(Object o){
        //Nach Algrad absteigend sortiert ausgeben.
        return algrad - ((Bpd)o).algrad;
    }

    public void toPrint(){
        System.out.println("\nBestandsnummer: "+this.bstnr+" Transporttyp: "+this.ttyp+" Artikelnummer: "+this.artnr+
                " Artikelbez: "+this.artbez+" Anzahl Boxen: "+this.anzbo+" Menge in Bestellung: "+this.menge+" " +
                "Auslastungsgrad: "+this.algrad+" Verpackt?: "+this.verpackt);
    }
}
