public class Packelement{
    int lagerbestand_bstnr;
    int box_vbnr;
    int vmenge;

    Packelement(int bstnr,int vbnr,int vmenge){
        this.box_vbnr = vbnr;
        this.lagerbestand_bstnr = bstnr;
        this.vmenge = vmenge;
    }

    public void toPrint(){
        System.out.println("Box Bestandsnummer: "+this.box_vbnr+", Lagerbestand Bestandsnummer: "+this.lagerbestand_bstnr+", Menge in der Box: "+this.vmenge);
    }
}
