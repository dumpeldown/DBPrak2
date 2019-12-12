public class FreieBox{
    String versandtyp;
    int bestandnummer;
    int restplatz;

    FreieBox(String versandtyp, int bestandnummer){
        this.versandtyp = versandtyp;
        this.bestandnummer = bestandnummer;
        this.restplatz = 101;
    }

    public void toPrint(){
        System.out.println("\nVersandtyp: "+this.versandtyp+", Bestandsnummer in Tabelle Box: "+this.bestandnummer+"," +
                " Restfuellmenge in Prozent: "+(this.restplatz-1));
    }
}
