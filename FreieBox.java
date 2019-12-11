public class FreieBox{
    String versandtyp;
    int bestandnummer;

    FreieBox(String versandtyp, int bestandnummer){
        this.versandtyp = versandtyp;
        this.bestandnummer = bestandnummer;
    }

    public void toPrint(){
        System.out.println("\nVersandtyp: "+this.versandtyp+", Bestandsnummer in Tabelle Box: "+this.bestandnummer);
    }
}
